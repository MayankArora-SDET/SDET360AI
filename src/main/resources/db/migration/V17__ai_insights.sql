CREATE TABLE ai_insights (
    insight_id UUID PRIMARY KEY,
    project_id UUID REFERENCES projects(project_id),
    insight_type VARCHAR,
    insight_data JSONB,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);