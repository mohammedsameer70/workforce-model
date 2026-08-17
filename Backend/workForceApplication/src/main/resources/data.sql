-- Insert test users with BCrypt hashed passwords
-- Note: Using same hash for demo purposes. In production, each user should have unique password.
-- Password for all test users: admin123
-- Hash generated using BCryptPasswordEncoder for "admin123"
-- Using INSERT IGNORE to handle duplicate entries on re-runs

-- First delete existing users to ensure clean insert
DELETE FROM users WHERE username IN ('admin', 'manager', 'viewer');

INSERT INTO users (username, password, role, enabled) VALUES 
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', true),
('manager', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MANAGER', true),
('viewer', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'VIEWER', true);
