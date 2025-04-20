CREATE TABLE automation_database_testing (
    db_test_id UUID PRIMARY KEY,
    feature_id UUID REFERENCES features(feature_id),
    test_name VARCHAR,
    query_config JSONB,
    expected_results JSONB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);