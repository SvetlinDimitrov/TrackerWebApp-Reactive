CREATE TABLE meal_foods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    info VARCHAR(255),
    large_info TEXT,
    picture VARCHAR(255),
    calorie_amount DOUBLE,
    calorie_unit VARCHAR(20),
    meal_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_meal_foods_meal FOREIGN KEY (meal_id) REFERENCES meals(id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_foods_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
