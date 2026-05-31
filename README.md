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

    
## 🗺️ Diagrama de Classe

[![](https://mermaid.ink/img/pako:eNqNV1tP3DgU_iuWpUrQHRADWZhGFdIypRISlBWt-rCalzOJJ3jXsVPboVwEf2Yf9of0j-2xM544Fy7zMEnO_Xzn4uSBZipnNKWZAGM-cSg0lAv57h2ZQwk5kJyRuZJWKyGYXkgvRr5z9rOlkoeFJO63c8VMpaSBGya-Mn3DM0b0gLQW_o3LnN1ufbWay4Isa5PBhFxgMIKU7n-bNKwgLtWNiuxvvSDKcm5Bb50rNMzzF61WTK-4eJOoAXGDVqMY4uQm5AQTQnHk18I6Fl76NjQruLEa9FwJZiFkn1WrVvJxIXsF2ADXoD-C8lgFrlilDLdK38VxttSg0oQSSWc9wtsA2I7jeku-DSlICpQD_U3lymxtk3N8eorsPQUx3yc62LFMl-pV6T-VPstDiUfD5JJnHAS_B325_JtZZf6QXF6DC-a5dMcKFWM2qFWEcCjXx49cYg4ryNjxcQhmhX10cjevVh20LivLlQQxmmej8kWVbK7KCmFVbjoB05LFWSGVZnMw7FKj0ZYRzEtUm5DY1wiePtcmpUG_PJPNEJ5muLZOpeU55MwQRtZwO-53EEpvjwDXdve6huExSiDk3WNhOn0KE7UA3aPmYOELmIyXTLZGPA5nOZL4imeQgfoMJRcc9BNGMUJup1Bixtr9B9KF0pBzwNXir4F8mtfewgmTCEf00AtQLU0nqqYIT-tZNSMFeha1c5WB-ASWfcNsfeadSYw84nJx4YzslngSOr5H0WpDeb1iFSBslplMvalEIdisrpzHU9QT0ImoqUU_Al-bE123aX9Wzm0jvWrvO7ZCETfWTvEM0yxThK1vem4srzakM7RZ4Gkp65JpHNDS7bqefIll0biJNmS_33E-zPqmE06nefoZMg-F9pM2XHmbaH7Y_NTYOgeXcWB6g0vfhbG7kOzDYKaqHkUofI_IVa37gDTJ9_V9K4zVdQlcD8SFa2CfV49RrzrxbsDrxwtFDQO0CjXwLvhtn8SwZTUeFo33yFm3BkuFIwWSFEygINfQZ5Two-YSzuGm3RqB5-DAxszVkAMRGhtTGHjGs2aEo4jihn54oS_DCNX4OmfatX2FoWfuyPF1QUbvpW9n53jsTSQlteXuJF3IEW5PKTpEXtcbnDuRSkeJLOiCogpe3-PNeh2mBBVNzbui71FsfGelvg7o69d_v_5VQ60G2Oethl3xvERnfFsxJ-hMO5G4hikekhbENaYbbHsrYSrT9WDcY-cQVnalNrPg0pIG9-9Cdvw7oU4gdEIL3B40tbpmE4pjW4J7pL6XFtRe48QuaIq3Oeh_EHP5iDoVyL-UKoOaVnVxTdMVCINPdYVbnK2_MjYifnfOVS0tTafJ1Nug6QO9pelOsre_e7R_NE0O96Z4mSbJhN7R9HD3ECnJwf5-ksxmyYfkcULvvdvpbnK0N9ub7R8e_f7h4GB2MJtQ9z2g9MX6Q8ddHv8H05N7SQ?type=png)](https://mermaid.ai/live/edit#pako:eNqNV1tP3DgU_iuWpUrQHRADWZhGFdIypRISlBWt-rCalzOJJ3jXsVPboVwEf2Yf9of0j-2xM544Fy7zMEnO_Xzn4uSBZipnNKWZAGM-cSg0lAv57h2ZQwk5kJyRuZJWKyGYXkgvRr5z9rOlkoeFJO63c8VMpaSBGya-Mn3DM0b0gLQW_o3LnN1ufbWay4Isa5PBhFxgMIKU7n-bNKwgLtWNiuxvvSDKcm5Bb50rNMzzF61WTK-4eJOoAXGDVqMY4uQm5AQTQnHk18I6Fl76NjQruLEa9FwJZiFkn1WrVvJxIXsF2ADXoD-C8lgFrlilDLdK38VxttSg0oQSSWc9wtsA2I7jeku-DSlICpQD_U3lymxtk3N8eorsPQUx3yc62LFMl-pV6T-VPstDiUfD5JJnHAS_B325_JtZZf6QXF6DC-a5dMcKFWM2qFWEcCjXx49cYg4ryNjxcQhmhX10cjevVh20LivLlQQxmmej8kWVbK7KCmFVbjoB05LFWSGVZnMw7FKj0ZYRzEtUm5DY1wiePtcmpUG_PJPNEJ5muLZOpeU55MwQRtZwO-53EEpvjwDXdve6huExSiDk3WNhOn0KE7UA3aPmYOELmIyXTLZGPA5nOZL4imeQgfoMJRcc9BNGMUJup1Bixtr9B9KF0pBzwNXir4F8mtfewgmTCEf00AtQLU0nqqYIT-tZNSMFeha1c5WB-ASWfcNsfeadSYw84nJx4YzslngSOr5H0WpDeb1iFSBslplMvalEIdisrpzHU9QT0ImoqUU_Al-bE123aX9Wzm0jvWrvO7ZCETfWTvEM0yxThK1vem4srzakM7RZ4Gkp65JpHNDS7bqefIll0biJNmS_33E-zPqmE06nefoZMg-F9pM2XHmbaH7Y_NTYOgeXcWB6g0vfhbG7kOzDYKaqHkUofI_IVa37gDTJ9_V9K4zVdQlcD8SFa2CfV49RrzrxbsDrxwtFDQO0CjXwLvhtn8SwZTUeFo33yFm3BkuFIwWSFEygINfQZ5Two-YSzuGm3RqB5-DAxszVkAMRGhtTGHjGs2aEo4jihn54oS_DCNX4OmfatX2FoWfuyPF1QUbvpW9n53jsTSQlteXuJF3IEW5PKTpEXtcbnDuRSkeJLOiCogpe3-PNeh2mBBVNzbui71FsfGelvg7o69d_v_5VQ60G2Oethl3xvERnfFsxJ-hMO5G4hikekhbENaYbbHsrYSrT9WDcY-cQVnalNrPg0pIG9-9Cdvw7oU4gdEIL3B40tbpmE4pjW4J7pL6XFtRe48QuaIq3Oeh_EHP5iDoVyL-UKoOaVnVxTdMVCINPdYVbnK2_MjYifnfOVS0tTafJ1Nug6QO9pelOsre_e7R_NE0O96Z4mSbJhN7R9HD3ECnJwf5-ksxmyYfkcULvvdvpbnK0N9ub7R8e_f7h4GB2MJtQ9z2g9MX6Q8ddHv8H05N7SQ)
> Diagrama gerado usando Mermaid

---
*Este projeto foi expandido para garantir a persistência completa de todos os dados socioeconômicos necessários para análise social.*
