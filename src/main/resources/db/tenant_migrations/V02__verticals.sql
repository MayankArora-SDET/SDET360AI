CREATE TABLE verticals (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    name VARCHAR,
    api_key VARCHAR,
    jira_username VARCHAR,
    jira_server_url VARCHAR,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);