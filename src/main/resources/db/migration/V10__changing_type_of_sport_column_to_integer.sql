ALTER TABLE user_sports
    ALTER COLUMN sport TYPE INTEGER USING sport::integer;