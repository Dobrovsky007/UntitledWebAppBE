ALTER TABLE events 
ADD COLUMN occupied INT NOT NULL DEFAULT 1;

CREATE OR REPLACE FUNCTION increment_occupied_count()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE events 
    SET occupied = occupied + 1 
    WHERE id = NEW.event_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION decrement_occupied_count()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE events 
    SET occupied = occupied - 1 
    WHERE id = OLD.event_id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_increment_occupied
    AFTER INSERT ON event_participants
    FOR EACH ROW
    EXECUTE FUNCTION increment_occupied_count();

CREATE TRIGGER trigger_decrement_occupied
    AFTER DELETE ON event_participants
    FOR EACH ROW
    EXECUTE FUNCTION decrement_occupied_count();