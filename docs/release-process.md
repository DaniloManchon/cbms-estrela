# Processo de Release e Versionamento

Este projeto utiliza um fluxo automatizado de CI/CD via GitHub Actions para gerenciar versões e publicar releases a cada merge na branch `main`.

## Visão Geral

Sempre que um commit é enviado para a branch `main`, o pipeline de release é disparado automaticamente. Ele é responsável por:
1. Incrementar a versão no `pom.xml`.
2. Commit e push da nova versão no repositório.
3. Build do projeto.
4. Criação de uma Release no GitHub com o artefato `.jar` anexado.

## Fluxo de Trabalho (`.github/workflows/release.yml`)

### 1. Incremento de Versão
O pipeline utiliza o plugin `versions-maven-plugin` para ler a versão atual do `pom.xml` e incrementar automaticamente o componente "incremental" (ex: de `0.0.1` para `0.0.2`).

### 2. Sincronização (Git)
Para garantir que o histórico do repositório reflita a mudança de versão:
- O workflow commita o `pom.xml` alterado de volta para a `main`.
- Utilizamos a tag `[skip ci]` na mensagem do commit para impedir que a Action entre em loop infinito (disparando novamente após o próprio push).
- Uma nova tag (ex: `v0.0.2`) é criada e enviada para o servidor.

### 3. Build e Release
Após o versionamento, o projeto é compilado (`mvn package -DskipTests`) e o artefato resultante (`.jar`) é enviado para a seção de **Releases** do GitHub, vinculado à tag criada no passo anterior.

## Pré-requisitos e Permissões

Para que o pipeline funcione corretamente, é necessário configurar as permissões do GitHub Actions no repositório:

1. Vá para **Settings** do repositório.
2. Acesse **Actions** > **General**.
3. Em **Workflow permissions**, selecione **"Read and write permissions"**.

Sem essa permissão, o bot da GitHub Actions não terá autorização para realizar o push do `pom.xml` atualizado nem para criar a release.

## Como utilizar

- **Desenvolvimento:** Trabalhe livremente na branch `develop` (ou feature branches).
- **Publicação:** Quando estiver pronto para lançar uma nova versão, faça o *merge* para a branch `main`. O sistema cuidará do resto.
