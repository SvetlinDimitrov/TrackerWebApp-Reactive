CREATE TABLE nutritions (
  id VARCHAR(36) PRIMARY KEY,
  name VARCHAR(100),
  unit VARCHAR(20),
  amount DECIMAL(10,2),
  food_id VARCHAR(36) NOT NULL,
  user_id VARCHAR(36) NOT NULL
);