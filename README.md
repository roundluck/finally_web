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

## Documentation & Deliverables

- `docs/FunctionalDocument.md` – feature overview, personas, and usage scenarios
- `docs/TechnicalDocument.md` – architecture, diagrams, API contracts, deployment
- `db/dorm_maintenance_schema.sql` – schema and seed data for MySQL
- `backend/` and `frontend/` – source code for API and web client

## Testing

- Backend: `./mvnw test`
- Frontend: `npm run build` (ensures the app compiles for production)

## Environment Variables

- `backend/src/main/resources/application.yml` includes defaults for in-memory H2. Override with `SPRING_DATASOURCE_*` variables for MySQL.
- Frontend optionally reads `VITE_API_BASE_URL`; defaults to `http://localhost:8080`.
