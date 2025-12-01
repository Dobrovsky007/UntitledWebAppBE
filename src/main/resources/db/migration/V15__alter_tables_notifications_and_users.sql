ALTER TABLE users
DROP COLUMN login_disabled;

ALTER TABLE notifications
ALTER COLUMN event_id DROP NOT NULL;