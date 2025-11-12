# Dormitory Maintenance Request Management System – Requirements

This document consolidates the functional and non‑functional requirements for the Dormitory Maintenance Request Management System (DMRMS) so it can accompany the GitHub repository.

## 1. Business Goals
- Digitise the dormitory maintenance workflow so students submit issues online.
- Give dorm managers a single dashboard to triage, prioritise, and assign technicians.
- Provide transparency to all parties via real‑time status updates and a chronological timeline.

## 2. Stakeholders & Roles
| Role        | Primary Responsibilities |
|-------------|-------------------------|
| Student / Resident | Submit repair tickets, attach context, cancel pending requests. |
| Dorm Manager | Review incoming requests, change status to UNDER_REVIEW, assign technicians, cancel duplicates, manage dorm coverage. |
| Technician | View assigned jobs, update progress (IN_PROGRESS) and completion (COMPLETED), leave notes. |
| System Administrator | Manage infrastructure, configure dorm data, provision accounts. *(Outside current scope but noted for completeness.)* |

## 3. Functional Requirements
1. **Authentication & Authorization**
   - Users authenticate with username/password; system issues JWT tokens.
   - Role-based access control enforces permissions: students can only act on their own tickets, managers can view all, technicians see assigned items.

2. **Request Submission (Student)**
   - Fields: title, description, dorm/block, room, category, priority (LOW → URGENT), preferred entry time, optional asset tag & photo URL.
   - System records NEW status and creates an initial timeline entry.
   - Student can cancel a request before completion; cancellation clears manager/technician assignments.

3. **Request Board & Filtering (All Roles)**
   - List requests with filters: status, dorm keyword, “mine” (manager-only) to show items managed by the logged-in manager.
   - Each row shows title, dorm, priority, status, assigned technician (if any), last updated timestamp, and context snippet.

4. **Assignment Workflow (Manager)**
   - Manager may move NEW requests to UNDER_REVIEW.
   - Manager can assign a technician: choose technician from directory, set completion target, add note; operation switches status to ASSIGNED and writes timeline entry.
   - Manager may cancel requests (e.g., duplicates) with notes.

5. **Technician Progress Updates**
   - Technician view lists only requests assigned to them.
   - Allowed transitions: ASSIGNED → IN_PROGRESS, IN_PROGRESS → COMPLETED, each with optional notes.

6. **Timeline & Audit Trail**
   - Every status change or assignment creates an entry with actor, timestamp, status, note.
   - Timeline is displayed chronologically within request responses.

7. **User Directory (Manager)**
   - Managers can fetch a list of technicians (id, name, contact) via `/api/users?role=TECHNICIAN` for assignment UI.

8. **Demo Data / Seeding**
   - Provide default accounts: `alicelee` (student), `briantan` (student), `dorm_manager` (manager), `tech_mario` (technician) with password `Password!23`.
   - Include sample requests illustrating different workflow stages.

## 4. Non‑Functional Requirements
- **Security**: JWT-based stateless sessions; passwords stored using BCrypt; CORS restricted via configuration.
- **Usability**: Responsive dashboard (desktop/tablet), clear status chips, accessible colour contrast.
- **Reliability**: All state‑changing operations must be transactional; invalid transitions produce meaningful errors.
- **Maintainability**: Backend follows layered architecture (controller/service/repository). Frontend uses React Context for auth and modular components.
- **Portability**: Backend uses Maven Wrapper and H2 default so it runs without external DB; MySQL schema provided for production.

## 5. Deliverables for Repository
1. Source code (`backend/`, `frontend/`).
2. Infrastructure scripts: Maven wrapper, environment configuration (`application.yml`), Vite config.
3. Documentation: `README.md`, this Requirements document, `docs/FunctionalDocument.md`, `docs/TechnicalDocument.md`.
4. Database schema dump (`db/dorm_maintenance_schema.sql`).
5. Instructions for running locally (`./mvnw spring-boot:run`, `npm run dev`) and optional Swagger/H2 console access.

## 6. Out‑of‑Scope / Future Enhancements
- Email/SMS notifications for status updates.
- File uploads for photos.
- Analytics dashboard and SLA tracking.
- Mobile/PWA client for technicians.
- Admin panel for managing dorm metadata and user provisioning.

This requirements document can be linked in your GitHub repository to explain the project scope to reviewers or instructors.
