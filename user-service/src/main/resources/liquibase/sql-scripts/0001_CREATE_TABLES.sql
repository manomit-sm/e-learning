CREATE SCHEMA IF NOT EXISTS user_service;

CREATE TABLE IF NOT EXISTS user_service.users (
    id uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    cognito_user_id VARCHAR(150) NOT NULL UNIQUE,
    name VARCHAR(100),
    phone_no VARCHAR(15) NOT NULL UNIQUE,
    address TEXT,
    photo VARCHAR(50),
    status BOOLEAN NOT NULL
);

ALTER TABLE IF EXISTS user_service.users
    ADD COLUMN IF NOT EXISTS email VARCHAR(100) NOT NULL UNIQUE,
    ADD COLUMN IF NOT EXISTS created_at timestamp default current_timestamp,
    ADD COLUMN IF NOT EXISTS updated_at timestamp;