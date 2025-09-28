CREATE TABLE events (
    id UUID PRIMARY KEY,
    organizer_id UUID NOT NULL,
    sport VARCHAR(50) NOT NULL,
    skill_level INT NOT NULL,
    adress TEXT NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    capacity INT NOT NULL,
    status_of_event INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)