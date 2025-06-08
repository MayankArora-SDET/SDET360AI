  CREATE TABLE automation_prompt_based_automation (
      pba_id UUID PRIMARY KEY,
      feature_id UUID REFERENCES features(feature_id),
      test_case_id VARCHAR NOT NULL,
      user_prompt TEXT NOT NULL,
      generated_script TEXT NOT NULL,
      log_path TEXT NOT NULL,
      report_path TEXT NOT NULL,
      output_path TEXT NOT NULL,
      created_at TIMESTAMP,
      updated_at TIMESTAMP,
      deleted_at TIMESTAMP
  );