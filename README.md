# User Management (Spring Boot + Maven)

Features:
- REST CRUD for users
- Spring Security with JWT access tokens and refresh tokens
- Role management (ROLE_USER, ROLE_ADMIN) and admin endpoints
- Password hashing with BCrypt
- MapStruct DTO <-> entity mapping
- Custom validation annotations (unique username/email)
- MySQL integration and Testcontainers for integration tests
- Dockerfile, docker-compose (MySQL + app)
- Kubernetes manifests (Secret, Deployment, Service)
- CI: GitHub Actions workflow to run tests and build and publish image to ghcr.io

Quickstart (local, Docker Compose):
1. Build & run with docker-compose:
   - docker-compose up --build
   - App: http://localhost:8080

2. Provide ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_PASSWORD environment variables to bootstrap an initial admin user on first startup (or create an admin via registration and assign role manually).

3. API:
   - POST /api/auth/register { username, email, password, fullName }
   - POST /api/auth/login { username, password } -> { accessToken, refreshToken }
   - POST /api/auth/refresh { refreshToken } -> rotate token
   - POST /api/auth/logout { refreshToken }
   - GET /api/users (authenticated; ROLE_USER or ROLE_ADMIN)
   - Admin endpoints (ROLE_ADMIN): /api/admin/**

Notes:
- Replace app.jwt.secret with a strong secret in production.
- Use Kubernetes secrets / environment variables for DB credentials.

Tests: include unit, controller, and integration tests (Testcontainers). Please run `mvn -B clean verify` locally or via CI.

---
