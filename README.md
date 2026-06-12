
# Desafio de Desenvolvimento

O objetivo deste desafio é obter uma ideia das habilidades que o candidato possui, da organização de tempo e também do código.

## Considerações Importantes – Por favor, leia com atenção:

- O desafio já tem códigos pré prontos para você completar as funcionalidades. Não é preciso reinventar a roda! Use o que existe!

- Use seu tempo de forma inteligente: Uma solução simples primeiro e depois avance.

- Comentários sempre são bem-vindos em métodos ou estruturas mais complexas.

- Parece não intuitivo, mas deixe as telas por último, pense na estrutura dos dados e nos métodos de gravação e exportação primeiro.

- Utilize os testes unitários já existentes e crie novos também, isso é importante. Não existe necessidade de 100% de cobertura, mas use-os para experimentar e validar sua solução – **é muito importante que os testes já existentes estejam passando após a sua implementação!**

- Faça commits frequentes, assim podemos ver a evolução da sua solução.

- Sobre banco de dados, você pode usar qualquer um que esteja acostumado, inclusive em memória, se preferir. Aqui utilizamos, comumente: PostgreSQL, Microsoft SQL Server, Oracle DB, MySQL e, especialmente para testes, HSQLDB. 

- Entregue tudo o que conseguir fazer, indiferente de estar completo ou não.

- Durante o período de teste, fique à vontade para enviar dúvidas ao recrutador.

- Ao final, deixamos alguns links que podem ser úteis para consulta, mas você pode consultar qualquer material, à vontade.

- Nos envie, ao final, uma descrição com detalhes de como podemos testar a sua implementação.

## O que você deve implementar:

Imagine que você quer fazer um sistema de escalação de times. Toda semana você vai montar um time vencedor. 

Não importa se é Esporte tradicional ou eSports.

Exemplos de Esporte tradicional : Futebol, Basquete.

Exemplos de eSports : Counter Strike, Valorant, Free Fire, League of Legends, APEX.

Sua tarefa é construir a melhor solução no tempo combinado, considerando os requisitos que estarão descritos abaixo.

Você pode usar a criatividade pois não existe uma solução definitiva para o desafio.

Abaixo, mais detalhes:

## Estrutura dos Dados

### Tabela de "Integrante" :

- Id
- Nome
- Função

### Tabela de Time:

- Id
- Nome do Clube
- Data

### Tabela de ComposicaoTime:

- Id
- Id_Time  (foreign key tabela Time)
- Id_Integrante  (foreign key tabela Integrante)

## Funcionalidades Principais

### 1) Tratamento de dados – PASSO MAIS IMPORTANTE DO DESAFIO, foque nessa etapa primeiro.

Esse passo é o mais importante no teste porque gostaríamos de medir a sua capacidade de lidar com estruturas de dados. 

Já existe um service criado no projeto (ApiService), com métodos para serem implementados, e testes unitários para eles. Utilize-os!

Sendo possível, crie novos testes unitários, aumente os cases dos testes atuais, amplie essa cobertura de testes, pois é muito importante garantir que o código esteja atendendo corretamente o que se pede.

No quadro, alguns detalhes sobre os métodos:

| Método  | Parâmetros | Descrição |
|--|--|--|
| TimeDaData | Data, Lista de todos os Times                              | Vai retornar um Time, com a composição do time daquela data                                 |
| IntegranteMaisUsado | Data inicial e Data final (podem ser null), Lista de todos os Times | Vai retornar o integrante que tiver presente na maior quantidade de times dentro do período |
| IntegrantesDoTimeMaisRecorrente | Data inicial e Data final (podem ser null), Lista de todos os Times | Vai retornar uma lista com os nomes dos integrantes do time mais recorrente dentro do período    |
| FuncaoMaisRecorrente | Data inicial e Data final (podem ser null), Lista de todos os Times | Vai retornar a função mais recorrente nos times dentro do período                                |
| ClubeMaisRecorrente | Data inicial e Data final (podem ser null), Lista de todos os Times |Vai retornar o nome do Clube mais comum dentro do período                      |
| ContagemDeClubesNoPeriodo | Data inicial e Data final (podem ser null), Lista de todos os Times | Vai retornar o número (quantidade) de aparições de cada Clube participante no período                           |
| ContagemPorFuncao | Data inicial e Data final (podem ser null), Lista de todos os Times | Vai retornar o número (quantidade) de Funções dentro do período                             |

## Funcionalidades Extras
### 2) API de Cadastro

Lembrando: a prioridade é a funcionalidade correta, não as telas. 

#### Cadastro de Integrantes

Fazer um cadastro de integrantes para os times.

#### Cadastro de Times

Fazer um cadastro de times onde não importa muito a quantidade de integrantes. 

Para cadastrar um time para uma determinada semana basta escolher os personagens/integrantes que farão parte dele.


### 3) API para processamento de Dados

Seu sistema vai processar as informações do banco de dados e vai exportá-las através de endpoints.

Você deve usar os selects para trazer todos os dados, mas processe eles na linguagem, através dos métodos implementados no passo 1.

| Endpoint  | Parâmetros |
|--|--|
| TimeDaData | Data | 
| IntegrantesDoTimeMaisRecorrente | Data inicial e Data final (podem ser null) |
| IntegranteMaisUsado | Data inicial e Data final (podem ser null) |
| FuncaoMaisRecorrente | Data inicial e Data final (podem ser null) |
| ClubeMaisRecorrente | Data inicial e Data final (podem ser null) |
| ContagemDeClubesNoPeriodo | Data inicial e Data final (podem ser null) |
| ContagemPorFuncao | Data inicial e Data final (podem ser null) |

Exemplos de Resultados esperados:

TimeDaData
``` 
{
  "data": 2021-01-15,
  "clube": "Falcons",
  "integrantes": [ "Bangalore", "BloodHound", "Crypto" ]
}
```

FuncaoMaisRecorrente
``` 
{
  "Função" : "Meia"
}
```

ContagemDeClubesNoPeriodo
``` 
{
  "Falcons": 5,
  "FURIA": 2,
  "DarkZero Esports": 3
}
```


### 4) Telas

Conforme já foi dito as telas de cadastro tem prioridade menor do que o funcionamento da API.

Você pode fazer as telas da maneira mais simples possível e usar qualquer framework que facilite o desenvolvimento.

- Tela de Inserção de Integrantes
    - Um formulário com os campos é suficiente
- Tela de Montagem de Times pode ser feita de diversas maneiras, algumas sugestões:
    - Fazer uma listagem e colocar um checkbox ao lado de cada integrante
    - Fazer um "transfer" usando dois "selects" de html
    - Usar um componente de jquery ( https://www.jqueryscript.net/blog/best-multiple-select.html )

Não se sinta obrigado a utilizar algo dessas sugestões, fique à vontade para utilizar o que tiver mais domínio ou preferência.

O importante é a tela estar funcional e a beleza não será avaliada.

## Alguns links úteis para consulta

- https://www.baeldung.com/java-collections
- https://www.baeldung.com/java-8-streams-introduction
- https://pt.linkedin.com/pulse/tdd-com-java-junit-e-mockito-tiago-perroni
- https://www.devmedia.com.br/rest-tutorial/28912
- https://www.baeldung.com/rest-with-spring-series
- https://www.baeldung.com/jackson-vs-gson

## Como executar a implementação

### Docker com SQL Server

#### Docker Compose v2

Pré-requisitos: Docker Engine e Docker Compose v2.

```bash
cp .env.example .env
# Preencha as senhas e usuários no .env antes de continuar
docker compose up --build
```

A aplicação estará disponível em `http://localhost:8080`. O Compose cria o banco
`duxus`, inicia a aplicação somente depois que o SQL Server estiver pronto e
mantém os dados no volume `sqlserver-data`.

Para encerrar:

```bash
docker compose down
```

Para também remover os dados persistidos:

```bash
docker compose down --volumes
```

#### Docker (sem Compose)

Pré-requisitos: Docker Engine.

```bash
# Cria a rede compartilhada
docker network create desafio-dx

# Inicia o SQL Server
docker run -d \
  --name sqlserver \
  --network desafio-dx \
  -e ACCEPT_EULA=Y \
  -e MSSQL_PID=Express \
  -e MSSQL_SA_PASSWORD="$MSSQL_SA_PASSWORD" \
  -v sqlserver-data:/var/opt/mssql \
  mcr.microsoft.com/mssql/server:2022-latest

# Aguarda o SQL Server ficar saudável
echo "Aguardando SQL Server..."
until docker exec sqlserver /opt/mssql-tools18/bin/sqlcmd \
  -C -S localhost -U sa -P "$MSSQL_SA_PASSWORD" -Q "SELECT 1" -b -o /dev/null 2>/dev/null; do
  sleep 2
done

# Cria o banco duxus
docker exec sqlserver /opt/mssql-tools18/bin/sqlcmd \
  -C -S localhost -U sa -P "$MSSQL_SA_PASSWORD" \
  -Q "IF DB_ID(N'duxus') IS NULL CREATE DATABASE [duxus]"

# Constrói a imagem da aplicação
docker build -t desafio-dx-app .

# Inicia a aplicação
docker run -d \
  --name app \
  --network desafio-dx \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:sqlserver://sqlserver:1433;databaseName=duxus;encrypt=true;trustServerCertificate=true" \
  -e SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.microsoft.sqlserver.jdbc.SQLServerDriver \
  -e SPRING_DATASOURCE_USERNAME="$SPRING_DATASOURCE_USERNAME" \
  -e SPRING_DATASOURCE_PASSWORD="$SPRING_DATASOURCE_PASSWORD" \
  -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \
  desafio-dx-app

# Para encerrar
docker stop app sqlserver
docker rm app sqlserver
docker network rm desafio-dx
docker volume rm sqlserver-data
```

### Execução local com SQL Server

Pré-requisitos: Java 8 ou superior, Docker Engine e Docker Compose v2.

```bash
docker compose up -d sqlserver sqlserver-init
./mvnw spring-boot:run
```

Por padrão, a aplicação acessa o banco `duxus` em
`jdbc:sqlserver://localhost:1433`. As credenciais devem ser definidas somente
no arquivo local `.env` ou nas variáveis de ambiente
`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` e
`SPRING_DATASOURCE_PASSWORD`.
O `.env` é carregado automaticamente na execução local e não é versionado.

Antes de inicializar o JPA, a aplicação conecta ao catálogo `master` e cria o
banco indicado por `databaseName` caso ele ainda não exista. O usuário
configurado precisa ter permissão `CREATE ANY DATABASE`.

O H2 é carregado somente durante os testes automatizados, pelo perfil `test`.

### Documentação da API

Com a aplicação em execução:

- Swagger UI: `http://localhost:8080/swagger`
- Especificação OpenAPI: `http://localhost:8080/api-docs`

Para executar todas as verificações:

```bash
./mvnw clean verify
```

## CI/CD

A workflow [`.github/workflows/ci-cd.yml`](.github/workflows/ci-cd.yml) é
executada em todo `push` e `pull_request`:

1. compila a aplicação e executa todos os testes com Java 8;
2. constrói os containers e sobe a aplicação conectada ao SQL Server;
3. executa um smoke test de escrita e leitura pela API;
4. na branch `main`, publica a imagem validada no GitHub Container Registry com
   as tags `latest` e `sha-<commit>`.

Para impedir a integração de commits com falha, configure a proteção da branch
`main` no GitHub exigindo os checks `Build and test` e
`Docker and SQL Server integration`.

## Endpoints

### Cadastro

| Método | Endpoint | Corpo |
|--|--|--|
| `POST` | `/api/integrantes` | `{"nome":"Michael Jordan","funcao":"ala"}` |
| `GET` | `/api/integrantes` | - |
| `POST` | `/api/times` | `{"nomeDoClube":"Chicago Bulls","data":"1995-01-01","integrantesIds":[1]}` |
| `GET` | `/api/times` | - |

### Processamento

Os parâmetros `dataInicial` e `dataFinal` usam o formato `AAAA-MM-DD`, são
opcionais e formam um período inclusivo.

| Método | Endpoint |
|--|--|
| `GET` | `/api/processamento/time-da-data?data=1995-01-01` |
| `GET` | `/api/processamento/integrante-mais-usado` |
| `GET` | `/api/processamento/integrantes-do-time-mais-recorrente` |
| `GET` | `/api/processamento/funcao-mais-recorrente` |
| `GET` | `/api/processamento/clube-mais-recorrente` |
| `GET` | `/api/processamento/contagem-de-clubes` |
| `GET` | `/api/processamento/contagem-por-funcao` |
