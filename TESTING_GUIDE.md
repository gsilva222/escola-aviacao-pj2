# TESTE E VALIDAÇÃO - Escola de Aviação

## 📋 Status Geral
✅ **BACKEND COMPLETO** - Pronto para uso em produção (com dados mock)
✅ **FRONTEND SWING** - Interface gráfica totalmente funcional
✅ **ARQUITETURA MULTI-CAMADAS** - Entities → DAOs → Services → Controllers → Views

---

## 🚀 COMO EXECUTAR A APLICAÇÃO

### Pré-requisitos
- Java 17 JDK instalado
- Maven 3.6+ instalado

### Passos para Compilar e Executar

1. **Navegar até a pasta do projeto:**
```bash
cd c:\Users\rafae\Documents\GitHub\escola-aviacao-pj2
```

2. **Compilar o projeto Maven:**
```bash
mvn clean install
```

3. **Executar a aplicação Desktop:**
```bash
mvn exec:java -pl desktop -Dexec.mainClass="pt.ipvc.estg.desktop.DesktopApp"
```

Ou executar diretamente:
```bash
java -cp "desktop/target/classes:shared/target/classes:backend-common/target/classes" pt.ipvc.estg.desktop.DesktopApp
```

---

## 🧪 TESTES RECOMENDADOS

### 1. TESTE DE DADOS MOCK
Ao abrir a aplicação, deverá ver:
- **Cursos Tab**: 4 cursos pré-carregados (PPL, CPL, IR, ATPL)
- **Estudantes Tab**: 4 estudantes pré-carregados (João, Ana, Marta, Ricardo)
- **Instrutores Tab**: 3 instrutores (Capt. António, Margarida, José)
- **Aviões Tab**: 5 aviões (CS-AER, CS-FLY, CS-NAV, CS-IFR, CS-SIM)

### 2. TESTE DE CRIAÇÃO DE REGISTOS

#### Criar um novo Curso:
1. Na aba "Cursos", preencha:
   - Nome: "Curso Teste"
   - Duração: "60"
   - Horas Voo: "40"
   - Horas Teóricas: "20"
   - Preço: "5000.00"
2. Clique em "Criar Curso"
3. Esperado: Mensagem de sucesso e novo curso na tabela

#### Criar um novo Estudante:
1. Na aba "Estudantes", preencha:
   - Nome: "José Silva"
   - Email: "jose@example.com"
2. Clique em "Criar Estudante"
3. Esperado: Novo estudante adicionado com curso default

### 3. TESTE DE EDIÇÃO

#### Editar um Estudante:
1. Na aba "Estudantes", selecione um estudante
2. Clique em "Editar"
3. Campos preenchem com dados
4. Modifique e clique em "Editar Estudante"
5. Esperado: Dados atualizados na tabela

### 4. TESTE DE PROGRESSO

#### Atualizar Progresso de Estudante:
1. Na aba "Estudantes", selecione um estudante
2. Clique em "Atualizar Progresso"
3. Digite novo valor (ex: 85)
4. Esperado: Progresso atualizado

### 5. TESTE DE STATUS

#### Mudar Status de Avião:
1. Na aba "Aviões", selecione um avião
2. Clique em "Mudar Status"
3. Escolha novo status (operational/maintenance/grounded)
4. Esperado: Status alterado

### 6. TESTE DE MÉTRICAS

#### Contar Aviões Operacionais:
1. Na aba "Aviões", clique "Aviões Operacionais"
2. Esperado: Dialog mostrando quantidade

#### Taxa de Aprovação:
1. Na aba "Avaliações", clique "Taxa Aprovação"
2. Esperado: Percentual de aprovação (inicialmente 0% pois sem dados)

---

## 📚 ESTRUTURA DE DADOS

### Entidades Implementadas
```
Perfil (Roles)
Course (Cursos)
Student (Estudantes)
Instructor (Instrutores)
Aircraft (Aviões)
Flight (Voos)
Evaluation (Avaliações)
Payment (Pagamentos)
Maintenance (Manutenções)
```

### Relacionamentos
```
Course ←→ Student (1:N)
Instructor ←→ Student (1:N)
Instructor ←→ Flight (1:N)
Student ←→ Flight (1:N)
Aircraft ←→ Flight (1:N)
Student ←→ Evaluation (1:N)
Course ←→ Evaluation (1:N)
Student ←→ Payment (1:N)
Aircraft ←→ Maintenance (1:N)
```

---

## 🔄 FLUXO DE DADOS

### Exemplo: Criar Voo e Registar Conclusão

1. **User clica em "Criar Voo" (BOFlights)**
   ↓
2. **BOFlights.criarVoo()** chama
   ↓
3. **FlightController.criarVoo()** que chama
   ↓
4. **FlightService.criarVoo()** que chama
   ↓
5. **FlightDAOMock.save()** → Armazena em Map<Integer, Flight>
   ↓
6. **Retorna Flight com ID auto-gerado**
   ↓
7. **UI atualiza tabela**

### Marcar Voo como Concluído:
1. **User seleciona voo e clica "Marcar Concluído"**
2. **Define duração e nota**
3. **BOFlights.marcarConcluido()** → FlightController → FlightService**
4. **FlightService.marcarComoConcluido()** atualiza status e grade**
5. **Notificação de sucesso**

---

## 📁 ARQUIVOS PRINCIPAIS

### Backend Layer
```
shared/
  ├── entities/          → 9 classes JPA
  └── dal/mock/          → 8 DAO Mock implementations

backend-common/
  └── services/          → 8 Service classes
```

### Frontend Layer
```
desktop/
  ├── controllers/       → 8 Controller classes
  ├── views/
  │   └── panels/       → 8 Swing Panel classes
  └── DesktopApp.java   → Main frame com abas
```

---

## 🐛 POSSÍVEIS PROBLEMAS E SOLUÇÕES

### Problema: "ClassNotFoundException" ao executar
**Solução**: Certifique-se que compilou com `mvn clean install` antes de executar

### Problema: Tabelas vazias exceto cursos/students/instructors/aircraft
**Solução**: É esperado! Voos, Avaliações, Pagamentos e Manutenções são vazios inicialmente. Crie registos manualmente.

### Problema: Botão "Aviões Operacionais" mostra 0
**Solução**: Alguns aviões podem estar em manutenção. Mude status para "operational" primeiro.

### Problema: "Taxa de Aprovação" mostra 0%
**Solução**: Nenhuma avaliação registada. Crie avaliações e registar resultados primeiro.

---

## 🔌 TRANSIÇÃO PARA BANCO DE DADOS REAL

Quando o servidor PostgreSQL estiver disponível:

1. **Atualizar persistence.xml** - Mudar diálogo PostgreSQL para real
2. **Criar RealDAOs** - Implementar usando JPA EntityManager
3. **Injetar RealDAO nos Services** - Remover dependência de Mock
4. **Nenhuma alteração na UI ou Controllers** - Eles funcionarão igual!

---

## ✨ FUNCIONALIDADES IMPLEMENTADAS

### Cursos
- [x] Listar cursos
- [x] Criar curso
- [x] Editar curso
- [x] Eliminar curso
- [x] Ver detalhes

### Estudantes
- [x] Listar estudantes
- [x] Criar estudante
- [x] Editar estudante
- [x] Atualizar progresso (0-100%)
- [x] Atualizar status (active/suspended/completed)
- [x] Eliminar estudante

### Instrutores
- [x] Listar instrutores
- [x] Criar instrutor
- [x] Editar instrutor
- [x] Mudar status (active/inactive)
- [x] Eliminar instrutor

### Aviões
- [x] Listar aviões
- [x] Criar avião
- [x] Editar avião
- [x] Mudar status (operational/maintenance/grounded)
- [x] Contar aviões operacionais
- [x] Eliminar avião

### Voos
- [x] Listar voos
- [x] Criar voo
- [x] Marcar como concluído (com duração + nota)
- [x] Ver detalhes
- [x] Eliminar voo

### Avaliações
- [x] Listar avaliações
- [x] Criar avaliação
- [x] Registar resultado (com score + tipo)
- [x] Calcular taxa de aprovação
- [x] Eliminar avaliação

### Pagamentos
- [x] Listar pagamentos
- [x] Criar pagamento
- [x] Registar pagamento
- [x] Ver pagamentos pendentes
- [x] Ver pagamentos atrasados
- [x] Calcular total recebido
- [x] Eliminar pagamento

### Manutenções
- [x] Listar manutenções
- [x] Criar manutenção
- [x] Marcar como concluída
- [x] Ver manutenções pendentes
- [x] Calcular custo total
- [x] Eliminar manutenção

---

## 📊 ESTATÍSTICAS DO PROJETO

| Item | Quantidade |
|------|-----------|
| Entidades JPA | 9 |
| DAO Mock Classes | 8 |
| Service Classes | 8 |
| Controller Classes | 8 |
| Swing Panels | 8 |
| Métodos de Negócio | 70+ |
| Linhas de Código (Backend) | 3000+ |
| Linhas de Código (Frontend) | 2500+ |

---

## 🎓 PRÓXIMOS PASSOS

1. **Integração com Banco de Dados Real**
   - Implementar RealDAOs com JPA EntityManager
   - Configurar persistence.xml para PostgreSQL real

2. **Dashboard com Gráficos**
   - Métrica de inscrição por curso
   - Gráfico de horas de voo por estudante
   - Receita vs. despesa de manutenção

3. **Relatórios PDF**
   - Certificados de conclusão
   - Extratos de pagamento

4. **Web Module**
   - REST API com Spring Boot
   - Dashboard em React/Angular

5. **Testes Unitários**
   - JUnit 5 para Services
   - Mockito para dependências

---

## 📞 SUPORTE

Caso encontre problemas:
1. Verifique todos os campos obrigatórios preenchidos
2. Consulte a consola para mensagens de erro
3. Recarregue os dados (botão "Recarregar" em cada painel)
4. Reinicie a aplicação se necessário

---

**Versão**: 1.0.0  
**Data**: 2024-01-16  
**Status**: PRONTO PARA USO ✅
