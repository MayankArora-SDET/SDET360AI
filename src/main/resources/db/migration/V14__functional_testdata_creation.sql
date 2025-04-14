CREATE TABLE functional_testdata_creation (
    data_id UUID PRIMARY KEY,
    feature_id UUID REFERENCES features(feature_id),
    data_name VARCHAR,
    test_data JSONB,
    data_type VARCHAR,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);