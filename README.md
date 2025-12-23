# qwarty

[![Backend CI](https://github.com/riru12/qwarty/actions/workflows/backend-ci.yml/badge.svg)](https://github.com/riru12/qwarty/actions/workflows/backend-ci.yml)

## Setup

### Prerequisites
* [Node 25+](https://nodejs.org/en)
* [Java 25+](https://adoptium.net/temurin/releases)
* [Docker](https://www.docker.com/)

### .env
Create a .env file in the root of the repository based on .env.example.

```
POSTGRES_USER=                  # Database username
POSTGRES_PASSWORD=              # Database password
POSTGRES_DB=                    # Database name
JWT_SECRET_KEY=                 # Secret key used to sign JWTs
JWT_ACCESS_EXPIRATION_TIME=     # Access token lifetime in milliseconds (e.g. 3600000 = 1 hour)
JWT_REFRESH_EXPIRATION_TIME=    # Refresh token lifetime in milliseconds (e.g. 604800000 = 7 days)
BACKEND_PORT=                   # Port where the Spring Boot server will run (e.g. 8080)
VITE_BACKEND_URL=               # Public backend URL (e.g. http://localhost:8080)
FRONTEND_URL=                   # Frontend URL (e.g. http://localhost:5173)
```

### Database
1. In the root of the repository, create the docker container for the PostgreSQL database
```bash
docker compose up -d
```


### Backend
1. Ensure that docker image for the PostgreSQL database is already running
2. Move to the backend directory `cd backend`
3. Run Spring Boot server `./mvnw spring-boot:run`
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend
1. Move to the frontend directory `cd frontend`
2. Install dependencies `npm install`
3. Host locally `npm run dev`
```bash
cd frontend
npm install
npm run dev
```