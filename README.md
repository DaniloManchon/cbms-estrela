# CBMS - Cadastro Beneficiário de Movimento Social

O CBMS é um sistema desenvolvido em Java com Spring Boot para gestão de beneficiários e controle de coletas/entregas de cestas básicas. O sistema foca na coleta detalhada de dados socioeconômicos para fornecer um perfil completo do responsável e seu núcleo familiar.

## 🛠 Tecnologias Utilizadas

- **Backend:** Java 21, Spring Boot 3.x
- **Persistência:** Spring Data JPA, Hibernate
- **Banco de Dados:** H2 (Banco em memória para desenvolvimento/testes)
- **Frontend:** Thymeleaf, Bootstrap 5, Bootstrap Icons
- **Integrações:** API ViaCEP (Consumo via Fetch API no Frontend)
- **Tooling:** Lombok (para redução de boilerplate)

## 🏗 Arquitetura e Modelagem

O sistema utiliza o padrão MVC (Model-View-Controller) e organiza os dados do Responsável através de objetos embutidos (`@Embedded`), garantindo que todas as informações socioeconômicas residam na mesma tabela principal, facilitando consultas e relatórios.

### Estrutura do Modelo `Responsavel`

A entidade principal `Responsavel` agrega as seguintes sub-estruturas:

1.  **Dados Pessoais:** Nome, CPF, Celular, Data de Nascimento (formato String para flexibilidade).
2.  **Identificação Familiar (`@ElementCollection`):** Lista dinâmica de membros residentes com parentesco e ocupação.
3.  **Renda:**
    *   `rendaBruta`: Valor total familiar.
    *   `fontesRenda`: Objeto que armazena o `tipo` (Formal, Informal, Pensão, BPC, Outros) e `outros` (detalhamento).
4.  **Moradia:**
    *   `endereco`: Dados de localização (CEP, Rua, Bairro, etc).
    *   `caracteristicas`: Tipo de moradia, nº de cômodos e material.
    *   `servicos`: Acesso a água, esgoto, lixo e eletricidade.
5.  **Educação e Bens:** Escolaridade do responsável, quantidade de estudantes e lista de bens duráveis (Geladeira, Carro, PC, etc).
6.  **Histórico de Coletas (`@OneToMany`):** Registro de todas as cestas retiradas pelo beneficiário.

## 🔌 Integrações e Funcionalidades Técnicas

### 1. Consulta Automática de CEP (ViaCEP)
Implementada no frontend através de JavaScript (`fetch`). Ao preencher o campo CEP e perder o foco (`blur`), o sistema:
- Valida o formato (8 dígitos).
- Realiza uma requisição assíncrona para `https://viacep.com.br/ws/{cep}/json/`.
- Popula automaticamente os campos de Logradouro, Bairro, Localidade e UF.
- Fornece feedback visual via Spinner e tratamento de erros (CEP não encontrado).

### 2. Mapeamento de Atributos (JPA Overrides)
Para evitar colisões de nomes em colunas do banco de dados (ex: o campo `tipo` presente tanto em Moradia quanto em Renda), o sistema utiliza `@AttributeOverrides` na entidade `Responsavel`:
```java
@Embedded
@AttributeOverrides({
    @AttributeOverride(name = "tipo", column = @Column(name = "moradia_tipo"))
})
private Moradia moradia;
```

### 3. Frontend Dinâmico
- **Layout em Abas:** Organizado com Bootstrap Tabs para melhorar a UX em formulários extensos.
- **Gestão de Família:** JavaScript manipulando o DOM para adicionar/remover linhas de membros da família, garantindo que o Spring consiga realizar o *binding* automático para a lista `List<IdentificacaoFamiliar>`.
- **Condicionais:** Exibição dinâmica de campos (ex: campo "Especifique" só aparece se a fonte de renda for "Outros").

## 🚀 Como Executar

1. Certifique-se de ter o Java 21 instalado.
2. Execute o comando: `./mvnw spring-boot:run`
3. Acesse no navegador: `http://localhost:8080`
4. Console do Banco de Dados H2: `http://localhost:8080/h2-console`
   - **JDBC URL:** `jdbc:h2:mem:testdb`
   - **User:** `sa`
   - **Password:** `password`

---
*Este projeto foi expandido para garantir a persistência completa de todos os dados socioeconômicos necessários para análise social.*
