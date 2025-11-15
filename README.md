# Dormitory Maintenance Request Management System

A full-stack web application for IN6225 individual assignment that digitises dormitory repair workflows. Students lodge maintenance issues, dorm managers triage and assign technicians, and technicians update progress using a shared timeline. The project follows the deliverables requested in class: source code (React + Spring Boot), database schema dump, and accompanying functional/technical documentation.

## Tech Stack

- **Frontend:** React 19 + Vite, React Router, Fetch API
- **Backend:** Spring Boot 3.3, Spring Security (JWT), Spring Data JPA, H2 (dev) / MySQL (prod)
- **Build Tools:** Maven Wrapper, npm

## Getting Started

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

The server listens on `http://localhost:8080`. Default accounts (password `Password!23`):

| Username      | Role       |
| ------------- | ---------- |
| `alicelee`    | STUDENT    |
| `briantan`    | STUDENT    |
| `dorm_manager`| MANAGER    |
| `tech_mario`  | TECHNICIAN |

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173` and sign in with one of the accounts above.

### Quick local demo (5 minutes)

1. `./backend/mvnw clean package spring-boot:repackage && java -jar backend/target/dorm-maintenance-backend-0.0.1-SNAPSHOT.jar`
2. In a new terminal: `cd frontend && npm install && npm run dev` (or `npm run build && npx serve dist` for prod)
3. Browse `http://localhost:5173` → login as `dorm_manager / Password!23` and walk through the scenario in `docs/DemoScript.md`.

### One-command helper

`./scripts/demo.sh` builds the backend jar, starts it in the background (log file: `backend-demo.log`), and then launches the Vite dev server. Press `Ctrl+C` when done and the script tears the backend down automatically.

### Local Node 20 runtime

The grading rubric expects the frontend to run on Node ≥ 20.19. Run `./scripts/install-node20.sh` once to download the official Node 20 binary into `.tools/`, then wrap any frontend command with `./scripts/node20.sh …`, for example:

```bash
./scripts/node20.sh npm install
./scripts/node20.sh npm run lint
./scripts/node20.sh npm run test
```

### Docker Compose (MySQL + backend + static frontend)

```bash
docker compose up --build
```

- `http://localhost:8080/actuator/health` – Spring Boot API (backed by MySQL container `db`)
- `http://localhost:4173` – Production build served by Nginx
- Default credentials remain the same; schema + seed data are handled by the application on first run.

## Documentation & Deliverables

- `docs/FunctionalDocument.md` – feature overview, personas, and usage scenarios
- `docs/TechnicalDocument.md` – architecture, diagrams, API contracts, deployment
- `db/dorm_maintenance_schema.sql` – schema and seed data for MySQL
- `backend/` and `frontend/` – source code for API and web client
- `docs/DemoScript.md` – 5‑10 minute live-demo script mapped to grading rubric

## Testing

- Backend: `./mvnw test` (integration tests hit auth, assignment, and status workflows)
- Frontend: `./scripts/node20.sh npm run lint && ./scripts/node20.sh npm run test && ./scripts/node20.sh npm run build`

## Environment Variables

- `backend/src/main/resources/application.yml` includes defaults for in-memory H2. Override with `SPRING_DATASOURCE_*` variables for MySQL.
- Frontend optionally reads `VITE_API_BASE_URL`; defaults to `http://localhost:8080`.

## Quality checklist

- ✅ Deployable artifacts (`java -jar …` and static `frontend/dist`)
- ✅ Automated backend regression tests
- ✅ ESLint/TypeScript checks enforced by `npm run lint`
- ✅ Live-demo script & seeded data to replicate flows quickly
- ✅ Documented credentials, environment switches, and MySQL dump with seed data
- ✅ Automated Docker Compose stack for MySQL + backend + static frontend
- ✅ Actuator health endpoint + helper script for timed demonstrations
