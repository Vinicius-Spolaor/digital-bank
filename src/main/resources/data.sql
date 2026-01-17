-- Digital Bank - Database Schema
-- Pre-populated customers and sample data

-- Insert initial customers (5 pre-loaded customers as required)
INSERT INTO customer (name, email, cpf, balance, created_at) VALUES
('John Smith', 'john.smith@email.com', '12345678901', 15000.00, CURRENT_TIMESTAMP),
('Maria Silva', 'maria.silva@email.com', '98765432109', 7500.50, CURRENT_TIMESTAMP),
('Carlos Oliveira', 'carlos.oliveira@email.com', '45678912345', 32000.00, CURRENT_TIMESTAMP),
('Ana Santos', 'ana.santos@email.com', '78912345678', 1250.75, CURRENT_TIMESTAMP),
('Pedro Costa', 'pedro.costa@email.com', '32165498732', 5000.00, CURRENT_TIMESTAMP);

-- Insert admin user
INSERT INTO customer (name, email, cpf, balance, created_at) VALUES
('Admin User', 'admin@digitalbank.com', '99988877766', 0.00, CURRENT_TIMESTAMP);
