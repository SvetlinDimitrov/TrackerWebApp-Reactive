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
      ON DELETE CASCADE
);
