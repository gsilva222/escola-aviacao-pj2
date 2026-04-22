# Migração de Estrutura - Arquivo do Projeto

## Status: ✅ COMPLETADO

### O que foi feito:

**Antes:**
```
escola-aviacao-pj2/
├── src/main/java/...
│   ├── Main.java
│   └── dal/Perfil.java
└── pom.xml (simples)
```

**Depois:**
```
escola-aviacao-pj2/
├── shared/              (Entidades + DAL)
├── backend-common/      (Serviços)
├── desktop/             (App Desktop Swing)
├── web/                 (API REST Spring Boot)
└── pom.xml (parent multi-módulo)
```

### Ficheiros Migrados:

| Ficheiro Original | New Location |
|------------------|-------------|
| `src/java/dal/Perfil.java` | `shared/java/entities/Perfil.java` ✨ (melhorado) |
| `src/resources/persistence.xml` | `shared/resources/META-INF/persistence.xml` ✨ (melhorado) |
| `src/java/Main.java` | `desktop/java/MainLegacy.java` ✨ (refatorizado) |

### Melhorias Aplicadas:

✅ **Perfil.java**
- Adicionados getters/setters completos
- Adicionados equals() e hashCode()
- Adicionado construtor com parâmetros
- Melhorada documentação

✅ **MainLegacy.java** (antes Main.java)
- Refatorizado para usar PerfilServiceMock (funciona sem BD)
- Pode-se facilmente trocar para PerfilService (com BD real)
- Melhorada documentação

✅ **persistence.xml**
- Adicionada configuração de dialect PostgreSQL
- Adicionada formatação SQL
- Melhorados comentários

### Nova Estrutura:

- **shared/**: Modelos (Entities) e DAO (acesso à BD)
- **backend-common/**: Serviços com lógica de negócio
- **desktop/**: Interface gráfica Swing
- **web/**: API REST Spring Boot

### Como Usar:

**Teste com dados em memória:**
```bash
cd desktop
mvn exec:java -Dexec.mainClass="pt.ipvc.estg.desktop.MainLegacy"
```

**Quando BD estiver disponível:**
- Trocar `PerfilServiceMock` por `PerfilService` nos Controllers
- O PerfilDAO real será ativado automaticamente

---

**Nota:** A pasta `src/` original pode ser removida - tudo foi migrado! ✨
