CREATE TABLE open_prompt (
    chat_id UUID PRIMARY KEY,
    feature_id UUID REFERENCES features(feature_id),
    user_id UUID REFERENCES users(id),
    message_content TEXT,
    message_type VARCHAR,
    message_time TIMESTAMP,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);