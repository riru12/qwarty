--liquibase formatted sql

--changeset qwarty:init/tables/users failOnError:true logicalFilePath:init/tables/users

CREATE TABLE users(
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    email TEXT NOT NULL,
    username TEXT NOT NULL,
    password_hash TEXT NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    status TEXT NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT users_pk PRIMARY KEY (id),
    CONSTRAINT users_email_uk UNIQUE (email),
    CONSTRAINT users_username_uk UNIQUE (username),
    CONSTRAINT users_status_chk CHECK (status IN ('ACTIVE', 'DISABLED')),
    CONSTRAINT users_email_chk CHECK (email ~* '[A-Z0-9._%-]+@[A-Z0-9._%-]+\.[A-Z]{2,4}')
);