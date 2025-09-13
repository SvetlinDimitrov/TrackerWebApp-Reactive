INSERT INTO users (id, username, email, password, user_role)
SELECT '1eebbd8d-203f-4a0e-ba14-84c0a6afb530', 'admin', 'admin@example.com', '$2a$10$o3FOVKjMtNBj6C.4GtOQHeAjmiPAdTrCSqyTdBRyOF1eXPPjIz1Oi', 'ADMIN'
WHERE NOT EXISTS (
  SELECT 1 FROM users WHERE email = 'admin@example.com'
);

INSERT INTO user_details (id, kilograms, height, age, workout_state, gender, user_id)
SELECT
  'a1b2c3d4-e5f6-7890-abcd-ef1234567890', -- random UUID for user_details
  75.0,                                   -- kilograms
  180.0,                                  -- height
  40,                                     -- age
  'LIGHTLY_ACTIVE',                       -- workout_state
  'MALE',                                 -- gender
  u.id                                    -- user_id
FROM users u
WHERE u.email = 'admin@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM user_details ud WHERE ud.user_id = u.id
  );