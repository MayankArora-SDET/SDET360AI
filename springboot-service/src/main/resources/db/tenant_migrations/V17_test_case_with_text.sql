CREATE TABLE IF NOT EXISTS test_case_with_text (
    id UUID PRIMARY KEY,
    feature_id UUID REFERENCES features(feature_id),
    created_at TIMESTAMP,
    updated_at TIMESTAMP, 
    user_text VARCHAR,
    ai_response VARCHAR,
);

