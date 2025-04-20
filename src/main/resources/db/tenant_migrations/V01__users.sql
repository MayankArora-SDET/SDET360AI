-- V1__create_users_table.sql

-- enabling the pgcrypto extension for gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username       VARCHAR,
    email          VARCHAR,
    password_hash  VARCHAR,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP,
    deleted_at     TIMESTAMP
);
