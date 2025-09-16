CREATE TABLE meal_foods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    info VARCHAR(255),
    large_info TEXT,
    picture VARCHAR(255),
    calorie_amount DOUBLE,
    calorie_unit VARCHAR(20),
    meal_id BIGINT NOT NULL,
    CONSTRAINT fk_meal_foods_meal FOREIGN KEY (meal_id) REFERENCES meals(id) ON DELETE CASCADE
);