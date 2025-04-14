CREATE TABLE projects (
    project_id UUID PRIMARY KEY,
    vertical_id UUID REFERENCES verticals(vertical_id),
    project_key VARCHAR,
    project_name VARCHAR,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);