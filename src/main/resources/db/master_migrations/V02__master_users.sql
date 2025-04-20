CREATE TABLE master_users (
    id BIGINT  PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255)
);