# Plano de Migração: PostgreSQL para MongoDB

Este documento detalha as mudanças necessárias para migrar o projeto de Spring Data JPA (PostgreSQL) para Spring Data MongoDB, utilizando a estratégia de **Embedding (Embutimento)** para a entidade `Coleta`.

---

## 1. Dependências (pom.xml)
Remover as dependências do JPA e do driver PostgreSQL:
- `spring-boot-starter-data-jpa`
- `postgresql`

Adicionar a dependência do MongoDB:
- `spring-boot-starter-data-mongodb`

---

## 2. Refatoração dos Models

### Coleta.java (Refatorada)
Deixaremos de usar `@Entity`. A classe torna-se um POJO (Plain Old Java Object) que será persistido dentro do documento `Beneficiario`.

```java
package com.estrela.cbms.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coleta {
    // ID removido, pois a coleta não será um documento separado
    private LocalDateTime dataColeta;
    
    // O beneficiário não é mais referenciado aqui pois ele é o "dono" do documento
}
```

### Beneficiario.java (Refatorada)
Substituiremos as anotações JPA por anotações do MongoDB.

```java
package com.estrela.cbms.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "beneficiarios") // Define a coleção no MongoDB
public class Beneficiario {

    @Id // Usa o ID do Spring Data MongoDB
    private String id;

    // ... campos existentes (nomeCompleto, cpf, etc)

    // Lista de coletas embutidas
    private List<Coleta> coletas = new ArrayList<>();

    // ... restante dos métodos
}
```

---

## 3. Repositórios

O repositório deixa de estender `JpaRepository` e passa a estender `MongoRepository`.

```java
// BeneficiarioRepository.java
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BeneficiarioRepository extends MongoRepository<Beneficiario, String> {
    // Métodos de busca customizados (se houver)
}
```

*Nota: O `ColetaRepository` pode ser removido, pois as operações de coletas agora são feitas carregando o `Beneficiario` e manipulando sua lista interna.*

---

## 4. Configuração (application.properties)

Substitua as configurações de DataSource pelas de conexão com MongoDB:

```properties
spring.data.mongodb.uri=mongodb://usuario:senha@localhost:27017/nome_do_banco
```

---

## 5. Próximos Passos (Migração de Dados)

Como o banco de dados é relacional agora, você precisará de um script de migração para mover os dados:

1. **Leitura**: Buscar todos os beneficiários e suas coletas correspondentes do Postgres.
2. **Transformação**: Instanciar os novos objetos `Beneficiario` (modelo MongoDB) e popular a lista de `Coleta` com os dados obtidos.
3. **Gravação**: Salvar os novos documentos na coleção `beneficiarios` do MongoDB.
