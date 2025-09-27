CREATE TABLE meal_foods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    info VARCHAR(255),
    large_info TEXT,
    picture VARCHAR(255),
    calorie_amount DOUBLE,
    calorie_unit VARCHAR(50),
    meal_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_meal_foods_meal FOREIGN KEY (meal_id) REFERENCES meals(id) ON DELETE CASCADE,
    CONSTRAINT fk_meal_foods_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_meal_foods_meal (meal_id),
    INDEX idx_meal_foods_user (user_id)
);

CREATE TABLE meal_food_serving (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    food_id BIGINT NOT NULL,
    amount DOUBLE NOT NULL,
    metric VARCHAR(50) NOT NULL,
    main BOOLEAN DEFAULT FALSE,
    grams_total DOUBLE NOT NULL,
    CONSTRAINT fk_mfs_meal_food FOREIGN KEY (food_id) REFERENCES meal_foods(id) ON DELETE CASCADE,
    INDEX idx_mfs_meal_food (food_id)
);

CREATE TABLE meal_food_nutrition (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    food_id BIGINT NOT NULL,
    name VARCHAR(100),
    unit VARCHAR(50),
    amount DOUBLE,
    CONSTRAINT fk_mfn_meal_food FOREIGN KEY (food_id) REFERENCES meal_foods(id) ON DELETE CASCADE,
    INDEX idx_mfn_meal_food (food_id),
    UNIQUE KEY uq_mfn_name_per_food (food_id, name)
);
