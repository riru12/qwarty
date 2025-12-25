# Database
This folder contains database migration scripts and schema definitions managed by Liquibase.

## Structure
```
db/
├── README.md
├── db-master.xml
├── init/           # Initial schema setup
│   ├── extensions/
│   ├── tables/
│   ├── ...
│   └── init.xml
└── alter/          # Post-deployment changes
    └── ...
```

## Migration Scripts
All scripts should have the following header which uses Liquibase's SQL convention:
```
--liquibase formatted sql
--changeset author:change_id failOnError:true logicalFilePath:path
```

## Practices
### Table Names
- Use plural names for consistency (e.g. `users` instead of `user`)
- Use lowercase letters and underscores for multi-word names (snake case) (e.g. `refresh_tokens`)

### Constraints
- Use the following naming pattern for constraints: `<table>_<field(s)>_<type>`. For example:
```
CONSTRAINT users_pk PRIMARY KEY (id)
CONSTRAINT users_email_uk UNIQUE (email)
CONSTRAINT users_fk FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
CONSTRAINT users_email_chk CHECK (email ~* '[A-Z0-9._%-]+@[A-Z0-9._%-]+\.[A-Z]{2,4}')
```

- Currently supported `<type>` suffixes (this list is not exhaustive and can be extended as needed)
    - `_pk` - primary key
    - `_uk` - unique constraint
    - `_fk` - foreign key
    - `_chk` - check constraint
