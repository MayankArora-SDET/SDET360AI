CREATE TABLE interaction_tables (
    interaction_id UUID PRIMARY KEY,
    feature_id UUID REFERENCES features(feature_id),
    testcase_id UUID,
    description TEXT,
    category VARCHAR,
    url VARCHAR,
    created_on TIMESTAMP,
    updated_on TIMESTAMP,
    deleted_on TIMESTAMP
);

CREATE TABLE event_tables (
    event_id UUID PRIMARY KEY,
    interaction_id UUID REFERENCES interaction_tables(interaction_id),
    absolute_path VARCHAR,
    relative_xpath VARCHAR,
    relational_xpath VARCHAR,
    is_modified BOOLEAN,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);