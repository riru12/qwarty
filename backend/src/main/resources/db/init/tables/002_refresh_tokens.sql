--liquibase formatted sql

--changeset qwarty:init/tables/refresh_tokens failOnError:true logicalFilePath:init/tables/refresh_tokens

CREATE TABLE refresh_tokens(
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    token_hash TEXT NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT refresh_tokens_pk PRIMARY KEY (id),
    CONSTRAINT users_fk FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);