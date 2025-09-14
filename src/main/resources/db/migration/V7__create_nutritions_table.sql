CREATE TABLE nutritions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    unit VARCHAR(20),
    amount DECIMAL(10,2),
    food_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_nutritions_food FOREIGN KEY (food_id) REFERENCES inserted_foods(id) ON DELETE CASCADE,
    CONSTRAINT fk_nutritions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
