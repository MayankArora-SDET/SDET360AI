CREATE TABLE functional_database_testing (
    test_id UUID PRIMARY KEY,
    feature_id UUID REFERENCES features(feature_id),
    test_name VARCHAR,
    sql_query TEXT,
    expected_results JSONB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);