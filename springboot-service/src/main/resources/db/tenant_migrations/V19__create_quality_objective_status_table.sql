CREATE TABLE IF NOT EXISTS quality_objective_status (
    id UUID PRIMARY KEY,
    key_feature VARCHAR(255) NOT NULL,
    category VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    success_criteria_level_1 TEXT NOT NULL,
    success_criteria_level_2 TEXT NOT NULL, 
    vertical_id UUID NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);
