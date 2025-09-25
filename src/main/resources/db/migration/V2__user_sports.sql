CREATE TABLE user_sports (
                           user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                           sport VARCHAR(100) NOT NULL,
                           skill_level INT NOT NULL,
                           PRIMARY KEY (user_id, sport)
);