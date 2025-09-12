CREATE TABLE calories (
  id VARCHAR(36) PRIMARY KEY,
  amount DECIMAL(10,2),
  unit VARCHAR(20),
  meal_id VARCHAR(36) NOT NULL,
  food_id VARCHAR(36) NOT NULL,
  user_id VARCHAR(36) NOT NULL
);