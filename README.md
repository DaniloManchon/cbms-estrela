# CBMS - Cadastro Beneficiário de Movimento Social

O CBMS é um sistema desenvolvido em Java com Spring Boot para gestão de beneficiários de uma ONG e controle de coletas/entregas de cestas básicas. O sistema foca na coleta detalhada de dados socioeconômicos para fornecer um perfil completo do beneficiário e seu núcleo familiar, com suporte a status de atividade e histórico completo de distribuições.

## 🛠 Tecnologias Utilizadas

- **Backend:** Java 21, Spring Boot 4.x
- **Persistência:** Spring Data MongoDB
- **Banco de Dados:** MongoDB (NoSQL)
- **Frontend:** Thymeleaf, Bootstrap 5, Bootstrap Icons
- **Validação:** Jakarta Validation
- **Integrações:** API ViaCEP (Consumo via Fetch API no Frontend)
- **Tooling:** Lombok, Log4j2
- **Testes:** JUnit 5, Mockito

## 🏗 Arquitetura e Modelagem

O sistema utiliza o padrão MVC (Model-View-Controller) com MongoDB como persistência NoSQL. Os dados do Beneficiário são organizados em uma estrutura hierárquica de documentos, garantindo que todas as informações socioeconômicas residam em um único documento, facilitando consultas e relatórios.

### Estrutura do Modelo `Beneficiario`

A entidade principal `Beneficiario` (coleção MongoDB) agrega as seguintes sub-estruturas:

1.  **Dados Pessoais:** Nome, CPF (único), Celular, Data de Nascimento (formato String para flexibilidade).
2.  **Status:** Campo `ativo` (boolean) para controlar se o beneficiário está ativo ou inativo na ONG.
3.  **Código de Barras:** Identificador único gerado automaticamente (EST + timestamp) para rastreamento.
4.  **Identificação Familiar:** Lista dinâmica de membros residentes com parentesco, data de nascimento e ocupação/escola.
5.  **Renda:**
    *   `rendaBruta`: Valor total familiar.
    *   `fontesRenda`: Objeto que armazena o `tipo` (Formal, Informal, Pensão, BPC/LOAS, Outros) e detalhamento.
6.  **Moradia:**
    *   `endereco`: Dados de localização (CEP, Rua, Bairro, Número, Complemento, Localidade, UF).
    *   `tipo`: Propriedade (Própria, Alugada, Cedida, Ocupação).
    *   `numeroComodos`: Quantidade de cômodos.
    *   `material`: Material da construção.
    *   `servicos`: Acesso a água, esgoto, lixo e eletricidade.
7.  **Educação e Bens:**
    *   `escolaridadeBeneficiario`: Nível educacional do beneficiário.
    *   `qtdEstudantes`: Quantidade de estudantes na casa.
    *   `bens`: Lista de bens duráveis (Geladeira, Máquina de Lavar, Computador, Carro, Motocicleta).
8.  **Observações:** Campo livre para anotações adicionais.
9.  **Histórico de Coletas:** Lista de registros de cestas básicas retiradas, com timestamp de cada coleta. **Nunca é sobrescrito durante edições.**

## 🔌 Integrações e Funcionalidades Técnicas

### 1. Consulta Automática de CEP (ViaCEP)
Implementada no frontend através de JavaScript (`fetch`). Ao preencher o campo CEP e perder o foco (`blur`), o sistema:
- Valida o formato (8 dígitos).
- Realiza uma requisição assíncrona para `https://viacep.com.br/ws/{cep}/json/`.
- Popula automaticamente os campos de Logradouro, Bairro, Localidade e UF.
- Fornece feedback visual via Spinner e tratamento de erros (CEP não encontrado).

### 2. Geração Automática de Código de Barras
- Ao criar um novo beneficiário, um código único é gerado no formato: `EST` + timestamp.
- O código é exibível e imprimível na página de perfil do beneficiário.
- Utiliza a biblioteca JsBarcode para renderização visual em CODE128.

### 3. Separação de Operações (Create vs Update)
O serviço implementa dois métodos específicos:
- **`criarBeneficiario()`**: Para criar novos beneficiários com validação de CPF único e geração de código de barras.
- **`atualizarBeneficiario()`**: Para editar beneficiários existentes com:
  - Validação de CPF (permite alterar para outro CPF se não existir).
  - **Preservação garantida do histórico de coletas** (nunca são sobrescritos).
  - Mantém o ID e o código de barras originais.
- **`salvar()`**: Método fachada que roteia para create ou update baseado na presença do ID.

### 4. Ordenação Automática de Coletas
- No método `listarTodos()` e `buscar()`: As coletas de cada beneficiário são automaticamente ordenadas por data em ordem **decrescente** (mais recentes primeiro).
- A página inicial exibe a coleta mais recente no campo "Última Coleta".
- Implementado via lambda: `sort((c1, c2) -> c2.getDataColeta().compareTo(c1.getDataColeta()))`.

### 5. Frontend Dinâmico
- **Layout em Abas:** Organizado com Bootstrap Tabs para melhorar a UX em formulários extensos (Dados Pessoais, Moradia, Renda, Educação e Bens, Família).
- **Gestão de Família:** JavaScript manipulando o DOM para adicionar/remover linhas de membros da família.
- **Condicionais:** Exibição dinâmica de campos (ex: campo "Especifique" só aparece se a fonte de renda for "Outros").
- **Status Visual:** Badges de status (Ativo/Inativo) exibidas na lista e no perfil do beneficiário.
- **Indicadores de Abas:** Mostra se os campos estão preenchidos (verde), parcialmente (amarelo) ou com obrigatórios vazios (vermelho).

### 6. Webcam para Foto de Perfil
- Captura de foto em tempo real via webcam no navegador.
- Armazenamento em Base64 no banco de dados.
- Pré-visualização antes de salvar.

## 🚀 Como Executar

### Pré-requisitos
- **Java 21** instalado
- **MongoDB** rodando localmente ou em um servidor remoto
- **Maven** (ou use o wrapper: `./mvnw`)

### Configuração do Banco de Dados
Configure a conexão MongoDB no arquivo `application.properties`:
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/cbms
# ou, se houver autenticação:
# spring.data.mongodb.uri=mongodb://usuario:senha@localhost:27017/cbms?authSource=admin
```

### Executando o Projeto
1. Na raiz do projeto, execute: `./mvnw spring-boot:run`
2. O navegador abrirá automaticamente em `http://localhost:8080`
3. Se não abrir, acesse manualmente: `http://localhost:8080`

### Testes
Execute os testes com: `./mvnw test`
- Total de testes: **17** (todos passando ✓)
- Cobertura: `BeneficiarioServiceTest`, `ViewControllerTest`, `CbmsApplicationTests`

## 📋 Funcionalidades Principais

### Para o Usuário Final
- ✅ Listar todos os beneficiários com status visual
- ✅ Buscar beneficiários por nome, CPF ou código de barras
- ✅ Cadastrar novo beneficiário com foto via webcam
- ✅ Editar perfil completo sem perder histórico de coletas
- ✅ Registrar coleta de cesta básica
- ✅ Visualizar perfil detalhado e histórico de retiradas
- ✅ Marcar beneficiário como ativo ou inativo
- ✅ Imprimir código de barras
- ✅ Consulta automática de endereço por CEP

### Para o Desenvolvedor
- ✅ Arquitetura em camadas (Model → Repository → Service → Controller → View)
- ✅ Serviço com operações de create/update bem definidas
- ✅ Preservação garantida de dados críticos (coletas)
- ✅ Validação em múltiplas camadas (Entity, Service)
- ✅ Testes unitários com Mockito
- ✅ Código com comentários explicativos (especialmente lambdas)
- ✅ Uso de Lombok para reduzir boilerplate

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/estrela/cbms/
│   │   ├── model/              # Entidades (Beneficiario, Coleta, etc)
│   │   ├── repository/         # Interfaces Spring Data MongoDB
│   │   ├── service/            # Lógica de negócio (criarBeneficiario, atualizarBeneficiario, etc)
│   │   ├── controller/         # Controllers MVC (ViewController)
│   │   └── CbmsApplication.java # Classe principal
│   └── resources/
│       ├── templates/          # Arquivos Thymeleaf (HTML)
│       ├── static/             # CSS, JS, imagens
│       └── application.properties
└── test/
    └── java/com/estrela/cbms/
        ├── service/BeneficiarioServiceTest.java
        ├── controller/ViewControllerTest.java
        └── CbmsApplicationTests.java
```

## 🔐 Segurança e Validações

- **CPF Único:** Validação de unicidade com regex `\d{3}\.\d{3}\.\d{3}-\d{2}`
- **CPF na Edição:** Permite alterar para outro CPF se não existir
- **Histórico Imutável:** Coletas nunca são perdidas em edições
- **Validação em Múltiplas Camadas:** Frontend (HTML5) e Backend (Jakarta Validation)

## 📝 Notas Técnicas

- As datas de nascimento são armazenadas em formato String (DD/MM/AAAA) para maior flexibilidade
- Coletas são ordenadas automaticamente em ordem decrescente (mais recentes primeiro)
- Status padrão de novo beneficiário é "Ativo"
- Códigos de barras são gerados apenas na criação (não são regenerados em edições)
