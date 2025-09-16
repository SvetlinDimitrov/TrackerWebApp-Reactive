INSERT INTO users (username, email, password, role)
SELECT 'admin', 'admin@example.com', '$2a$10$o3FOVKjMtNBj6C.4GtOQHeAjmiPAdTrCSqyTdBRyOF1eXPPjIz1Oi', 'ADMIN'
WHERE NOT EXISTS (
  SELECT 1 FROM users WHERE email = 'admin@example.com'
);

INSERT INTO user_details (kilograms, height, age, workout_state, gender, user_id)
SELECT
  75.0,
  180.0,
  40,
  'LIGHTLY_ACTIVE',
  'MALE',
  u.id
FROM users u
WHERE u.email = 'admin@example.com'
  AND NOT EXISTS (
    SELECT 1 FROM user_details ud WHERE ud.user_id = u.id
  );
