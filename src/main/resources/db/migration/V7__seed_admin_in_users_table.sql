INSERT INTO users (username, email, password, role)
SELECT 'admin', 'admin@example.com', '$2a$10$o3FOVKjMtNBj6C.4GtOQHeAjmiPAdTrCSqyTdBRyOF1eXPPjIz1Oi', 'ADMIN'
WHERE NOT EXISTS (
  SELECT 1 FROM users WHERE email = 'admin@example.com'
);

INSERT INTO user_details (
  kilograms, height, age, workout_state, gender, goal, diet, nutrition_authority, user_id
)
SELECT
  75.0,                          -- weight
  180.0,                         -- height
  40,                            -- age
  'LIGHTLY_ACTIVE',              -- WorkoutState
  'MALE',                        -- Gender
  'MAINTAIN_WEIGHT',             -- Goals
  'NONE',                        -- DietType
  'NIH_IOM',                     -- NutritionAuthority
  u.id
FROM users u
WHERE u.email = 'admin@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM user_details ud WHERE ud.user_id = u.id
  );
