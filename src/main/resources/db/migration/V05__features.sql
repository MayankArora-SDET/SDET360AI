CREATE TABLE features (
    feature_id UUID PRIMARY KEY,
    vertical_id UUID REFERENCES verticals(vertical_id),
    feature_name VARCHAR,
    feature_type VARCHAR,
    is_enabled BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);