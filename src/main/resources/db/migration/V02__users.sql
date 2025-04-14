
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    username VARCHAR,
    email VARCHAR,
    password_hash VARCHAR,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);
