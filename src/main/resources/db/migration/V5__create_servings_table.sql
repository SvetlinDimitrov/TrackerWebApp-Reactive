CREATE TABLE servings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10,2),
    serving_weight DECIMAL(10,2),
    metric VARCHAR(50),
    main BOOLEAN DEFAULT FALSE,
    food_id BIGINT NOT NULL,
    CONSTRAINT fk_servings_food FOREIGN KEY (food_id) REFERENCES inserted_foods(id) ON DELETE CASCADE
);
