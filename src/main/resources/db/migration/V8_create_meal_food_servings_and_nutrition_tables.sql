CREATE TABLE meal_foods_servings (
    meal_food_id BIGINT NOT NULL,
    serving_id BIGINT NOT NULL,
    PRIMARY KEY (meal_food_id, serving_id),
    CONSTRAINT fk_mfs_meal_food FOREIGN KEY (meal_food_id) REFERENCES meal_foods(id) ON DELETE CASCADE,
    CONSTRAINT fk_mfs_serving FOREIGN KEY (serving_id) REFERENCES servings(id) ON DELETE CASCADE
);

CREATE TABLE meal_foods_nutritions (
    meal_food_id BIGINT NOT NULL,
    nutrition_id BIGINT NOT NULL,
    PRIMARY KEY (meal_food_id, nutrition_id),
    CONSTRAINT fk_mfn_meal_food FOREIGN KEY (meal_food_id) REFERENCES meal_foods(id) ON DELETE CASCADE,
    CONSTRAINT fk_mfn_nutrition FOREIGN KEY (nutrition_id) REFERENCES nutritions(id) ON DELETE CASCADE
);
