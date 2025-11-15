# Live Demo Script (5–10 minutes)

Use this script during the graded presentation to demonstrate that the application deploys cleanly, the framework is used correctly, and the core personas are supported. The flow assumes the backend is running on `:8080` and the frontend on `:5173` (or `:4173` when serving `dist`).

## 0. Setup (1 min)

1. Show the terminal tab running `java -jar backend/target/dorm-maintenance-backend-0.0.1-SNAPSHOT.jar`.
2. Show the frontend tab (`npm run dev` or static host) and confirm the login page appears.
3. Mention seeded credentials are documented in the README and in the database dump.

## 1. Student files a request (2 min)

1. Log in as `alicelee / Password!23`.
2. Click “New Request”, submit a ticket (e.g., “Aircon dripping”) and attach preferred entry time + category.
3. Highlight validation (required fields) and the fact that the new request appears in the personal list with status `NEW`.
4. Log out.

## 2. Dorm manager triages (2 min)

1. Log in as `dorm_manager / Password!23`.
2. Filter the board by `NEW` status and open the ticket created above.
3. Assign technician `tech_mario`, set a completion target date, and add a note.
4. Show the status change to `ASSIGNED` and mention that the backend enforces role-based transitions (see Technical Document §5).

## 3. Technician updates progress (2 min)

1. Log in as `tech_mario / Password!23`.
2. Open the assigned ticket, add a timeline entry (e.g., “Replaced valve”), and move status to `COMPLETED`.
3. Point out the chronological timeline component and the backend API that powers it.

## 4. Highlight technical features (2 min)

- Show Swagger UI (`/swagger-ui`) or the technical document diagram to emphasize API contracts.
- Hit `curl http://localhost:8080/actuator/health` to prove the Actuator probe works (bonus points for ops readiness).
- Mention automated tests: run `./backend/mvnw test` to highlight the auth + workflow coverage and `npm run test` for the React unit test.
- Mention lint/build checks (`npm run lint && npm run build`) and that deployment artefacts or `docker compose up --build` were produced just before the demo.
- Optional: open the H2 console or MySQL dump to prove data persistence options.

Close with a recap of the architecture (React 19 + Vite frontend, Spring Boot 3 backend, JWT security, MySQL/H2) and invite questions.
