# Aula 1 — Do código ao primeiro container

**Sistema:** página HTML servida pelo Nginx  
**Duração sugerida:** 20 a 25 minutos  
**Objetivo:** compreender ambiente de execução, Docker, Dockerfile, imagem e container;

### Analogia da cozinha

Imagine que diferentes pessoas receberam apenas esta instrução:

> “Façam um bolo de chocolate.”

Cada uma pode usar ingredientes, utensílios, quantidades e temperaturas diferentes. O resultado provavelmente será diferente.

Para reproduzir o mesmo bolo, precisamos definir:

- ingredientes;
- quantidades;
- utensílios;
- temperatura;
- tempo de preparo;
- ordem das etapas.

O Docker busca essa mesma padronização para aplicações.

| Conceito | Analogia |
|---|---|
| Aplicação | O bolo que queremos entregar |
| Ambiente de execução | A cozinha, os utensílios e os equipamentos |
| Dockerfile | A receita detalhada |
| `docker build` | Seguir a receita e preparar uma massa pronta |
| Imagem | Uma forma com a massa pronta, congelada e etiquetada |
| `docker run` | Usar uma cópia dessa massa para colocar um bolo em produção |
| Container | Um bolo sendo produzido e servido |
| Docker Hub | Um grande catálogo de massas e bases prontas |

A imagem pode ser entendida como uma **massa pronta congelada e etiquetada**. 
Ela já foi preparada seguindo a receita, mas ainda não está sendo servida. C
omo está congelada, não muda enquanto é usada. O Docker pode tirar quantas cópias forem necessárias dessa mesma base.
abrir uma instância de cozinha não é abrir uma nova cozinha completa, mas sim uma estação dentro desse ambiente
Quando executamos `docker run`, o Docker não consome nem modifica a imagem original. Ele usa uma cópia dela para iniciar um container. 
Por isso, a mesma imagem pode criar um, dois ou cem containers.


## servidor web
Usaremos o **Nginx**, que é um servidor web. Nginx não é um provedor de nuvem: ele é o programa que receberá a requisição HTTP e entregará nosso `index.html`.

## Dockerfile

O Dockerfile é nossa receita.

### `FROM nginx:alpine`

```dockerfile
FROM nginx:alpine
```

`FROM` define a imagem base.

Em vez de instalar e configurar um servidor web do zero, começamos com uma imagem que já possui o Nginx. 
A variante Alpine utiliza uma distribuição Linux pequena, adequada para uma imagem simples como esta.

Na analogia:

> Ninguém precisa plantar trigo, fabricar farinha, construir um forno e produzir todos os utensílios antes de começar cada bolo. 
> Usamos ingredientes e equipamentos que já estão prontos.

Quando a imagem `nginx:alpine` ainda não existe localmente, o Docker a baixa de um registry, normalmente no Docker Hub.
Executar `docker build baixa automaticamente a imagem base quando necessário, 
portanto, o `pull` é útil para explicar o conceito, mas não é obrigatório antes do build.

### `COPY index.html /usr/share/nginx/html/index.html`

```dockerfile
COPY index.html /usr/share/nginx/html/index.html
```

`COPY` copia um arquivo do projeto para dentro da imagem.

- `index.html` é a origem, na pasta atual;
- `/usr/share/nginx/html/index.html` é o destino dentro da imagem.

Essa é a pasta na qual o Nginx procura sua página principal. Nossa cópia substitui a página padrão.

Na analogia, estamos acrescentando nosso recheio e nossa decoração à base preparada. 
Depois do build, essa combinação fará parte da massa congelada que chamamos de imagem.

Como é uma cópia, modificar o HTML no computador não altera imagens já construídas. 
Para incorporar uma alteração, será necessário construir uma nova imagem.

### `EXPOSE 80`

```dockerfile
EXPOSE 80
```

O Nginx escuta a porta `80` dentro do container.
`EXPOSE` documenta essa porta, mas não a publica no computador. 
A ligação será feita mais tarde com `-p` no `docker run`.

É como informar que existe uma porta de atendimento dentro da estação. 
Ainda precisamos criar o caminho que permitirá ao público chegar até ela.

## imagem

```bash
docker build -t app_aula:1.0 .
```

Tradução do comando:
> “Docker, leia o Dockerfile desta pasta, construa uma imagem e dê a ela o nome `app_aula` com a versão `1.0`.”

Cada parte possui uma função:

- `docker build`: inicia a construção;
- `-t`: permite atribuir nome e tag;
- `app_aula`: nome da imagem;
- `1.0`: tag usada como versão;
- `.`: usa a pasta atual como contexto de construção.

O contexto é o conjunto de arquivos que o Docker pode utilizar no build. Neste caso, ele precisa encontrar o Dockerfile e o `index.html`.

Na analogia:

```text
Dockerfile
Receita
    ↓
docker build
Preparo
    ↓
app_aula:1.0
Massa pronta, congelada e etiquetada
```

Agora liste as imagens:

```bash
docker image ls
```

Devem aparecer, entre outras:

- `app_aula:1.0`, construída por nós;
- `nginx:alpine`, usada como base.

> “Neste momento temos uma imagem, mas ainda não temos nossa aplicação executando. Temos um modelo pronto para criar containers.”

## Criando e executando o container

```bash
docker run -d -p 8080:80 --name app-aula-container app_aula:1.0
```

Tradução completa:

> “Docker, crie e execute em segundo plano um container chamado `app-aula-container`, baseado na imagem `app_aula:1.0`, 
> enviando o tráfego da porta 8080 do computador para a porta 80 do container.”

### `docker run`

Cria um container a partir de uma imagem e inicia sua execução.

Se executarmos `docker run` novamente, não estaremos simplesmente ligando o mesmo container. Estaremos criando outro container.

### `-d`

Significa `detached`: o container funciona em segundo plano e o terminal fica livre para outros comandos.

### `-p 8080:80`

Publica uma porta, seguindo a ordem:

```text
HOST : CONTAINER
8080 : 80
```

O navegador acessa a porta `8080` do computador. O Docker encaminha a requisição para a porta `80` do Nginx dentro do container.

```text
Navegador
localhost:8080
      ↓
Docker
      ↓
container:80
      ↓
Nginx
      ↓
index.html
```

Vários containers podem escutar a porta `80` internamente, pois cada um possui sua própria rede. No computador, porém, cada publicação precisa usar uma porta disponível:

```text
8080 → Container A:80
8081 → Container B:80
```

### `--name app-aula-container`

Define um nome legível para o container. Sem isso, o Docker gera um nome automaticamente.

### `app_aula:1.0`

É a imagem utilizada para criar o container.

Na analogia:

> A imagem é como uma massa pronta congelada. O `docker run` utiliza uma cópia dela e a coloca em produção. O container é esse bolo sendo produzido e servido, enquanto a imagem original continua disponível para criar outros.

## Acessando a aplicação

Abra no navegador:

```text
http://localhost:8080
```

Explique:

> “Agora o HTML não está sendo aberto diretamente como um arquivo. O navegador envia uma requisição ao Nginx que está executando dentro do container, e o Nginx devolve nossa página.”

## Listando containers

Execute:

```bash
docker ps
```

`ps` mostra os containers que estão executando.

Observe:

- ID do container;
- imagem utilizada;
- comando principal;
- tempo de execução;
- portas;
- nome.

Para mostrar também containers parados:

```bash
docker ps -a
```

O `-a` significa `all`.

Na analogia, esse comando mostra quais estações estão abertas e quais estão fechadas.

## Visualizando logs

Execute:

```bash
docker logs app-aula-container
```

Esse comando mostra as saídas produzidas pelo serviço dentro do container.
Para acompanhar novas mensagens:

```bash
docker logs -f app-aula-container
```

O `-f` significa `follow`.

Abra ou recarregue a página para observar o Nginx registrar novas requisições. Pressione `Ctrl+C` para sair do acompanhamento; isso não encerra o container.

## Entrando no container

Execute:

```bash
docker exec -it app-aula-container sh
```

- `docker exec`: executa um comando em um container já iniciado;
- `-i`: mantém a entrada interativa;
- `-t`: cria um terminal;
- `app-aula-container`: container escolhido;
- `sh`: shell que será aberto.

Agora estamos executando comandos dentro do container.
Liste os arquivos:

```sh
ls
```

Entre na pasta do Nginx:

```sh
cd /usr/share/nginx/html
```

Liste o conteúdo:

```sh
ls
```

Mostre o HTML:

```sh
cat index.html
```

Explique:

> “Esse é o arquivo que copiamos durante o `docker build`. Ele está no sistema de arquivos do container criado a partir da nossa imagem.”

Saia:

```sh
exit
```

Essa inspeção é ótima para a aula, mas alterações manuais dentro de containers não são uma boa maneira de publicar versões. O correto é alterar o código-fonte, construir uma nova imagem e substituir o container.

## Parando e iniciando o container

Pare o container:

```bash
docker stop app-aula-container
```

Atualize `http://localhost:8080`. A página não estará disponível porque o Nginx parou.

Confira:

```bash
docker ps
```

O container não aparece entre os que estão em execução.
Mostre os parados:

```bash
docker ps -a
```

Agora inicie o mesmo container:

```bash
docker start app-aula-container
```

Atualize o navegador. A página volta a funcionar.

Na analogia:

> `stop` fecha a estação sem desmontá-la. `start` reabre a mesma estação.

## Removendo o container

Pare novamente:

```bash
docker stop app-aula-container
```

Remova:

```bash
docker rm app-aula-container
```

Confira:

```bash
docker ps -a
```

O container desapareceu, mas a imagem continua existindo:

```bash
docker image ls
```

Explique:

> “Remover um container não remove automaticamente a imagem. Encerrar a produção de um bolo não apaga a receita nem a massa congelada que permite produzir outros.”

Também existe a remoção forçada:

```bash
docker rm -f app-aula-container
```

O `-f` para e remove o container. Use apenas quando souber exatamente qual container será removido. Para a explicação principal, `stop` seguido de `rm` torna o ciclo mais fácil de compreender.

## Criando outro container com a mesma imagem

Execute:

```bash
docker run -d -p 8080:80 --name container-novo app_aula:1.0
```

Abra novamente:

```text
http://localhost:8080
```

Temos outro container, criado a partir da mesma imagem.
Para demonstrar que uma imagem pode originar vários containers, crie um segundo usando outra porta do computador:

```bash
docker run -d -p 8081:80 --name outro-container app_aula:1.0
```

Agora temos:

```text
http://localhost:8080 → container-novo:80
http://localhost:8081 → outro-container:80
```

Liste:

```bash
docker ps
```

Explique:

> “Os dois containers são independentes, mas nasceram da mesma imagem. É como usar duas cópias da mesma massa congelada para produzir dois bolos ao mesmo tempo.”

Encerre o container extra:

```bash
docker stop outro-container
docker rm outro-container
```

## 17. Criando a versão 2.0

No arquivo `index.html`, altere:

```html
<p>Versão 1.0</p>
```

para:

```html
<p>Versão 2.0</p>
```

Agora construa outra imagem:

```bash
docker build -t app_aula:2.0 .
```

Liste:

```bash
docker image ls
```

As imagens `1.0` e `2.0` podem coexistir.

O container atual ainda mostra a versão 1.0. Isso acontece porque containers existentes não mudam quando uma nova imagem é construída.
Remova o container antigo:

```bash
docker rm -f container-novo
```

Crie um novo a partir da imagem 2.0:

```bash
docker run -d -p 8080:80 --name container-versionado app_aula:2.0
```

Abra:

```text
http://localhost:8080
```

Agora aparece a versão 2.0.

Explique:

> “Alterar a receita não transforma bolos que já estão sendo produzidos. Preparamos uma nova massa, etiquetada como versão 2.0, encerramos o container antigo e iniciamos outro usando a nova imagem.”

## O que aprendemos na primeira aula

```text
Código
  ↓ COPY
Dockerfile
  ↓ docker build
Imagem 1.0
  ↓ docker run
Container
  ↓ porta publicada
Navegador
```

Os conceitos centrais são:

- Docker ajuda a padronizar ambientes;
- Dockerfile é a receita da imagem;
- `docker build` constrói a imagem;
- imagem é um modelo reutilizável;
- `docker run` cria e inicia um container;
- container é uma execução isolada da imagem;
- portas ligam o computador ao serviço interno;
- `logs` permite observar a execução;
- `exec` permite executar comandos dentro do container;
- containers podem ser parados, removidos e substituídos;
- alterar código exige construir uma nova imagem;
- uma imagem pode originar vários containers.


## Limite desta primeira aplicação

Surge então uma nova situação:

> “E se nossa aplicação precisar de outro serviço, como PostgreSQL?”

Também surge uma pergunta ainda mais importante:

> “Se containers podem ser removidos e substituídos, onde devemos guardar os dados?”

Poderíamos criar aplicação, rede, banco, variáveis e armazenamento com vários comandos individuais. Conforme a arquitetura cresce, isso se torna difícil de repetir e explicar.
É nesse ponto que entram:

- Docker Compose, para coordenar os serviços;
- redes, para permitir a comunicação;
- variáveis de ambiente, para configurar cada execução;
- volumes, para manter fora dos containers os dados criados durante a execução.

# Aula prática: Docker, Compose, volumes e persistência

**Sistema:** CRUD de embarcações com Spring Boot e PostgreSQL. 
**Objetivo:** construir a imagem, iniciar aplicação e banco juntos e provar que os dados sobrevivem à remoção dos containers.

## Dockerfile e imagem

Abra o `Dockerfile` entregue. Ele usa duas etapas:

```dockerfile
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY src/ src/
RUN sh mvnw -B -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /workspace/target/prova1-0.0.1-SNAPSHOT.jar app.jar
USER 10001:10001
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Explique em ordem:

| Instrução | O que faz neste sistema |
|---|---|
| Primeiro `FROM` | Usa Java 17 com ferramentas de compilação; chama a etapa de `build` |
| `WORKDIR /workspace` | Define a pasta de trabalho dentro da etapa |
| `COPY` | Copia o Maven Wrapper, o arquivo de dependências e os fontes |
| `RUN ... package` | Executa o Maven durante a construção e gera o JAR |
| `-B` | Executa Maven sem interação |
| `-DskipTests` | Não executa testes nessa construção; o teste de contexto existente precisa de banco. Isso não equivale a validar a aplicação |
| Segundo `FROM` | Começa a imagem final com o ambiente Java de execução |
| `COPY --from=build` | Traz somente o JAR gerado na primeira etapa |
| `USER` | Executa a aplicação com um usuário sem privilégios de root |
| `EXPOSE 8080` | Documenta a porta interna; não publica essa porta sozinho |
| `ENTRYPOINT` | Define o processo iniciado ao executar o container |

JDK cria e compila (inclui o JRE), e JRE é o runtime, feito para rodar o software java (inclui o JVM java virtual machine), carrega e executa o código
> “Compilar é uma etapa. Executar é outra. Na imagem final precisamos do programa e do Java de execução; Maven e código-fonte ficam na etapa de construção.”

Execute:

```bash
docker compose build app
```

> “Estamos pedindo ao Compose para construir a imagem do serviço app a partir do Dockerfile. Ainda não estamos iniciando a aplicação.”

Se quiser relacionar com o comando básico, `docker build -t embarcacoes:aula .` também constrói uma imagem usando este Dockerfile. `-t` dá um nome e tag; `.` indica o contexto atual. Não é necessário executar ambos na demonstração.

## Compose explicado

Abra `compose.yaml` e explique primeiro `services`: são as partes da aplicação que o Compose executará.

### Serviço banco

```yaml
banco:
  image: postgres:17
  environment:
    POSTGRES_DB: prova1
    POSTGRES_USER: postgres
    POSTGRES_PASSWORD: aula1234
  volumes:
    - dados-postgres:/var/lib/postgresql/data
```

`image` usa uma imagem pronta. As variáveis configuram o banco inicial e o usuário quando o diretório de dados está vazio. Mudar a senha no YAML depois não altera automaticamente a senha de um banco já inicializado.

A versão 17 foi escolhida explicitamente para manter o caminho de dados desta aula. Não substitua por `latest` sem revisar a configuração: a imagem oficial mudou a organização de dados nas versões 18 ou posteriores. Veja a [referência da imagem PostgreSQL](https://hub.docker.com/_/postgres).

### A linha que conecta o volume

O PostgreSQL é o responsável por organizar os registros, mas ele precisa guardar seus arquivos em algum lugar. Por padrão, a imagem do PostgreSQL 17 utiliza:

```text
/var/lib/postgresql/data
```

Se os arquivos permanecessem somente na camada gravável do container, seriam descartados junto com ele. Por isso, conectamos esse diretório a um volume:

```text
 dados-postgres : /var/lib/postgresql/data
       |                       |
 caderno guardado        lugar onde o PostgreSQL
 fora do container       escreve e procura os dados
```

Na analogia:

```text
Usuário faz um cadastro
        ↓
Aplicação recebe o pedido
        ↓
PostgreSQL organiza o registro
        ↓
Volume guarda o caderno fora do container
```

O lado esquerdo, `dados-postgres`, é o volume administrado pelo Docker. O lado direito, `/var/lib/postgresql/data`, é o caminho que o PostgreSQL enxerga dentro do container. A montagem conecta os dois.
A declaração no final do arquivo:

```yaml
volumes:
  dados-postgres:
```

registra o volume. O Compose o cria se ainda não existir. O nome do projeto `aula-embarcacoes`, definido no início do YAML, resulta no nome `aula-embarcacoes_dados-postgres` no Docker.

> “O container do PostgreSQL é como a estação onde um funcionário trabalha. O volume é o caderno de pedidos guardado no escritório. Podemos desmontar a estação e montar outra; quando o novo funcionário recebe o mesmo caderno, encontra os registros anteriores.”

Isso explica por que imagem e volume têm funções diferentes:

| Recurso | Conteúdo | Quando muda |
|---|---|---|
| Imagem | Aplicação e ambiente preparados | Quando executamos um novo build |
| Container | Processo em execução e camada temporária | Enquanto aquela instância funciona |
| Volume | Cadastros e arquivos persistentes do banco | Conforme os usuários usam o sistema |

Um volume é gerenciado pelo Docker. Um bind mount liga uma pasta específica do computador a uma pasta do container. Nesta aula usamos volume para os dados do banco. Consulte a [documentação de volumes](https://docs.docker.com/engine/storage/volumes/).

### Serviço app e rede

```yaml
app:
  build: .
  ports:
    - "127.0.0.1:8081:8080"
  environment:
    SERVER_PORT: "8080"
    SPRING_DATASOURCE_URL: jdbc:postgresql://banco:5432/prova1
    SPRING_DATASOURCE_USERNAME: postgres
    SPRING_DATASOURCE_PASSWORD: aula1234
```

- `build: .`: usa o Dockerfile desta pasta.
- `127.0.0.1:8081:8080`: acesso local pela porta 8081 do computador, encaminhado à porta 8080 do container.
- `SERVER_PORT`: mantém o Spring Boot na porta interna 8080, mesmo que você tenha configurado 8081 no arquivo local.
- `SPRING_DATASOURCE_*`: fornece a conexão do banco para esta execução, sem mudar o código Java.
- `banco`: é o nome do serviço e o endereço usado pela aplicação na rede criada pelo Compose.

> “Dentro do container da aplicação, localhost é o próprio container. Para acessar o outro serviço, usamos banco.”

O banco não tem `ports`: a aplicação o acessa pela rede interna. Isso também evita conflito com um PostgreSQL local na porta 5432.

### Esperar o banco ficar pronto

O `healthcheck` executa `pg_isready -U postgres -d prova1` para verificar se o banco aceita conexões. Ele verifica a cada cinco segundos, com limites de tempo e tentativas definidos no arquivo.

O `depends_on` com `condition: service_healthy` faz o Compose esperar essa condição antes de iniciar a aplicação. Só ordenar a criação dos containers não garante que o banco esteja pronto. Essa condição resolve a ordem inicial, mas não reinicia automaticamente a aplicação se o banco falhar depois. [Referência de ordem de inicialização](https://docs.docker.com/compose/how-tos/startup-order/).

## Executar e usar o CRUD

Execute:

```bash
docker compose up -d --build
```

Traduza:

- `docker compose`: gerencia os serviços descritos no arquivo.
- `up`: cria/inicia os serviços e prepara os recursos necessários.
- `-d`: deixa os serviços em segundo plano e devolve o terminal.
- `--build`: constrói a imagem da aplicação antes de iniciar, usando cache quando possível.

Como a imagem já foi construída no passo anterior, `docker compose up -d` também bastaria aqui. A forma com `--build` é útil quando acabamos de alterar o código.

Depois:

```bash
docker compose ps
docker compose logs -f app
```

`ps` mostra o estado dos serviços e o mapeamento de portas. Nos logs, espere `Started Prova1Application`; apenas ver o container como “Up” não garante que o Spring terminou de iniciar. Saia dos logs com `Ctrl+C` e abra http://localhost:8081/.

cadatre uma embarcação

Opcionalmente, mostre que o registro chegou ao PostgreSQL:

```bash
docker compose exec banco psql -U postgres -d prova1 -c "SELECT id, nome, registro, quantidade_passageiros FROM embarcacao WHERE registro = 'AULA-001';"
```

Explique: `exec banco` executa um comando no serviço em funcionamento; `psql` é o cliente PostgreSQL; `-U` escolhe o usuário, `-d` escolhe o banco e `-c` executa a consulta. Aqui `-d` pertence ao `psql` e não significa segundo plano.

## Prova de persistência

Antes de remover os containers, mostre seus IDs e o volume:

```bash
docker compose ps -q
docker volume ls --filter label=com.docker.compose.project=aula-embarcacoes
```

`ps -q` mostra apenas IDs. Guarde uma captura ou deixe o resultado no histórico para comparar depois.

Pergunte:

> “Se eu remover os dois containers, o cadastro AULA-001 vai desaparecer? Onde os dados estão sendo gravados?”

Execute:

```bash
docker compose down
```

Esse comando para e remove os containers e a rede do projeto. Por padrão, preserva o volume nomeado.

Na analogia, fechamos e desmontamos as estações da cozinha, mas não jogamos fora o caderno de pedidos.

```text
Containers antigos
estações da cozinha
        ↓ removidos

Volume
caderno de pedidos
        ↓ permanece

Containers novos
novas estações
        ↓ recebem o mesmo caderno

Dados continuam disponíveis
```

Confira:

```bash
docker compose ps -a
docker volume inspect aula-embarcacoes_dados-postgres
```

A primeira saída não deve mostrar containers do projeto. A segunda mostra que o volume continua existindo. O `Mountpoint` é um caminho no host do Docker; no Docker Desktop, ele normalmente fica dentro da máquina virtual usada pelo Docker.

Recrie:

```bash
docker compose up -d
docker compose logs -f app
```

Espere a inicialização, saia dos logs e compare os IDs:

```bash
docker compose ps -q
```

Abra a página e clique em **Atualizar lista**. Mostre o mesmo ID, o registro `AULA-001` e os **25 passageiros**. Você pode repetir a consulta SQL do passo anterior.

> “Os IDs dos containers mudaram, portanto são novas estações de trabalho. O registro e a alteração continuam no banco porque o PostgreSQL recebeu o mesmo caderno: o volume.”

Não use apenas Titanic, Sereia ou Pedra Azul como prova: essas embarcações são inseridas pela migração inicial e reapareceriam até em um banco novo. O registro criado ao vivo diferencia persistência de reinicialização.

A distinção entre `down` e remoção de volumes está na [referência oficial de docker compose down](https://docs.docker.com/reference/cli/docker/compose/down/).

## Comando destrutivo: explicar, não executar no roteiro principal


Para encerrar a aula preservando os dados, use apenas:

```bash
docker compose down
```


# Slide 1 — Persistência não é sincronização

> “O volume mantém o caderno, mas não distribui cópias dele.”

## Notas do apresentador

Explique que o volume preserva os dados quando os containers são substituídos no mesmo host Docker.

Ele não funciona como Google Drive, Dropbox ou outro serviço de sincronização. Criar volumes com o mesmo nome em dois computadores não faz com que eles possuam o mesmo conteúdo.

O nome igual não representa o mesmo armazenamento físico.

---

# Slide 2 — Cada computador possui seus próprios dados

> “Executar o mesmo Compose não significa compartilhar o mesmo volume.”

## Notas do apresentador

Explique que, se cada pessoa executar o projeto em seu próprio computador, cada Docker Engine criará seus próprios containers, rede e volume.

Os arquivos `Dockerfile` e `compose.yaml` padronizam o ambiente, mas os dados produzidos durante a execução continuam locais.

Na analogia:

> “Cada pessoa teria sua própria cozinha e seu próprio caderno de pedidos. Escrever em um caderno não atualiza automaticamente o outro.”

---

# Slide 3 — Como várias pessoas compartilham os dados?

> “Todos fazem pedidos para a mesma cozinha, e o funcionário registra tudo no mesmo caderno.”

## Notas do apresentador

Explique que não precisamos sincronizar vários volumes quando todos os usuários acessam a mesma aplicação.

A aplicação fica em um computador servidor. Todos acessam esse servidor pelo navegador. As requisições chegam ao mesmo backend, que grava tudo no mesmo PostgreSQL e no mesmo volume.

O banco não precisa ser acessado diretamente pelos usuários. O fluxo adequado é:

```text
Usuários → aplicação → banco
```

Evite:

```text
Usuários → banco diretamente
```

---

# Slide 4 — Dados centralizados não significam tela em tempo real

Explique que os dados ficam centralizados assim que a aplicação grava no PostgreSQL. Entretanto, o navegador de outra pessoa não recebe automaticamente a informação de que algo mudou.

Na aplicação atual, o usuário precisa clicar em **Atualizar lista**.

Para atualizar as telas automaticamente, seria possível utilizar consultas periódicas, Server-Sent Events ou WebSocket. Não aprofunde a implementação; apresente apenas como possibilidade.

Finalize explicando que uma implantação real também precisaria de autenticação, HTTPS, controle de acesso, backups, monitoramento e tratamento seguro das credenciais.

## Fala final

> “O volume mantém o caderno quando trocamos os containers, mas não distribui cópias desse caderno entre computadores. Para compartilhar os mesmos dados, colocamos a aplicação e o banco em um servidor central e fazemos todos os usuários acessarem aquela mesma cozinha.”


## vantagens limitações e segurança

| Vantagens | Limitações | Segurança |
|---|---|---|
| Mesmo ambiente em máquinas diferentes | Exige conhecer imagens, redes, portas e volumes | Não colocar senhas no código ou nas imagens |
| Isolamento entre aplicações | Persistência e backups precisam ser planejados | Manter imagens e dependências atualizadas |
| Containers leves e rápidos de recriar | Consome recursos do computador | Usar poucos privilégios e expor somente o necessário |
| Facilita testes e atualizações | Não substitui monitoramento | Usar autenticação, HTTPS e backups em produção |

Fala sugerida:

> “Docker facilita a padronização, o isolamento e a substituição das aplicações, mas não resolve tudo sozinho. Ainda precisamos planejar dados, recursos, monitoramento e backups. Container também não significa segurança automática: devemos proteger credenciais, atualizar as imagens, limitar acessos e expor apenas o necessário.”
