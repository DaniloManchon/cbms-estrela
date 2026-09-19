# Processo de Release e Versionamento

Este projeto utiliza um fluxo automatizado de CI/CD via GitHub Actions para gerenciar o versionamento semântico de forma inteligente e publicar releases a cada merge na branch `main`.

## Visão Geral

Sempre que um commit ou pull request é integrado à branch `main`, o pipeline de release é disparado. Ele executa as seguintes etapas de forma sequencial e segura:
1. **Versionamento Dinâmico:** Identifica a última tag válida do Git e calcula a nova versão baseando-se na mensagem do commit.
2. **Build do Projeto:** Compila e empacota a aplicação com o novo número de versão gerado.
3. **Sincronização no Git:** Atualiza o arquivo `pom.xml`, cria a nova tag e envia as alterações de volta ao repositório.
4. **Publicação da Release:** Cria uma Release no GitHub anexando o artefato `.jar` correspondente.
5. **Publicação no Docker Hub:** Builda e envia a nova imagem Docker atualizada para o registro.

## Regras de Incremento Automático (Semantic Versioning)

O pipeline analisa o assunto e o corpo do último commit integrado na `main` para decidir qual dígito da versão deve subir. O comportamento padrão é incrementar correções (*Patch*), a menos que palavras-chave específicas sejam detectadas:

| Tipo de Mudança | Gatilhos na Mensagem do Commit | Exemplo de Impacto |
| :--- | :--- | :--- |
| **Major** (X.0.0) | `#major`, `[major]` ou `BREAKING CHANGE` | De `v1.2.3` para `v2.0.0` |
| **Minor** (X.Y.0) | `#minor`, `[minor]` ou prefixos `feat:` e `feat(...)` | De `v1.2.3` para `v1.3.0` |
| **Patch** (X.Y.Z) | **Padrão Automático** (qualquer outra mensagem como `fix:`, `refactor:`, etc.) | De `v1.2.3` para `v1.2.4` |

*Nota: Se o repositório for totalmente novo e nenhuma tag for encontrada, o pipeline adota a versão base `v0.0.0` para calcular o primeiro incremento.*

## Fluxo de Trabalho (`.github/workflows/release.yml`)

### 1. Leitura e Cálculo da Versão
Diferente de abordagens tradicionais, o pipeline ignora a tag `<version>` antiga do arquivo local e consulta diretamente o Git com o comando `git describe --tags --abbrev=0`. O Bash isola os números e reescreve temporariamente o `pom.xml` via `mvn versions:set -DnewVersion=...`.

### 2. Sincronização e Prevenção de Loop (`[skip ci]`)
Para persistir a alteração e evitar que a própria esteira rode infinitamente após atualizar o código:
- O workflow commita o `pom.xml` atualizado utilizando o bot do GitHub.
- A mensagem de commit obrigatoriamente inclui a flag **`[skip ci]`** (ex: `chore: bump version to 1.3.0 [skip ci]`), instruindo o GitHub Actions a ignorar este push específico.
- A nova tag correspondente (ex: `v1.3.0`) é gerada e publicada.

### 3. Build e Artefatos
O projeto é compilado utilizando `mvn package -DskipTests`. O arquivo `.jar` gerado na pasta `target/` é anexado automaticamente à aba de **Releases** do repositório no GitHub. Além disso, a imagem Docker correspondente é gerada e publicada com a tag da versão e a tag `latest`.

## Pré-requisitos e Permissões

Para que o pipeline funcione corretamente, certifique-se de que os seguintes pontos foram configurados no repositório:

1. **Permissões de Escrita do Workflow:**
    - Vá em **Settings** > **Actions** > **General**.
    - Em *Workflow permissions*, selecione **"Read and write permissions"**.
    - Isso garante que a action consiga realizar o `git push` e abrir releases.

2. **Secrets do Repositório:**
    - Vá em **Settings** > **Secrets and variables** > **Actions**.
    - Certifique-se de preencher as Secrets para a publicação do Docker:
        - `DOCKERHUB_USERNAME`: Seu usuário do Docker Hub.
        - `DOCKERHUB_TOKEN`: Seu token de acesso (PAT) do Docker Hub.

## Como utilizar no dia a dia

- **Desenvolvimento:** Crie suas branchs de feature ou correção normalmente. Ao abrir Pull Requests para a `main`, garanta que o título do PR ou os commits sigam a semântica explicada na tabela de incrementos (ex: começar com `feat:` se for uma nova funcionalidade).
- **Publicação:** Ao aprovar e realizar o *Merge* na branch `main`, o fluxo inteligente calculará a versão correta, gerará a tag Git e distribuirá o JAR e a imagem Docker sem intervenção manual.
