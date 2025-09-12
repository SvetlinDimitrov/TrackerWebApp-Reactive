CREATE TABLE inserted_foods (
  id VARCHAR(36) PRIMARY KEY,
  name VARCHAR(100),
  meal_id VARCHAR(36) NOT NULL,
  user_id VARCHAR(36) NOT NULL
);