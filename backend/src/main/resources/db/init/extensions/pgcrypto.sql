--liquibase formatted sql

--changeset qwarty:init/extensions/pgcrypto failOnError:true logicalFilePath:init/extensions/pgcrypto

CREATE EXTENSION IF NOT EXISTS pgcrypto;