# Escola de Aviação - Sistema de Gestão

Projeto multi-módulo em Java para gestão de uma escola de aviação, com suporte para Desktop e Web.

## 📁 Estrutura do Projeto

```
escola-aviacao-pj2/
│
├── pom.xml                          # Parent POM (configuração principal)
│
├── shared/                           # Módulo compartilhado
│   ├── pom.xml
│   └── src/main/java/pt/ipvc/estg/
│       ├── entities/                 # Modelos de dados (JPA)
│       │   └── Perfil.java
│       └── dal/                      # Data Access Layer (DAO)
│           └── PerfilDAO.java
│
├── backend-common/                   # Lógica compartilhada
│   ├── pom.xml
│   └── src/main/java/pt/ipvc/estg/
│       └── services/                 # Serviços (Business Logic)
│           └── PerfilService.java
│
├── desktop/                          # Aplicação Desktop (Swing/JavaFX)
│   ├── pom.xml
│   └── src/main/java/pt/ipvc/estg/desktop/
│       ├── DesktopApp.java           # Classe principal
│       ├── ui/                       # Componentes da UI
│       ├── controllers/              # Lógica da UI
│       └── models/                   # View Models
│
└── web/                              # API REST (Spring Boot)
    ├── pom.xml
    └── src/main/java/pt/ipvc/estg/web/
        ├── WebApp.java               # Classe principal
        ├── api/                      # Controllers REST
        └── config/                   # Configurações
```

## 🏗️ Arquitetura

### Modelo em Camadas:

```
┌─────────────────────────────────────┐
│         UI (Desktop/Web)            │  ← Controllers, Views
├─────────────────────────────────────┤
│      Services (Backend-Common)      │  ← Lógica de Negócio
├─────────────────────────────────────┤
│    DAO / DAL (Shared)              │  ← Acesso aos Dados
├─────────────────────────────────────┤
│    Entities / Models (Shared)       │  ← Modelos de Dados (JPA)
├─────────────────────────────────────┤
│       Base de Dados (PostgreSQL)    │
└─────────────────────────────────────┘
```

## 🔄 Divisão de Responsabilidades

| Módulo | Responsabilidade |
|--------|-----------------|
| **shared** | Entidades JPA, DAOs, acesso direto à BD |
| **backend-common** | Serviços com lógica de negócio, validações |
| **desktop** | Interface gráfica Swing/JavaFX, controllers da UI |
| **web** | API REST em Spring Boot, endpoints HTTP |

## 🚀 Como Usar

### 1. Compilar o projeto

```bash
mvn clean install
```

### 2. Executar a aplicação Desktop

```bash
cd desktop
mvn exec:java -Dexec.mainClass="pt.ipvc.estg.desktop.DesktopApp"
```

Ou criar um jar executável:
```bash
mvn clean package
java -jar target/escola-aviacao-desktop-1.0-SNAPSHOT.jar
```

### 3. Executar a API Web

```bash
cd web
mvn spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

## 📦 Dependências Principais

- **Java 17+**
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Hibernate 6.4.4**
- **Spring Boot 3.2.4** (apenas para Web)

## 🛠️ Próximos Passos

1. **Desktop**: Criar a interface gráfica baseada nos mockups do Figma
2. **Entidades**: Adicionar mais modelos (Utilizador, Avião, Voo, etc.)
3. **Web**: Implementar endpoints REST para a API
4. **Autenticação**: Adicionar sistema de login/autenticação
5. **Testes**: Criar testes unitários e de integração
6. **BD**: Conectar ao servidor PostgreSQL quando disponível

## 📝 Notas

- A estrutura está pronta para growth, permitindo facilmente adicionar novos módulos
- Desktop e Web compartilham a mesma lógica de negócio (backend-common e shared)
- O padrão DAO faz abstração da BD, facilitando mudanças futuras
- MVP (Mock) disponível para desenvolvimento sem BD ativa
