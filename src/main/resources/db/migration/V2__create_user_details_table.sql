CREATE TABLE user_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    kilograms DECIMAL(10,2),
    height DECIMAL(10,2),
    age INT,
    workout_state VARCHAR(50),
    gender VARCHAR(10),
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);