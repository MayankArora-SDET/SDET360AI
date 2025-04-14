CREATE TABLE product_business_requirement (
    requirement_id UUID PRIMARY KEY,
    feature_id UUID REFERENCES features(feature_id),
    requirement_name VARCHAR,
    requirement_description TEXT,
    requirement_priority VARCHAR,
    acceptance_criteria JSONB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);