# Team Task Tracker

A small internal app for weekly task assignments (GTPS, WIP/IP, VOIP), daily completion and additional help.

- **Backend/**: Spring Boot 4, Spring Security (JWT), Spring Data JPA, MySQL
- **Frontend/**: React, React Router, Axios, Vite

## Concepts

| | Official weekly assignment | Additional help |
|---|---|---|
| Meaning | "This agent is responsible for this task this week" | "This agent also helped with this task on this day" |
| Created by | Support | The agent |
| Table | `weekly_assignments` (+ `daily_completions`) | `help_records` |

Help records are not linked to assignments, so recording help never changes an official assignment.

## Rules

- Support can assign any number of agents to any task type. The only restriction is that the same agent can't have the same task twice in the same week.
- Agents can only see and complete their own assignments, and only for **today**.
- An assignment that already has completed days cannot be removed, so its history is kept.
- An agent cannot record help for a task they are officially assigned to that week.
- Weeks start on Monday.

## Running locally

Requirements: Java 21+, Node 20+, a MySQL server on `localhost:3306`.

```bash
# Backend (http://localhost:8080). The `tasktracker` database is created automatically.
cd Backend
./mvnw spring-boot:run

# Frontend (http://localhost:5173). /api is proxied to the backend.
cd Frontend
npm install
npm run dev
```

On first start a Support account is created: **support / support123**. Log in, change its password on the
Users page, then add the agents.

### Configuration (environment variables)

| Variable | Default |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/tasktracker?createDatabaseIfNotExist=true&serverTimezone=UTC` |
| `DB_USERNAME` / `DB_PASSWORD` | `root` / *(empty)* |
| `JWT_SECRET` | a development value, **set a real one (32+ characters) outside development** |
| `SUPPORT_USERNAME` / `SUPPORT_PASSWORD` | `support` / `support123` (only used when the users table is empty) |

### Tests

```bash
cd Backend
./mvnw test
```

The tests use an in-memory H2 database, so they don't need MySQL.

## API overview

| Group | Endpoints | Role |
|---|---|---|
| Auth | `POST /api/auth/login`, `GET /api/auth/me` | public / any |
| Users | `GET/POST /api/users`, `PATCH /api/users/{id}` | SUPPORT |
| Assignments | `GET /api/assignments?weekStart=`, `POST /api/assignments`, `DELETE /api/assignments/{id}`, `POST /api/assignments/copy?from=&to=` | SUPPORT |
| My assignments | `GET /api/me/assignments?weekStart=`, `PUT/DELETE /api/me/assignments/{id}/completions/today` | AGENT |
| My help | `GET /api/me/help?from=&to=`, `POST /api/me/help`, `DELETE /api/me/help/{id}` | AGENT |
| Reports | `GET /api/reports/daily?date=`, `GET /api/reports/weekly?weekStart=`, `GET /api/reports/help?from=&to=` | SUPPORT |
