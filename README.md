# Escola de Aviação — PJ2

Projeto multi-módulo Maven: **desktop** (Swing), **web** (API REST Spring Boot) e módulos partilhados.

## Módulo Web (API)

Base URL: `http://localhost:8080/api`

### Variáveis de ambiente

| Variável | Descrição |
|----------|-----------|
| `DB_URL` | JDBC PostgreSQL, ex. `jdbc:postgresql://localhost:5432/escola_aviacao` |
| `DB_USER` | Utilizador da base de dados |
| `DB_PASSWORD` | Password da base de dados |
| `JWT_SECRET` | Segredo JWT (mín. 32 caracteres) |
| `SEED_ADMIN_PASS` | Password do admin inicial (opcional em dev: `admin123`) |
| `SEED_STUDENT_PASS` | Password dos alunos criados pelo seed (default: `aluno123`) |
| `SEED_ENABLED` | `true` para criar contas e dados demo |
| `SEED_BUSINESS` | `true` para popular cursos/alunos/voos (default: `true`) |
| `UPLOAD_DIR` | Pasta de documentos (default: `./uploads`) |

### Base de dados

1. Criar base PostgreSQL vazia.
2. Executar `web/src/main/resources/db/schema-postgresql.sql`.
3. Definir `spring.jpa.hibernate.ddl-auto=validate` (já configurado).

### Arranque

```bash
mvn -pl web spring-boot:run
```

Swagger: `http://localhost:8080/api/swagger`

### Autenticação

- `POST /api/auth/register` — criar conta (primeiro `ADMIN`, depois `STUDENT` com `studentId`)
- `POST /api/auth/login` — obter JWT

Header: `Authorization: Bearer <token>`

### Endpoints principais

**BackOffice** (`ROLE_ADMIN`): `/api/bo/*`

- CRUD: courses, students, instructors, aircraft, flights, payments, evaluations, maintenance
- `GET /api/bo/reports/summary` — KPIs agregados
- `GET /api/bo/payments/summary` — totais de pagamentos
- `GET/POST/DELETE /api/bo/student-documents/{studentId}` — documentos do aluno

**FrontOffice** (`ROLE_STUDENT`): `/api/fo/*`

- `GET/PUT /api/fo/me` — perfil
- `GET /api/fo/dashboard` — resumo
- `GET /api/fo/flights`, `/api/fo/schedule`, `/api/fo/hours`
- `GET /api/fo/evaluations`
- `GET /api/fo/payments`, `/api/fo/payments/summary`
- `GET/POST/DELETE /api/fo/documents` — documentos do aluno autenticado

### Testes

```bash
mvn -pl web test
```

## Módulo Desktop

```bash
mvn -pl desktop exec:java -Dexec.mainClass="pt.ipvc.estg.desktop.DesktopApp"
```

(ou executar a classe principal configurada no IDE)
