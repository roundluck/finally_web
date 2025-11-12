# Functional Document – Dormitory Maintenance Request Management System

## 1. Introduction
Dormitory Maintenance Request Management System (DMRMS) empowers NTU students, dorm managers, and technicians to collaborate on repair tasks through a unified portal. The solution eliminates manual paper trails, enforces role-based workflows, and keeps every stakeholder updated with timeline events.

## 2. Objectives & Success Metrics
| Objective | Success KPI |
| --- | --- |
| Replace email/spreadsheet-based maintenance intake | 100% of new tickets created via the portal | 
| Reduce assignment delays | ≥90% of NEW tickets assigned within 24 hours |
| Increase transparency for residents | Students can track request status & notes without calling front desk |
| Provide service insights | Managers view status distribution and technician workload at a glance |

## 3. Target Users & Personas
- **Students / Residents** – submit repair tickets, upload context, cancel if the issue self-resolves.
- **Dorm Managers** – triage requests, move to UNDER_REVIEW, assign technicians, cancel duplicates.
- **Technicians** – view assigned jobs, mark IN_PROGRESS and COMPLETED, add timeline notes.

## 4. Core Features
1. **Request Intake** (Students)
   - Create tickets with title, dorm/block, room, category, priority, preferred entry time, optional asset tag.
   - Attach supporting photo URL; system stores ISO timestamps.
   - Cancel pending tickets before work starts.
2. **Request Board** (All roles)
   - Filter by status, dorm, or “managed by me”.
   - Review timeline entries showing who changed what and when.
3. **Assignment Workflow** (Managers)
   - Move NEW tickets to UNDER_REVIEW.
   - Select a technician from live directory, set target completion, add note; system emits timeline entry and switches status to ASSIGNED.
4. **Technician Console**
   - Quickly start work (IN_PROGRESS) and close ticket (COMPLETED).
   - View outstanding jobs prioritized by urgency.
5. **Security & Session Handling**
   - JWT login, role-based route protection, HTTPS ready.
6. **Seeded Accounts** for demo & grading convenience.

## 5. Usage Scenarios
1. **Leaky Tap (Student)** – Alice submits “Bathroom tap leaking” with preferred entry slot. She tracks updates as manager moves it to review and assigns Mario.
2. **Power Failure (Manager)** – Manager sees multiple HIGH priority tickets for Block 8, cancels duplicates, assigns electrician with completion deadline.
3. **Aircon Service (Technician)** – Mario logs in, marks assigned request IN_PROGRESS upon arrival, attaches “filter replaced” note, and completes.

## 6. User Flows
1. **Submission Flow**
   1. Student logs in → Dashboard auto-filtered to own requests.
   2. Fills intake form → backend validates & stores timeline entry.
   3. Student sees NEW status immediately in board.
2. **Assignment Flow**
   1. Manager filters NEW tickets → clicks “Move to Review”.
   2. Opens Assign modal, selects technician & target date.
   3. API persists assignment, timeline logs manager note, technician now sees job.
3. **Technician Completion Flow**
   1. Technician logs in → board shows ASSIGNED/IN_PROGRESS only.
   2. Clicks “Start Work” (optional) then “Mark Complete”.
   3. Student receives COMLETED state and note.

## 7. Non-Functional Highlights
- Responsive layout (desktop + tablet) using CSS grid/flex.
- Accessible color palette with high contrast for chips.
- Optimistic refresh with refresh button to sync statuses.

## 8. Future Enhancements
- Push/email notifications when status changes.
- Attachment uploads (S3) instead of URLs.
- Analytics dashboard summarising average resolution time.
- Mobile PWA shell for technicians.
