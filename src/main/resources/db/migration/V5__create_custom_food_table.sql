CREATE TABLE IF NOT EXISTS custom_food (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    info TEXT NULL,
    large_info TEXT NULL,
    picture VARCHAR(512) NULL,
    calorie_amount DOUBLE NULL,
    calorie_unit VARCHAR(50) NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_custom_food_user
      FOREIGN KEY (user_id) REFERENCES users (id)
      ON UPDATE CASCADE
      ON DELETE CASCADE,
    INDEX idx_custom_food_user (user_id)
);

CREATE TABLE IF NOT EXISTS custom_food_servings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    food_id BIGINT NOT NULL,
    amount DOUBLE NOT NULL,
    metric VARCHAR(50) NOT NULL,
    main BOOLEAN DEFAULT FALSE,
    grams_total DOUBLE NOT NULL,
    CONSTRAINT fk_cfs_custom_food
      FOREIGN KEY (food_id) REFERENCES custom_food(id)
      ON UPDATE CASCADE
      ON DELETE CASCADE,
    INDEX idx_cfs_custom_food (food_id)
);

CREATE TABLE IF NOT EXISTS custom_food_nutritions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    food_id BIGINT NOT NULL,
    name VARCHAR(100),
    unit VARCHAR(50),
    amount DOUBLE,
    CONSTRAINT fk_cfn_custom_food
      FOREIGN KEY (food_id) REFERENCES custom_food(id)
      ON UPDATE CASCADE
      ON DELETE CASCADE,
    INDEX idx_cfn_custom_food (food_id),
    UNIQUE KEY uq_cfn_name_per_food (food_id, name)
);
