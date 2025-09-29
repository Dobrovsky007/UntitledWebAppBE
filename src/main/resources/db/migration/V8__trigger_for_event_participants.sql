CREATE OR REPLACE FUNCTION add_event_owner()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO event_participants (user_id, event_id, role_of_participant)
    VALUES (NEW.organizer_id, NEW.id, 0);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_event_insert
AFTER INSERT ON events
FOR EACH ROW
EXECUTE FUNCTION add_event_owner();