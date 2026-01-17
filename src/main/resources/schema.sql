-- Digital Bank - Database Schema
-- Database: H2 (PostgreSQL compatible mode)

-- Temporarily disable referential integrity for cleanup
SET REFERENTIAL_INTEGRITY FALSE;

-- Drop tables in reverse order of dependencies
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS bank_transaction;
DROP TABLE IF EXISTS customer;

-- Drop sequences if they exist
DROP SEQUENCE IF EXISTS customer_seq;
DROP SEQUENCE IF EXISTS transaction_seq;
DROP SEQUENCE IF EXISTS notification_seq;

-- Create sequences for ID generation
CREATE SEQUENCE customer_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE transaction_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE notification_seq START WITH 1 INCREMENT BY 50;

-- Customer table
CREATE TABLE customer (
    id BIGINT DEFAULT NEXT VALUE FOR customer_seq PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    balance DECIMAL(19,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0
);

COMMENT ON TABLE customer IS 'Digital bank customers';
COMMENT ON COLUMN customer.name IS 'Customer full name';
COMMENT ON COLUMN customer.email IS 'Unique customer email';
COMMENT ON COLUMN customer.cpf IS 'Unique customer CPF (format: 000.000.000-00)';
COMMENT ON COLUMN customer.balance IS 'Current account balance (19 digits, 2 decimals)';
COMMENT ON COLUMN customer.version IS 'Version control for optimistic locking';

-- Bank Transaction table
CREATE TABLE bank_transaction (
    id BIGINT DEFAULT NEXT VALUE FOR transaction_seq PRIMARY KEY,
    origin_customer_id BIGINT NOT NULL,
    destination_customer_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL CHECK (amount > 0),
    status VARCHAR(20) DEFAULT 'PENDING',
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    error_message VARCHAR(500),
    description VARCHAR(255),
    version INTEGER DEFAULT 0,

    CONSTRAINT fk_origin_customer
        FOREIGN KEY (origin_customer_id)
        REFERENCES customer(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_destination_customer
        FOREIGN KEY (destination_customer_id)
        REFERENCES customer(id)
        ON DELETE CASCADE
);

COMMENT ON TABLE bank_transaction IS 'Bank transaction records';
COMMENT ON COLUMN bank_transaction.origin_customer_id IS 'ID of the origin customer';
COMMENT ON COLUMN bank_transaction.destination_customer_id IS 'ID of the destination customer';
COMMENT ON COLUMN bank_transaction.amount IS 'Transaction amount (positive)';
COMMENT ON COLUMN bank_transaction.status IS 'Status: PENDING, COMPLETED, FAILED, CANCELLED';
COMMENT ON COLUMN bank_transaction.description IS 'Optional transaction description';

-- Notification table
CREATE TABLE notification (
    id BIGINT DEFAULT NEXT VALUE FOR notification_seq PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    bank_transaction_id BIGINT,
    message VARCHAR(1000) NOT NULL,
    notification_type VARCHAR(50) DEFAULT 'TRANSFER',
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_sent BOOLEAN DEFAULT FALSE,

    CONSTRAINT fk_notification_customer
        FOREIGN KEY (customer_id)
        REFERENCES customer(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_notification_transaction
        FOREIGN KEY (bank_transaction_id)
        REFERENCES bank_transaction(id)
        ON DELETE SET NULL
);

COMMENT ON TABLE notification IS 'Customer notifications';
COMMENT ON COLUMN notification.notification_type IS 'Type: TRANSFER_RECEIVED, TRANSFER_SENT, TRANSFER_FAILED, ALERT, SYSTEM, PROMOTIONAL';

-- Performance indexes
CREATE INDEX idx_customer_email ON customer(email);
CREATE INDEX idx_customer_cpf ON customer(cpf);
CREATE INDEX idx_customer_created ON customer(created_at);

CREATE INDEX idx_transaction_origin ON bank_transaction(origin_customer_id);
CREATE INDEX idx_transaction_destination ON bank_transaction(destination_customer_id);
CREATE INDEX idx_transaction_date ON bank_transaction(transaction_date);
CREATE INDEX idx_transaction_status ON bank_transaction(status);
CREATE INDEX idx_transaction_amount ON bank_transaction(amount);

CREATE INDEX idx_notification_customer ON notification(customer_id);
CREATE INDEX idx_notification_sent ON notification(is_sent);
CREATE INDEX idx_notification_type ON notification(notification_type);
CREATE INDEX idx_notification_date ON notification(sent_at);

-- Validation constraints
ALTER TABLE customer ADD CONSTRAINT chk_balance_positive
    CHECK (balance >= 0);

ALTER TABLE customer ADD CONSTRAINT chk_cpf_format
    CHECK (cpf ~ '^[0-9]{11}$');

ALTER TABLE customer ADD CONSTRAINT chk_email_format
    CHECK (email ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$');

ALTER TABLE bank_transaction ADD CONSTRAINT chk_amount_positive
    CHECK (amount > 0);

ALTER TABLE bank_transaction ADD CONSTRAINT chk_valid_status
    CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED', 'PROCESSING'));

-- Re-enable referential integrity
SET REFERENTIAL_INTEGRITY TRUE;

-- Create a view for customer summary
CREATE VIEW customer_summary AS
SELECT
    c.id,
    c.name,
    c.email,
    c.cpf,
    c.balance,
    c.created_at,
    COUNT(DISTINCT t_out.id) AS total_outgoing_transactions,
    COUNT(DISTINCT t_in.id) AS total_incoming_transactions,
    COALESCE(SUM(CASE WHEN t_out.status = 'COMPLETED' THEN t_out.amount ELSE 0 END), 0) AS total_outgoing_amount,
    COALESCE(SUM(CASE WHEN t_in.status = 'COMPLETED' THEN t_in.amount ELSE 0 END), 0) AS total_incoming_amount,
    COUNT(DISTINCT n.id) AS total_notifications
FROM customer c
LEFT JOIN bank_transaction t_out ON c.id = t_out.origin_customer_id
LEFT JOIN bank_transaction t_in ON c.id = t_in.destination_customer_id
LEFT JOIN notification n ON c.id = n.customer_id
GROUP BY c.id, c.name, c.email, c.cpf, c.balance, c.created_at;
