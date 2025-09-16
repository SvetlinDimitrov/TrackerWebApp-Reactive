CREATE TABLE servings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DOUBLE,
    serving_weight DOUBLE,
    metric VARCHAR(50),
    main BOOLEAN DEFAULT FALSE
);
