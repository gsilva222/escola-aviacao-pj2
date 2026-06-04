# Escola de Aviacao - Sistema de Gestao

Projeto Java multi-modulo para gerir uma escola de aviacao.

## Modulos

- `shared`: entidades e DAL
- `backend-common`: servicos de negocio
- `desktop`: aplicacao Swing
- `web`: API Spring Boot

## Como executar

Consulta o guia: [COMO_CORRER_O_PROJETO.md](./COMO_CORRER_O_PROJETO.md)

## API Web

O modulo `web` expoe uma API REST Spring Boot em `http://localhost:8080/api`.

Endpoints principais:

- `POST /api/auth/register` - cria conta `ADMIN` ou `STUDENT`
- `POST /api/auth/login` - devolve token JWT
- `GET|POST|PUT|DELETE /api/bo/courses` - cursos
- `GET|POST|PUT|DELETE /api/bo/students` - alunos
- `GET|POST|PUT|DELETE /api/bo/instructors` - instrutores
- `GET|POST|PUT|DELETE /api/bo/aircraft` - aeronaves
- `GET|POST|PUT|DELETE /api/bo/flights` - voos
- `GET|POST|PUT|DELETE /api/bo/payments` - pagamentos
- `GET|POST|PUT|DELETE /api/bo/evaluations` - avaliacoes
- `GET|POST|PUT|DELETE /api/bo/maintenance` - manutencoes

Documentacao interativa:

```text
http://localhost:8080/api/swagger
```

Exemplo de login:

```powershell
curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
```

Exemplo de chamada autenticada:

```powershell
curl http://localhost:8080/api/bo/students `
  -H "Authorization: Bearer <TOKEN>"
```

## Base de Dados Web

Como a API usa `spring.jpa.hibernate.ddl-auto=validate`, a BD PostgreSQL deve existir antes de arrancar a aplicacao. O schema de referencia esta em:

```text
web/src/main/resources/db/schema-postgresql.sql
```

Variaveis obrigatorias para correr o modulo `web` fora dos testes:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/escola_aviacao"
$env:DB_USER="escola_user"
$env:DB_PASSWORD="<password>"
$env:JWT_SECRET="<segredo-com-pelo-menos-32-caracteres>"
$env:SEED_ADMIN_PASS="admin123"
$env:SEED_STUDENT_PASS="aluno123"
```
