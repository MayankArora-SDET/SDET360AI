CREATE TABLE functional_api_testing (
    test_id UUID PRIMARY KEY,
    feature_id UUID REFERENCES features(feature_id),
    endpoint_url VARCHAR,
    http_method VARCHAR,
    request_body JSONB,
    expected_response JSONB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);