CREATE TABLE functional_edgecase_analysis (
    case_id UUID PRIMARY KEY,
    feature_id UUID REFERENCES features(feature_id),
    case_name VARCHAR,
    case_description TEXT,
    severity_level VARCHAR,
    test_parameters JSONB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);