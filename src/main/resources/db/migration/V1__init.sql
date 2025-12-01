CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       enabled BOOLEAN DEFAULT false,
                       created_at TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE email_verification_tokens (
                                           id UUID PRIMARY KEY,
                                           user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                           token VARCHAR(255) NOT NULL,
                                           expires_at TIMESTAMPTZ NOT NULL,
                                           created_at TIMESTAMPTZ DEFAULT now()
);

CREATE INDEX idx_verification_token_user ON email_verification_tokens(user_id);
