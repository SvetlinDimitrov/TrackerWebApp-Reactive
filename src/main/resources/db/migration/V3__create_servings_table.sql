CREATE TABLE servings (
  id VARCHAR(36) PRIMARY KEY,
  amount DECIMAL(10,2),
  serving_weight DECIMAL(10,2),
  metric VARCHAR(50),
  main BOOLEAN DEFAULT FALSE,
  food_id VARCHAR(36) NOT NULL
);