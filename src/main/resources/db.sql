-- ============================================
-- Finance Dashboard — Database Schema (H2)
-- ============================================

-- Permissions lookup table
CREATE TABLE IF NOT EXISTS permissions (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Roles lookup table
CREATE TABLE IF NOT EXISTS roles (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(30)  NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Many-to-many: roles <-> permissions
CREATE TABLE IF NOT EXISTS role_permissions (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id)       REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name  VARCHAR(100) NOT NULL,
    email      VARCHAR(150) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Many-to-many: users <-> roles
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Categories for financial records
CREATE TABLE IF NOT EXISTS categories (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Financial records
CREATE TABLE IF NOT EXISTS financial_records (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount      DECIMAL(15,2) NOT NULL,
    type        VARCHAR(20)   NOT NULL,
    category_id BIGINT,
    record_date DATE          NOT NULL,
    description VARCHAR(500),
    deleted     BOOLEAN       DEFAULT FALSE,
    created_by  BIGINT,
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by)  REFERENCES users(id) ON DELETE SET NULL
);

-- Indexes for common queries
CREATE INDEX IF NOT EXISTS idx_records_type        ON financial_records(type);
CREATE INDEX IF NOT EXISTS idx_records_date        ON financial_records(record_date);
CREATE INDEX IF NOT EXISTS idx_records_category    ON financial_records(category_id);
CREATE INDEX IF NOT EXISTS idx_records_deleted      ON financial_records(deleted);
CREATE INDEX IF NOT EXISTS idx_records_created_by   ON financial_records(created_by);
CREATE INDEX IF NOT EXISTS idx_users_email          ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_status         ON users(status);

-- ============================================
-- Seed data: Permissions
-- ============================================
INSERT INTO permissions (name, description) VALUES
    ('READ_RECORDS',      'View financial records'),
    ('WRITE_RECORDS',     'Create and update financial records'),
    ('DELETE_RECORDS',    'Delete financial records'),
    ('VIEW_ANALYTICS',   'Access dashboard analytics'),
    ('MANAGE_USERS',     'Create, update, deactivate users'),
    ('MANAGE_CATEGORIES','Create, update, delete categories');

-- ============================================
-- Seed data: Roles
-- ============================================
INSERT INTO roles (name, description) VALUES
    ('VIEWER',  'Read-only access to financial records'),
    ('ANALYST', 'Read records and view analytics/insights'),
    ('ADMIN',   'Full access — CRUD, user management, analytics');

-- ============================================
-- Seed data: Role-Permission mappings
-- ============================================
-- VIEWER: READ_RECORDS
INSERT INTO role_permissions (role_id, permission_id)
    SELECT r.id, p.id FROM roles r, permissions p
    WHERE r.name = 'VIEWER' AND p.name = 'READ_RECORDS';

-- ANALYST: READ_RECORDS + VIEW_ANALYTICS
INSERT INTO role_permissions (role_id, permission_id)
    SELECT r.id, p.id FROM roles r, permissions p
    WHERE r.name = 'ANALYST' AND p.name IN ('READ_RECORDS', 'VIEW_ANALYTICS');

-- ADMIN: all permissions
INSERT INTO role_permissions (role_id, permission_id)
    SELECT r.id, p.id FROM roles r, permissions p
    WHERE r.name = 'ADMIN';

-- ============================================
-- Seed data: Default categories
-- ============================================
INSERT INTO categories (name, description) VALUES
    ('Salary',         'Monthly salary and wages'),
    ('Freelance',      'Freelance and contract income'),
    ('Investment',     'Investment returns and dividends'),
    ('Rent',           'Rent and lease payments'),
    ('Utilities',      'Electricity, water, internet bills'),
    ('Food',           'Groceries and dining'),
    ('Transportation', 'Fuel, public transport, ride-sharing'),
    ('Healthcare',     'Medical expenses and insurance'),
    ('Entertainment',  'Movies, subscriptions, hobbies'),
    ('Miscellaneous',  'Other uncategorized expenses');
