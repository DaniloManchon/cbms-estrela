# CLAUDE.md - Contexto do Projeto CBMS

## 📋 Visão Geral do Projeto

**Nome:** CBMS - Cadastro Beneficiário de Movimento Social

**Descrição:** Sistema web desenvolvido em Java/Spring Boot para gestão de beneficiários de uma ONG, com controle de coletas/entregas de cestas básicas e histórico completo de doações. O foco é coletar dados socioeconômicos detalhados para criar um perfil completo do beneficiário e sua família.

**Status:** Em desenvolvimento ativo - novas funcionalidades sendo adicionadas (coletas, doações, gerenciamento de beneficiários)

---

## 🛠 Stack Tecnológico

### Backend
- **Linguagem:** Java 21
- **Framework:** Spring Boot 4.x (MVC)
- **Persistência:** Spring Data MongoDB
- **Banco de Dados:** MongoDB (NoSQL - documentos)
- **Validação:** Jakarta Validation
- **Logging:** Log4j2
- **Build:** Maven

### Frontend
- **Templates:** Thymeleaf
- **CSS/UI:** Bootstrap 5 + Bootstrap Icons
- **JavaScript:** Vanilla JS para integrações (ViaCEP, Webcam)
- **Barcode:** JsBarcode para geração visual de códigos

### Testes
- **Framework:** JUnit 5
- **Mocking:** Mockito
- **Cobertura:** BeneficiarioServiceTest, ViewControllerTest

---

## 📁 Estrutura do Projeto

```
cbms-estrela/
├── src/main/java/com/estrela/cbms/
│   ├── model/                          # Entidades MongoDB
│   │   ├── Beneficiario.java          # Entidade principal (documento)
│   │   ├── Coleta.java                # Registro de coleta (cestas básicas)
│   │   ├── Doacoes.java               # Registro de doação
│   │   ├── Renda.java                 # Dados de renda familiar
│   │   ├── Moradia.java               # Dados de habitação
│   │   ├── EducacaoBens.java          # Educação e bens duráveis
│   │   ├── Endereco.java              # Estrutura de endereço
│   │   └── ... (outras entidades)
│   ├── repository/                     # Spring Data MongoDB
│   │   └── BeneficiarioRepository.java
│   ├── service/                        # Lógica de negócio
│   │   └── BeneficiarioService.java
│   ├── controller/                     # Controllers MVC
│   │   └── ViewController.java
│   └── CbmsApplication.java            # Classe principal
├── src/main/resources/
│   ├── templates/                      # Thymeleaf (HTML)
│   │   ├── index.html                 # Listagem de beneficiários
│   │   ├── cadastro_beneficiario.html # Formulário (novo/edit)
│   │   ├── perfil_beneficiario.html   # Perfil detalhado
│   │   └── fragmentos_comuns.html     # Componentes reutilizáveis
│   ├── static/                         # CSS, JS, imagens
│   │   ├── js/
│   │   │   ├── form-beneficiario.js
│   │   │   ├── masks.js
│   │   │   └── barcode-*.js
│   │   └── css/
│   └── application.properties
├── src/test/java/com/estrela/cbms/
│   ├── service/BeneficiarioServiceTest.java
│   ├── controller/ViewControllerTest.java
│   └── CbmsApplicationTests.java
├── pom.xml                             # Dependências Maven
└── CLAUDE.md                           # Este arquivo
```

---

## 🚀 Como Rodar o Projeto

### Pré-requisitos
- Java 21 instalado
- MongoDB rodando (localmente ou remoto)
- Maven (ou use `./mvnw`)

### Configuração
1. **MongoDB URI** em `src/main/resources/application.properties`:
   ```properties
   spring.data.mongodb.uri=mongodb://localhost:27017/cbms
   ```

2. **Executar aplicação:**
   ```bash
   ./mvnw spring-boot:run
   ```
   Acessa em: `http://localhost:8080`

### Testes
```bash
./mvnw test
```
- **Total de testes:** 25+ (BeneficiarioServiceTest, ViewControllerTest)
- Todos os testes devem passar ✓

---

## 🏗 Modelo de Dados Principal

### Beneficiario (Documento MongoDB)

```java
@Document(collection = "beneficiarios")
public class Beneficiario {
    String id;                          // ID automático MongoDB
    String nomeCompleto;                // Obrigatório
    String cpf;                         // Única, padrão XXX.XXX.XXX-XX
    String celular;
    String dataNascimento;              // Formato: DD/MM/YYYY
    String codigoBarras;                // Gerado automaticamente (EST + timestamp)
    String foto;                        // Base64 da foto via webcam
    
    // Status
    Boolean ativo;                      // Padrão: true
    String motivoInativacao;            // Preenchido quando inativado
    
    // Sub-objetos aninhados
    Renda renda;
    Moradia moradia;
    EducacaoBens educacaoBens;
    String obs;                         // Observações gerais
    
    // Históricos (NUNCA são sobrescritos em edições)
    List<Coleta> coletas;               // Histórico de cestas retiradas
    List<Doacoes> doacoes;              // Histórico de doações recebidas
    List<IdentificacaoFamiliar> identificacaoFamiliar;
}
```

### Coleta
```java
public class Coleta {
    LocalDateTime dataColeta;           // Timestamp da retirada
}
```

### Doacoes
```java
public class Doacoes {
    LocalDateTime dataDoacao;           // Data/hora da doação
    String descricaoDoacao;             // O que foi doado
}
```

---

## 🔧 Padrões de Código e Convenções

### Estrutura em Camadas
1. **Model:** Entidades MongoDB com validações (`@NotBlank`, `@Pattern`)
2. **Repository:** Interface `BeneficiarioRepository extends MongoRepository`
3. **Service:** Lógica de negócio (SEMPRE aqui, não no controller)
4. **Controller:** Roteamento HTTP e renderização de views
5. **View:** Templates Thymeleaf

### Métodos de Serviço
- **`criarBeneficiario()`** - Novo beneficiário com validações e geração de código de barras
- **`atualizarBeneficiario()`** - Edição com preservação garantida de coletas/doações
- **`salvar()`** - Fachada que roteia para create ou update
- **`buscarPorId(id)`** - Busca com inicialização de objetos aninhados e ordenação
- **`listarTodos()`** - Lista todos com ordenação decrescente de coletas
- **`buscar(termo)`** - Busca por nome, CPF ou código de barras
- **`registrarColeta(cpf)`** - Registra retirada de cesta básica
- **`registrarDoacao(beneficiario, data, descricao)`** - Registra doação recebida
- **`deletarColeta(id, indice)`** - Deleta coleta específica (com ordenação antes de deletar)
- **`deletarDoacao(id, indice)`** - Deleta doação específica (com ordenação antes de deletar)

### Método Auxiliar Importante
- **`inicializarObjetosAninhados(beneficiario)`** - Garante que nenhum objeto/lista seja nulo
  - Previne `NullPointerException` na renderização
  - Inicializa `Renda`, `Moradia`, `EducacaoBens`, `Coletas`, `Doacoes`, etc
  - Usado em `buscarPorId()` e `novoBeneficiario()`

- **`ordenarColetasEDoacoes(beneficiario)`** - Ordena coletas e doações por data decrescente
  - Usado em: `listarTodos()`, `buscar()`, `buscarPorId()`, `deletarColeta()`, `deletarDoacao()`

### Endpoints HTTP

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/` | Lista beneficiários / busca |
| GET | `/novo` | Formulário novo beneficiário |
| GET | `/editar/{id}` | Formulário edição |
| GET | `/perfil/{id}` | Perfil detalhado + históricos |
| POST | `/salvar` | Salva novo ou edição |
| POST | `/coleta/{cpf}` | Registra coleta |
| POST | `/beneficiario/{id}/doacao` | Registra doação |
| POST | `/beneficiario/{id}/coleta/{indice}/deletar` | Deleta coleta |
| POST | `/beneficiario/{id}/doacao/{indice}/deletar` | Deleta doação |

---

## 💡 Fluxo de Desenvolvimento Recomendado

### Ao adicionar funcionalidade nova

1. **Defina o modelo** em `model/` (adicione campos/sub-entidades)
2. **Adicione métodos no serviço** em `service/BeneficiarioService.java`
3. **Crie endpoints** em `controller/ViewController.java`
4. **Adicione templates/HTML** em `templates/`
5. **Escreva testes** em `test/service/` e `test/controller/`
6. **Teste manualmente** via `./mvnw spring-boot:run`

### Importante
- **NUNCA** logica de negócio no controller
- **SEMPRE** ordene listas de coletas/doações após buscar do DB (usa `ordenarColetasEDoacoes()`)
- **SEMPRE** use `inicializarObjetosAninhados()` ao retornar beneficiário para renderizar
- **SEMPRE** preserve dados críticos (coletas, doações) ao fazer update
- **SEMPRE** adicione testes para novos métodos

---

## 🔍 Instruções Específicas para Claude

### Quando você for ajudar neste projeto:

1. **Leia primeiro o contexto relevante:**
   - Se mexendo com beneficiários: leia a entidade `Beneficiario.java`
   - Se mexendo com coletas/doações: leia `Coleta.java` e `Doacoes.java`
   - Se mexendo com validação: veja o `BeneficiarioService.java`

2. **Ao adicionar funcionalidade:**
   - Siga o padrão Model → Repository → Service → Controller → View
   - Use o método `ordenarColetasEDoacoes()` quando listar/buscar beneficiários
   - Sempre inicialize objetos com `inicializarObjetosAninhados()`
   - Nunca sobrescreva listas de coletas/doações em updates

3. **Testes:**
   - Use `BeneficiarioServiceTest` como referência (Mockito + JUnit 5)
   - Teste sempre os casos de sucesso E os erros (RuntimeException)
   - Para testes de controller, use `MockMvc` conforme `ViewControllerTest.java`

4. **Banco de dados:**
   - MongoDB NoSQL - documentos aninhados
   - Uma coleção: `beneficiarios`
   - Busca por: CPF (unique), ID, código de barras, nome

5. **Frontend:**
   - Thymeleaf para templates
   - Bootstrap 5 para estilo
   - Validate em múltiplas camadas (HTML5 + Backend)

6. **Ao relatar bug/issue:**
   - Mostre o stack trace completo
   - Indique qual teste falha (se houver)
   - Mencione os dados de entrada (ex: CPF, datas)

---

## 📝 Recente Implementação

### Coletas
- ✅ Registrar coleta de cesta básica (só para beneficiários ativos)
- ✅ Histórico de coletas na página de perfil
- ✅ Deletar coleta específica com confirmação
- ✅ Ordenação automática (mais recente primeiro)

### Doações
- ✅ Registrar doação com data e descrição
- ✅ Histórico de doações na página de perfil
- ✅ Desabilitar registro de doações para beneficiários inativos
- ✅ Deletar doação específica com confirmação
- ✅ Ordenação automática (mais recente primeiro)

---

## 🐛 Bugs Conhecidos / Em Correção

Nenhum atualmente - relatar qualquer problema encontrado!

---

## 📚 Referências Rápidas

- **Java Date/Time:** `LocalDateTime` para timestamps (coletas/doações)
- **Formato CPF:** `XXX.XXX.XXX-XX` (regex: `\d{3}\.\d{3}\.\d{3}-\d{2}`)
- **Ordenação:** `list.sort((a, b) -> b.getData().compareTo(a.getData()))` (decrescente)
- **MongoDB Find:** `beneficiarioRepository.findByCpf(cpf)` retorna `Optional<Beneficiario>`
- **Thymeleaf Loops:** `th:each="item, stat : ${lista}"` - use `stat.index` para índice

---

**Desenvolvedor principal:** Danilo Manchon  

