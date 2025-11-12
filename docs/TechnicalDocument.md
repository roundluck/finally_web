# Technical Document – Dormitory Maintenance Request Management System

## 1. Architecture Overview
- **Client:** React 19 SPA created with Vite. Uses React Router for navigation and Context API for auth state. Communicates with backend via Fetch and JWT Bearer tokens.
- **Server:** Spring Boot 3.3 monolith exposing REST APIs. Layers: Controller → Service → Repository → JPA Entities. Spring Security protects endpoints with JWT filter. OpenAPI served at `/swagger-ui`.
- **Database:** H2 in-memory for local development; MySQL 8 for production. Tables: `users`, `maintenance_requests`, `request_timeline`.
- **Build/Deploy:** Maven Wrapper for backend, npm scripts for frontend. Containerisation-ready via Spring Boot fat jar & static frontend build output.

```
[React SPA] --HTTP+JWT--> [Spring Boot API] --JPA--> [MySQL/H2]
```

## 2. Key Components
| Layer | Classes | Notes |
| --- | --- | --- |
| Security | `JwtService`, `JwtAuthenticationFilter`, `SecurityConfig`, `AppUserDetailsService` | Issues and validates JWT, configures stateless filter chain. |
| Controllers | `AuthController`, `MaintenanceRequestController`, `UserDirectoryController` | Handle auth, CRUD requests, technician directory. |
| Services | `AuthServiceImpl`, `MaintenanceRequestServiceImpl`, `UserDirectoryServiceImpl` | Encapsulate business rules (status transitions, assignment validation). |
| Persistence | `AppUser`, `MaintenanceRequest`, `RequestTimelineEntry` + repositories | Mapped with JPA annotations. |
| Frontend | `AuthContext`, `Dashboard`, `LoginPage`, `api/client.js` | Context-driven auth and board UI. |

## 3. Data Model (ER)
```
AppUser (id, username, password, fullName, email, phone, dorm, role)
 1 ────< MaintenanceRequest (id, title, dorm, room, priority, status, student_id, manager_id, technician_id, ...)
MaintenanceRequest 1 ────< RequestTimelineEntry (id, status, note, created_by, created_at)
```
- `role` enum: STUDENT, MANAGER, TECHNICIAN.
- `status` enum: NEW, UNDER_REVIEW, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED.

## 4. API Reference (selected)
| Method | Endpoint | Description | Roles |
| --- | --- | --- | --- |
| POST | `/api/auth/login` | Issue JWT token | Public |
| GET | `/api/auth/me` | Current profile | Authenticated |
| POST | `/api/requests` | Submit maintenance request | STUDENT |
| GET | `/api/requests` | List requests (status/dorm/mine filters) | ALL (scope limited server-side) |
| PATCH | `/api/requests/{id}/assign` | Assign technician + target date | MANAGER |
| PATCH | `/api/requests/{id}/status` | Move request through workflow (validation by role) | STUDENT / MANAGER / TECHNICIAN |
| GET | `/api/users?role=TECHNICIAN` | Directory of technicians for assignment UI | MANAGER |

All responses are JSON `*Response` DTOs described in `edu.ntu.maintenance.dto` package.

## 5. Security
- BCrypt hashed passwords.
- Stateless JWT with custom filter placed before `UsernamePasswordAuthenticationFilter`.
- Method-level security via `@PreAuthorize` on user directory.
- Configurable CORS origins in `application.yml` (`app.cors.allowed-origins`).
- H2 console disabled unless authenticated; headers configured for frames.

## 6. Build & Deployment Steps
1. **Backend**
   ```bash
   cd backend
   ./mvnw clean package
   java -jar target/dorm-maintenance-backend-0.0.1-SNAPSHOT.jar
   ```
   Configure MySQL via environment variables or override `spring.datasource.*` (see `application.yml`).
2. **Frontend**
   ```bash
   cd frontend
   npm install
   npm run build
   ```
   Deploy `/dist` behind static host (Netlify, S3) and point `VITE_API_BASE_URL` to backend URL.

## 7. Testing Strategy
- **Backend:** `./mvnw test` ensures code compiles; service-level tests can be expanded (placeholder for demonstration due to time).
- **Frontend:** `npm run build` verifies JSX & TypeScript transpile; manual QA covering login, submission, assignment, technician actions.

## 8. Operations & Monitoring
- Structured logs via Spring Boot (log level overrides for Hibernate & Security).
- Ready for Spring Boot Actuator if needed (`/actuator` GET permitted by default).
- Seed data via `DataSeeder` for demo users/requests.

## 9. Known Limitations
- File uploads replaced with external photo URL (fits coursework scope).
- Notifications (email/SMS) not implemented; timeline + UI acts as feedback loop.
- No pagination on request list (sufficient for assignment volume but can be added with Spring Data `Pageable`).

## 10. Future Enhancements
- Multi-dorm analytics dashboard + CSV export.
- WebSocket push for live status updates.
- Technician mobile PWA with offline cache.
- Automated reminders when requests remain in NEW/ASSIGNED beyond SLA threshold.
