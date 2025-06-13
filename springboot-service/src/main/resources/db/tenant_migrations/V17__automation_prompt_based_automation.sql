-- Create test_cases table
CREATE TABLE prompt_automation_test_cases (
    id UUID PRIMARY KEY,
    feature_id UUID REFERENCES features(feature_id),
    test_case_id VARCHAR NOT NULL,
    category TEXT NOT NULL,
    description TEXT NOT NULL,
    log_path TEXT NOT NULL,
    report_path TEXT NOT NULL,
    output_path TEXT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

-- Create test_steps table
CREATE TABLE prompt_automation_test_steps (
    id UUID PRIMARY KEY,
    test_case_id UUID REFERENCES prompt_automation_test_cases(id),
    step_number INT NOT NULL,
    test_steps TEXT,
    test_data TEXT,
    expected_result TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);