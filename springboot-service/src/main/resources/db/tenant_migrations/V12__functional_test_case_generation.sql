CREATE TABLE functional_test_case_generation (
    test_case_id UUID PRIMARY KEY,
    feature_id UUID REFERENCES features(feature_id),
    test_name VARCHAR,
    test_description TEXT,
    test_priority VARCHAR,
    test_steps JSONB,
    expected_results JSONB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);