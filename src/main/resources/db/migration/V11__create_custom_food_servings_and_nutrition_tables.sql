CREATE TABLE IF NOT EXISTS custom_food_servings (
    custom_food_id BIGINT NOT NULL,
    serving_id BIGINT NOT NULL,
    PRIMARY KEY (custom_food_id, serving_id),
    CONSTRAINT fk_cfs_custom_food FOREIGN KEY (custom_food_id) REFERENCES custom_food(id) ON DELETE CASCADE,
    CONSTRAINT fk_cfs_serving     FOREIGN KEY (serving_id)     REFERENCES servings(id)     ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS custom_food_nutritions (
    custom_food_id BIGINT NOT NULL,
    nutrition_id BIGINT NOT NULL,
    PRIMARY KEY (custom_food_id, nutrition_id),
    CONSTRAINT fk_cfn_custom_food FOREIGN KEY (custom_food_id) REFERENCES custom_food(id) ON DELETE CASCADE,
    CONSTRAINT fk_cfn_nutrition   FOREIGN KEY (nutrition_id)   REFERENCES nutritions(id)  ON DELETE CASCADE
);
