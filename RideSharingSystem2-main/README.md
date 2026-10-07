**Overview**: This repository contains a React + Vite frontend and a Spring Boot backend. The app stores data in PostgreSQL. The repository provides a `docker-compose.yml` to run the whole stack (Postgres + pgAdmin + backend + frontend).

**Quick Docker run**:
- **Build & start**: run the Compose file from the project root (PowerShell):

```powershell
cd "d:\CICD project\RideSharingSystem2-main"
docker compose up -d --build
```

- **Access services**:
  - Frontend: http://localhost:8080
  - Backend API: http://localhost:6565 (the backend listens on container port `9090` and is mapped to host `6565`)
  - pgAdmin: http://localhost:5050 (login `admin@local` / `admin`)

**Connect pgAdmin to Postgres**:
- In pgAdmin UI add a new server:
  - Name: `ridesharing-db`
  - Host: `localhost`
  - Port: `5432`
  - Username: `postgres`
  - Password: `1234`

Because Docker Compose maps Postgres container port `5432` to the host, `localhost:5432` works from your machine and pgAdmin.

**Run services locally (no Docker)**
- PostgreSQL: install PostgreSQL (e.g., via the Postgres installer) and create a database/user matching the defaults below, or change the values in `application.properties`.
- Backend (Spring Boot): set env vars or rely on the defaults in `application.properties`.

Example will run the backend locally (PowerShell):

```powershell
# (1) ensure PostgreSQL is running and has DB 'ridesharing-backend' and user 'postgres' with password '1234'
# (2) from backend folder
cd "d:\CICD project\RideSharingSystem2-main\Backend\ridesharing-backend"
.
\mvnw.cmd spring-boot:run
```

If you prefer to run with explicit env variables (overrides defaults):

```powershell
$env:SPRING_DATASOURCE_URL = 'jdbc:postgresql://localhost:5432/ridesharing-backend'
$env:SPRING_DATASOURCE_USERNAME = 'postgres'
$env:SPRING_DATASOURCE_PASSWORD = '1234'
.
\mvnw.cmd spring-boot:run
```

**Run frontend locally**

```powershell
cd "d:\CICD project\RideSharingSystem2-main\frontend"
npm install
# point Vite dev server to the backend host:port (use host port mapping 6565)
$env:VITE_API_URL = 'http://localhost:6565'
npm run dev
```

**What I changed and why**
- `Backend/src/main/resources/application.properties`: now reads DB connection and server port from environment variables (with defaults). This allows the same file to work both in Docker and locally.
- `docker-compose.yml`: added a `pgadmin` service and its persistent volume so you can manage the Postgres DB via a web UI.

**DB defaults used by the Compose file**
- DB name: `ridesharing-backend`
- DB user: `postgres`
- DB password: `1234`

If you'd like, I can also:
- Add a small SQL init script to create tables or seed data at container startup.
- Wire frontend runtime env substitution so you can deploy in different environments without rebuilding.

---
If you want, I can now:
- start the Docker stack for you (runs `docker compose up -d`) or
- add an SQL init script and test the full stack locally.
