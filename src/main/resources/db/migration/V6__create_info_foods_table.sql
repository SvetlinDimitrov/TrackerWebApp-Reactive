CREATE TABLE info_foods (
  id VARCHAR(36) PRIMARY KEY,
  info VARCHAR(255),
  large_info TEXT,
  picture VARCHAR(255),
  food_id VARCHAR(36) NOT NULL
);