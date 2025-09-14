CREATE TABLE info_foods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    info VARCHAR(255),
    large_info TEXT,
    picture VARCHAR(255),
    food_id BIGINT NOT NULL,
    CONSTRAINT fk_info_foods_food FOREIGN KEY (food_id) REFERENCES inserted_foods(id) ON DELETE CASCADE
);
