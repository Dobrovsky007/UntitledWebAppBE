ALTER TABLE users 
ADD COLUMN trust_score INT DEFAULT 0,
ADD COLUMN number_of_reviews INT DEFAULT 0;