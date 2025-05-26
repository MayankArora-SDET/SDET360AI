CREATE TABLE IF NOT EXISTS release_test_coverage (
    id UUID PRIMARY KEY,
    epic VARCHAR(255) NOT NULL,
    severity_1 INTEGER NOT NULL,
    severity_2 INTEGER NOT NULL,
    severity_3 INTEGER NOT NULL,
    severity_4 INTEGER NOT NULL,
    test_cases INTEGER NOT NULL,
    vertical_id UUID NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);