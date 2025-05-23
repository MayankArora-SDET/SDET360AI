  CREATE TABLE automation_prompt_based_automation (
      pba_id UUID PRIMARY KEY,
      feature_id UUID REFERENCES features(feature_id),
      user_prompt TEXT NOT NULL,
      generated_response TEXT NOT NULL,
      created_at TIMESTAMP,
      updated_at TIMESTAMP,
      deleted_at TIMESTAMP
  );