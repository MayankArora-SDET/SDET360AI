CREATE TABLE automation_custom_function (
    function_id UUID PRIMARY KEY,
    feature_id UUID REFERENCES features(feature_id),
    function_name VARCHAR,
    function_code TEXT,
    function_language VARCHAR,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);