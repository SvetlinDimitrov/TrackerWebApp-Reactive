CREATE TABLE user_details (
  id VARCHAR(36) PRIMARY KEY,
  kilograms DECIMAL(10,2),
  height DECIMAL(10,2),
  age INT,
  workout_state VARCHAR(50),
  gender VARCHAR(10),
  user_id VARCHAR(36) NOT NULL
);