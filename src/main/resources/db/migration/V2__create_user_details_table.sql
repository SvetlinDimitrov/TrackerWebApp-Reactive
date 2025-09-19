CREATE TABLE user_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    kilograms DOUBLE,
    height DOUBLE,
    age INT,
    workout_state VARCHAR(50),
    gender VARCHAR(50),
    goal VARCHAR(50),
    diet VARCHAR(50),
    nutrition_authority VARCHAR(50),
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
