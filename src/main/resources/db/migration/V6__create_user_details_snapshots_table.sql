CREATE TABLE user_details_snapshots (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    kilograms DOUBLE,
    height DOUBLE,
    age INT,
    workout_state VARCHAR(50),
    gender VARCHAR(50),
    goal VARCHAR(50),
    diet VARCHAR(50),
    nutrition_authority VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
