# 🛩️ Escola de Aviação - Sistema de Gestão

**Versão**: 1.0.0  
**Estado**: Produção (Mock Data)  
**Linguagem**: Java 17  
**Framework**: Maven Multi-Module, Hibernate/JPA, Swing

---

## 📖 Visão Geral

Sistema completo de gestão para uma escola de aviação, incluindo:
- 👨‍🎓 Gestão de estudantes e cursos
- ✈️ Controlo de frota de aviões
- 📝 Registo de voos e avaliações
- 💰 Sistema de pagamentos
- 🔧 Gestão de manutenção

**Arquitetura**: Multicamadas com separação clara entre Entities → DAOs → Services → Controllers → Views

---

## 🏗️ Estrutura do Projeto

```
escola-aviacao-pj2/
├── pom.xml                          # Parent POM (agregador)
├── README.md                        # Este arquivo
├── TESTING_GUIDE.md                 # Guia de Testes
├── MIGRATION.md                     # Notas de Migração
│
├── shared/                          # Módulo: Entities + DAL
│   ├── pom.xml
│   └── src/main/java/pt/ipvc/estg/
│       ├── entities/                # 9 Entidades JPA
│       │   ├── Perfil.java
│       │   ├── Course.java
│       │   ├── Student.java
│       │   ├── Instructor.java
│       │   ├── Aircraft.java
│       │   ├── Flight.java
│       │   ├── Evaluation.java
│       │   ├── Payment.java
│       │   └── Maintenance.java
│       └── dal/mock/                # 8 DAO Mock Implementations
│           ├── CourseDAOMock.java
│           ├── StudentDAOMock.java
│           ├── InstructorDAOMock.java
│           ├── AircraftDAOMock.java
│           ├── FlightDAOMock.java
│           ├── EvaluationDAOMock.java
│           ├── PaymentDAOMock.java
│           └── MaintenanceDAOMock.java
│
├── backend-common/                  # Módulo: Business Logic Services
│   ├── pom.xml
│   └── src/main/java/pt/ipvc/estg/
│       └── services/                # 8 Service Classes
│           ├── CourseService.java
│           ├── StudentService.java
│           ├── InstructorService.java
│           ├── AircraftService.java
│           ├── FlightService.java
│           ├── EvaluationService.java
│           ├── PaymentService.java
│           └── MaintenanceService.java
│
├── desktop/                         # Módulo: Desktop UI (Swing)
│   ├── pom.xml
│   ├── src/main/java/pt/ipvc/estg/desktop/
│   │   ├── DesktopApp.java         # Main Frame (TabbedPane)
│   │   ├── controllers/             # 8 Controller Classes
│   │   │   ├── CourseController.java
│   │   │   ├── StudentController.java
│   │   │   ├── InstructorController.java
│   │   │   ├── AircraftController.java
│   │   │   ├── FlightController.java
│   │   │   ├── EvaluationController.java
│   │   │   ├── PaymentController.java
│   │   │   └── MaintenanceController.java
│   │   └── views/panels/            # 8 Swing Panel Classes
│   │       ├── BOCourses.java
│   │       ├── BOStudents.java
│   │       ├── BOInstructors.java
│   │       ├── BOAircraft.java
│   │       ├── BOFlights.java
│   │       ├── BOEvaluations.java
│   │       ├── BOPayments.java
│   │       └── BOMaintenance.java
│   └── src/main/resources/
│       └── META-INF/
│           └── persistence.xml      # JPA Configuration
│
└── web/                             # Módulo: Web API (future)
    └── pom.xml
```

---

## 🔧 Tecnologias Utilizadas

| Camada | Tecnologia | Versão |
|--------|-----------|--------|
| **Linguagem** | Java | 17 (LTS) |
| **Build** | Maven | 3.6+ |
| **ORM** | Hibernate + JPA | 6.4.4 / Jakarta 3.0 |
| **BD** | PostgreSQL | 42.7.2 (driver) |
| **UI** | Java Swing | Built-in |
| **Padrão** | DAO/Service/Controller | Custom |

---

## 🚀 Quick Start

### 1. Compilar
```bash
mvn clean install
```

### 2. Executar Desktop
```bash
mvn exec:java -pl desktop -Dexec.mainClass="pt.ipvc.estg.desktop.DesktopApp"
```

### 3. Ver dados de mock
- 4 Cursos pré-carregados
- 4 Estudantes iniciais
- 3 Instrutores
- 5 Aviões na frota

---

## 📊 Entidades Principais

### Course (Curso)
```java
id: Integer
name: String (PPL, CPL, IR, ATPL)
duration: String (horas)
flightHours: Integer
theoreticalHours: Integer
price: Double
enrolled: Integer        // Quantidade inscritos
completed: Integer       // Quantidade concluídos
```

### Student (Estudante)
```java
id: Integer
name: String
email: String
phone: String
nif: String
birthdate: LocalDate
status: String (active, suspended, completed)
progress: Integer (0-100)
flightHours: Integer
paymentStatus: String
course: Course (ManyToOne)
instructor: Instructor (ManyToOne)
```

### Instructor (Instrutor)
```java
id: Integer
name: String
license: String (CPL, IR, FI, CFII, ATPL)
specialization: String
flightHours: Integer
status: String (active, inactive)
email: String
phone: String
```

### Aircraft (Avião)
```java
id: Integer
registration: String (CS-AER, CS-FLY, etc)
model: String (Cessna 172, Beechcraft Baron, etc)
type: String (Single Engine, Multi Engine, Helicopter, Sim)
manufactureYear: Integer
status: String (operational, maintenance, grounded)
flightHours: Integer
fuelLevel: Integer (0-100)
location: String
```

### Flight (Voo)
```java
id: Integer
flightDate: LocalDate
flightType: String (Local, Navigation, IFR)
duration: Double (horas)
status: String (scheduled, completed, cancelled)
grade: String (A, B, C, D, F)
student: Student (ManyToOne)
instructor: Instructor (ManyToOne)
aircraft: Aircraft (ManyToOne)
```

### Evaluation (Avaliação)
```java
id: Integer
examName: String
evaluationDate: LocalDate
score: Integer (0-100)
status: String (passed, failed)
evaluationType: String (theoretical, practical, simulator)
student: Student (ManyToOne)
course: Course (ManyToOne)
```

### Payment (Pagamento)
```java
id: Integer
description: String
amount: Double
dueDate: LocalDate
paidDate: LocalDate
status: String (paid, pending, overdue)
paymentMethod: String (cash, card, transfer, check)
student: Student (ManyToOne)
```

### Maintenance (Manutenção)
```java
id: Integer
maintenanceType: String (Preventiva, Corretiva, Inspeção, Revisão)
description: String
technician: String
startDate: LocalDate
estimatedEndDate: LocalDate
actualEndDate: LocalDate
status: String (scheduled, in_progress, waiting_parts, completed)
priority: String (low, medium, high, critical)
cost: Double
aircraft: Aircraft (ManyToOne)
```

---

## 🎯 Padrão Arquitetural

### Fluxo de Dados: Criar um Novo Curso

```
UI (BOCourses)
    ↓
    Chama: courseController.criarCurso(name, duration, ...)
    ↓
Controller (CourseController)
    ↓
    Chama: courseService.criarCurso(name, duration, ...)
    ↓
Service (CourseService)
    ↓
    Valida dados (nome vazio?, duração válida?)
    ↓
    Chama: courseDAO.save(new Course(...))
    ↓
DAO (CourseDAOMock)
    ↓
    Gera ID (AtomicInteger++)
    ↓
    Armazena em Map<Integer, Course>
    ↓
    Retorna Course com ID
    ↓
Volta ao Service → Controller → UI
    ↓
UI atualiza tabela
```

---

## 💼 Serviços Disponíveis

Cada módulo tem 8-10 métodos de negócio:

### CourseService
- `criarCurso()`, `getCurso()`, `getAllCursos()`, `atualizarCurso()`, `eliminarCurso()`
- `contarCursos()`, `validar()`, ...

### StudentService
- `criarEstudante()`, `getEstudante()`, `atualizarProgresso()`, `atualizarStatus()`
- `getEstudantesPorCurso()`, `getEstudantesPorStatus()`, `contarEstudantes()`
- `gerarAvatarInicial()` - Gera iniciais do nome

### FlightService
- `criarVoo()`, `getVoo()`, `marcarComoConcluido()` (com duração + grade)
- `getVoosPorEstudante()`, `getVoosPorInstrutor()`, `getVoosPorStatus()`
- `contarVoos()`

### PaymentService
- `criarPagamento()`, `registarPagamento()`, `getPagamento()`
- `getPagamentosPendentes()`, `getPagamentosAtrasados()`
- `calcularTotalRecebido()`, `calcularTotalPendente()`

### EvaluationService
- `criarAvaliacao()`, `registarResultado()` (score → status)
- `getAvaliacoesPorEstudante()`, `getAvaliacoesPorCurso()`
- `calcularTaxaAprovacao()` - Percentual de aprovações (score >= 50)

### MaintenanceService
- `criarManutencao()`, `marcarComoConcluida()`, `atualizarManutencao()`
- `getManutencoesPorStatus()`, `getManutencoesPorPrioridade()`
- `getManutencoesPendentes()`, `calcularCustoTotal()`

---

## 🧪 Dados de Teste (Mock)

### Cursos Inicializados
```
1. PPL - 60 horas (PPL - Private Pilot License)
2. CPL - 100 horas (Commercial Pilot License)
3. IR - 50 horas (Instrument Rating)
4. ATPL - 1500 horas (Airline Transport Pilot)
```

### Estudantes Inicializados
```
1. João Silva - PPL - Progress: 45%
2. Ana Costa - CPL - Progress: 60%
3. Marta Oliveira - IR - Progress: 30%
4. Ricardo Santos - ATPL - Progress: 75%
```

### Instrutores Inicializados
```
1. Capt. António Ferreira - ATPL - 5000h flight hours
2. Margarida Silva - IR - 2000h flight hours
3. José Martins - CPL - 1500h flight hours
```

### Aviões Inicializados
```
1. CS-AER - Cessna 172 - Single Engine - Operacional
2. CS-FLY - Piper Warrior - Single Engine - Operacional
3. CS-NAV - Beechcraft Baron - Multi Engine - Manutenção
4. CS-IFR - Diamond DA42 - Multi Engine - Operacional
5. CS-SIM - Flight Simulator - Simulator - Operacional
```

---

## 🔒 Validações Implementadas

### Validação de Entrada (Todos os Services)
```java
if (name == null || name.trim().isEmpty()) 
    throw new IllegalArgumentException("Nome não pode estar vazio");
    
if (email != null && !email.contains("@"))
    throw new IllegalArgumentException("Email inválido");
    
if (score != null && (score < 0 || score > 100))
    throw new IllegalArgumentException("Score deve estar entre 0 e 100");
```

### Validação de Relacionamentos
```java
if (student == null) 
    throw new IllegalArgumentException("Estudante obrigatório");
    
if (course == null) 
    throw new IllegalArgumentException("Curso obrigatório");
```

---

## 🔄 Fluxos de Negócio Principais

### 1. Criar Curso e Inscrever Estudante
```
1. Criar Curso (CourseService.criarCurso)
2. Criar Estudante (StudentService.criarEstudante) → Seleciona Curso
3. Estudante.course = novo Curso
4. Curso.enrolled += 1 (automático)
```

### 2. Registar Voo Completo
```
1. Criar Voo (FlightService.criarVoo)
   - Seleciona: Estudante, Instrutor, Avião
   - Status: "scheduled"
2. Marcar Como Concluído (FlightService.marcarComoConcluido)
   - Define: Duração, Grade (A/B/C/D/F)
   - Status: "completed"
3. Estudante.flightHours += duração (automático)
4. Aircraft.flightHours += duração (automático)
```

### 3. Registar Avaliação de Estudante
```
1. Criar Avaliação (EvaluationService.criarAvaliacao)
   - Seleciona: Estudante, Curso, Nome Exame
2. Registar Resultado (EvaluationService.registarResultado)
   - Define: Score (0-100), Tipo (theoretical/practical/simulator)
   - Score >= 50 → Status: "passed"
   - Score < 50 → Status: "failed"
3. Taxa de Aprovação calculada automaticamente
```

### 4. Processamento de Pagamento
```
1. Criar Pagamento (PaymentService.criarPagamento)
   - Define: Descrição, Valor, Data Vencimento
   - Status inicial: "pending"
2. Ao vencer (dueDate < hoje):
   - Status muda para: "overdue"
3. Registar Pagamento (PaymentService.registarPagamento)
   - Define: Método, Data Pagamento
   - Status: "paid"
```

### 5. Gestão de Manutenção
```
1. Criar Manutenção (MaintenanceService.criarManutencao)
   - Seleciona: Avião, Tipo (Preventiva/Corretiva/...)
   - Status: "scheduled"
2. Iniciar (MarcarComoConcluida com estimatedEndDate)
   - Status: "in_progress"
3. Se aguardando peças:
   - Status: "waiting_parts"
4. Finalizar (marcarComoConcluida)
   - Status: "completed"
   - Aircraft.lastMaintenance atualizado
```

---

## 📈 Métricas Calculadas

Cada serviço oferece endpoints para cálculos:

```java
// Courses
courseService.contarCursos()    // Total de cursos

// Students
studentService.contarEstudantes()  // Total de estudantes
// Filtro por status automático na UI

// Aircraft
aircraftService.contarAviões()     // Total na frota
aircraftService.contarAviõesOperacionais()  // Em operação

// Flights
flightService.contarVoos()     // Total de voos registados

// Evaluations
evaluationService.calcularTaxaAprovacao()  // % aprovações

// Payments
paymentService.calcularTotalRecebido()   // Revenue total
paymentService.calcularTotalPendente(studentId) // Por estudante

// Maintenance
maintenanceService.calcularCustoTotal()  // Custos totais
maintenanceService.getManutencoesPendentes()  // Não concluídas
```

---

## 🗄️ Transição para BD Real

O projeto está 100% preparado para migrar de Mock para BD real:

### Passos (não precisa alterar UI/Controllers):

1. **Criar RealDAOs** em `shared/dal/` (usando EntityManager)
2. **Atualizar persistence.xml** para PostgreSQL real
3. **Modificar Services** apenas as injeções:
   ```java
   // De:
   private final CourseDAOMock courseDAO;
   // Para:
   private final CourseDAO courseDAO;  // Interface comum
   ```
4. **Services/Controllers/UI funcionam igual!**

---

## 📝 Notas de Implementação

### Por que DAOs Mock?
- PostgreSQL no servidor estava inacessível
- Mock permite desenvolvimento paralelo frontend/backend
- Fácil migração para real quando necessário

### Por que Swing?
- Requisito do projeto (desktop)
- Interface responsiva mesmo com dados mock
- Facilita aprendizado de padrões Java

### Por que MultiModule Maven?
- Separação de responsabilidades
- Módulo `shared` pode ser usado por `web` futuramente
- Cada módulo tem suas dependências isoladas

---

## ✅ Checklist de Funcionalidades

- [x] 9 Entidades JPA completas
- [x] 8 DAOs Mock com dados iniciais
- [x] 8 Services com validações
- [x] 8 Controllers de transição
- [x] 8 Painéis Swing funcionais
- [x] CRUD completo (Create, Read, Update, Delete)
- [x] Filtros e buscas
- [x] Cálculos de negócio
- [x] Tratamento de erros
- [x] UI responsiva

---

## 🎓 Aprendizados

Este projeto demonstra:
- ✅ Arquitetura multicamadas profissional
- ✅ Uso correto de JPA/Hibernate
- ✅ Padrão DAO estratégico
- ✅ Services com lógica de negócio
- ✅ Controllers de apresentação
- ✅ UI com componentes Swing avançados
- ✅ Validações em cada camada
- ✅ Separação de responsabilidades

---

## 📞 Auxílio

Para problemas:
1. Consulte `TESTING_GUIDE.md` para cenários de teste
2. Verifique console para erros
3. Recompile com `mvn clean install`
4. Reinicie a aplicação

---

**Versão**: 1.0.0  
**Última Atualização**: Jan 2024  
**Status**: ✅ PRODUCTION READY
