# Escola de Aviação — Parte Web

API REST Spring Boot + frontend React para gestão da escola de aviação.

## Requisitos

- Java 17+
- Maven 3.9+
- Node.js 20+ (frontend)
- PostgreSQL 16 (ou Docker)

## Arranque rápido com Docker

```bash
cp .env.example .env
docker compose up --build
```

| Serviço   | URL                          |
|-----------|------------------------------|
| API       | http://localhost:8080/api    |
| Swagger   | http://localhost:8080/api/swagger |
| Health    | http://localhost:8080/api/actuator/health |
| Frontend  | http://localhost:5173        |

**Credenciais seed:** `admin` / `admin123` (BackOffice) · alunos: email do aluno / `aluno123`

## Arranque local (sem Docker)

### 1. Base de dados

```bash
createdb aeroschool
psql -d aeroschool -f web/src/main/resources/db/schema-postgresql.sql
```

### 2. API

Defina as variáveis (ver `.env.example`) e execute:

```bash
mvn -pl web -am spring-boot:run
```

### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Abra http://localhost:5173

## Endpoints principais

- **Auth:** `POST /auth/login`, `POST /auth/register`, `GET /auth/me`, `POST /auth/change-password`
- **BackOffice:** CRUD completo + `/bo/dashboard`, `/bo/users`, `/bo/profiles`, documentos de aluno
- **FrontOffice (aluno):** área completa com perfil e documentos
- **Frontend:** React com CRUD, gráficos, menus por perfil staff, paginação
- **CI:** GitHub Actions (`.github/workflows/ci.yml`)

## Segurança

- JWT Bearer token em todos os endpoints (excepto login, registo de aluno e health)
- Registo de `ADMIN` público desactivado por defeito (`AUTH_ALLOW_PUBLIC_ADMIN=false`)
- Apenas admins autenticados podem criar novos admins via `POST /auth/register`

## Testes

```bash
mvn -pl web -am test
```
