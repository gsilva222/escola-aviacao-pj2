# Escola de Aviação

Sistema de gestão da escola de aviação com três componentes:

| Módulo | Descrição |
|--------|-----------|
| **web** | API REST Spring Boot (porta 8080, contexto `/api`) |
| **frontend** | Interface React (porta 5173 em desenvolvimento) |
| **desktop** | Aplicação Swing que consome a API |

Módulos partilhados: `shared` (entidades/DAL) e `backend-common` (serviços).

## Requisitos

- Java 17+
- Maven 3.9+
- Node.js 20+ (frontend)
- PostgreSQL 16 (ou Docker)

## Credenciais de teste

- **BackOffice:** `admin` / `admin123`
- **Alunos:** email do aluno / `aluno123`

## Arranque completo (web + desktop)

Para ter tudo a funcionar em conjunto, são necessários **3 terminais** (com a API a correr).

### Opção A — Web com Docker + desktop local

**Terminal 1 — stack web:**

```bash
cp .env.example .env          # Windows: Copy-Item .env.example .env
docker compose up --build
```

**Terminal 2 — desktop** (com a API já disponível em `http://localhost:8080/api`):

```bash
mvn -pl desktop -am exec:java
```

### Opção B — Tudo local (sem Docker para API/frontend)

**Terminal 1 — base de dados** (só PostgreSQL via Docker):

```bash
docker compose up postgres -d
```

Alternativa sem Docker: criar a BD manualmente:

```bash
createdb aeroschool
psql -d aeroschool -f web/src/main/resources/db/schema-postgresql.sql
```

**Terminal 2 — API** (perfil `dev`, sem variáveis de ambiente):

```bash
mvn -pl web -am install -DskipTests
cd web
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

No PowerShell, usar aspas no argumento do perfil: `mvn spring-boot:run "-Dspring-boot.run.profiles=dev"`

O ficheiro `web/src/main/resources/application-dev.properties` já define ligação à BD, JWT e seed.

**Terminal 3 — frontend:**

```bash
cd frontend
npm install
npm run dev
```

Abrir http://localhost:5173

**Terminal 4 — desktop:**

```bash
mvn -pl desktop -am exec:java
```

---

## Web

### Arranque rápido com Docker

```bash
cp .env.example .env          # Windows: Copy-Item .env.example .env
docker compose up --build
```

| Serviço   | URL |
|-----------|-----|
| API       | http://localhost:8080/api |
| Swagger   | http://localhost:8080/api/swagger |
| Health    | http://localhost:8080/api/actuator/health |
| Frontend  | http://localhost:5173 |

Serviços definidos em `docker-compose.yml`: `postgres`, `api`, `frontend`.

### Arranque local (sem Docker)

#### 1. Base de dados

```bash
docker compose up postgres -d
```

Ou, com PostgreSQL instalado localmente:

```bash
createdb aeroschool
psql -d aeroschool -f web/src/main/resources/db/schema-postgresql.sql
```

#### 2. API

**Recomendado** — perfil de desenvolvimento (valores pré-configurados):

```bash
mvn -pl web -am install -DskipTests
cd web
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

No PowerShell: `mvn spring-boot:run "-Dspring-boot.run.profiles=dev"`. O comando `mvn -pl web -am spring-boot:run` a partir da raiz falha no Windows porque o Maven tenta arrancar o projeto pai em vez do módulo `web`.

**Alternativa** — variáveis de ambiente (ver `.env.example`):

```bash
mvn -pl web -am install -DskipTests
cd web

# Linux/macOS
export $(grep -v '^#' ../.env | xargs)
mvn spring-boot:run

# Windows PowerShell (exemplo)
$env:DB_URL = "jdbc:postgresql://localhost:5432/aeroschool"
$env:DB_USER = "aeroschool"
$env:DB_PASSWORD = "aeroschool"
$env:JWT_SECRET = "change_me_in_production_min_32_chars!!"
$env:SEED_ADMIN_PASS = "admin123"
$env:SEED_STUDENT_PASS = "aluno123"
mvn spring-boot:run
```

#### 3. Frontend

```bash
cd frontend
npm install
npm run dev
```

Em desenvolvimento, o Vite (`frontend/vite.config.ts`) faz proxy de `/api` para `http://localhost:8080`. A variável `VITE_API_BASE_URL` (ver `.env.example`) só é necessária em builds de produção.

---

## Desktop

Aplicação Swing em `desktop/`. Liga-se à API REST; o URL base é configurável em `desktop/src/main/resources/aeroschool.properties` (por defeito `http://localhost:8080/api`).

### Arrancar

Com a API a correr:

```bash
mvn -pl desktop -am exec:java
```

### Alternativa (JAR)

```bash
mvn -pl desktop -am package -DskipTests
java -jar desktop/target/aeroschool-desktop.jar
```

### URL da API

Para apontar para outro servidor:

```bash
mvn -pl desktop -am exec:java -Daeroschool.api.baseUrl=http://localhost:8080/api
```

Ou editar `aeroschool.properties` na pasta de arranque.

---

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
# Web
mvn -pl web -am test

# Desktop
mvn -pl desktop -am test

# Todos os módulos
mvn test
```
