CREATE TABLE user_details (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    kilograms DOUBLE,
    height DOUBLE,
    age INT,
    workout_state VARCHAR(50),
    gender VARCHAR(10),
    goal VARCHAR(50),           -- NEW
    duet VARCHAR(50),      -- NEW
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
