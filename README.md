## [DynamoDB Enhanced Client](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/dynamodb-enhanced-client.html)

O cliente avançado do Amazon DynamoDB é uma biblioteca de alto nível que faz parte do AWS SDK for Java versão 2 (v2). Ele oferece uma maneira simples de mapear classes do lado do
cliente para tabelas do DynamoDB. Defina as relações entre as tabelas e suas classes de modelo correspondentes no seu código. Depois, você poderá executar intuitivamente várias
operações de criação, leitura, atualização ou exclusão (CRUD) em tabelas ou itens do DynamoDB.
O AWS SDK for Java v2 inclui um conjunto de anotações que você pode usar com um bean Java para gerar rapidamente um TableSchema para mapear suas classes para tabelas. Como opção,
se você declarar cada TableSchema explicitamente, não será necessário incluir anotações em suas classes.

## Introdução

Projeto SpringBoot Web com Groovy/Java exemplificando como mapear tabelas do DynamoDb em entidades usando anotações similar ao SpringJPA.
Com endpoints para fazer operações de CRUD e testar o funcionamento na prática.

## Tecnologias

* SpringBoot 2.6.7
* AWS 2.17.182 → `implementation 'software.amazon.awssdk:dynamodb-enhanced:2.20.85'`
* Java 11
* Groovy 3.0.10
* Gradle 7.4.1

## Explicação

Para quem conhece o *SpringJPA* já está familiarizado com o uso de anotações para mapear tabela(s) e coluna(s) em objeto/entidade e campos/atributos.
A implementção está seguindo o padrão MVC, estão temos Controller, Service, Repository, Model.

* `@DynamoDbBean` *[obrigatória] [classe]*: Identifica a classe anotada como sendo uma entidade mapeável do DynamoDb;
* `@DynamoDbPartitionKey` *[obrigatória] [método]*: Indica que o atributo anotado é a chave de partição primária (hashKey) da tabela DynamoDb.
* `@DynamoDbSortKey` *[opcional] [método]*: Indica que o atributo anotado é a chave de classificação primária (sortKey) opcional da tabela DynamoDb;
* `@DynamoDbAttribute(String value)` *[opcional] [método]*: Especifica um nome diferente para o atributo do que o mapeador inferiria automaticamente usando uma estratégia de
  nomenclatura;
  * Por exemplo: se temos na tabela a coluna `dtNasc` e queremos mapear na entidade para `dataNascimento` anotamos com `@DynamoDbAttribute('dtNasc')`;
* `@DynamoDbConvertedBy` *[opcional] [método]*: Usada para associar um *AttributeConverter* personalizado ao atributo;
* `@DynamoDbIgnore` *[opcional] [método]*: Indica que o atributo será ignorado pelo mapeador, ou seja, não participa do schema da tabela;
* As anotações de nível de método não podem ser usadas no atributo/campo diretamente, sendo assim,
  mesmo que você esteja usando Groovy ou Java com Lombok precisará ter um getter ou setter do campo para ser anotado;
* O mapeamento não funciona com tipos primitivos: `int, long, boolean...`, então use o tipo equivalente: `Integer, Long, Boolean...`;
* As anotações: `@DynamoDbPartitionKey, @DynamoDbSortKey`  só podem ser usadas em atributos com um tipo escalar do DynamoDb (string, número ou binário);
* Existem outras anotações que podem ser vistas no pacote `software.amazon.awssdk.enhanced.dynamodb.mapper.annotations`;
* Para instanciar o *DynamoDbEnhancedClient* temos que passar o *DynamoDbClient* como parâmetro, ou seja, debaixo dos panos ele usa o cliente "tradicional".

## Executando

### LocalStack - docker-compose

* Necessário ter docker e docker-compose instalados;
* Execute o seguinte comando na raiz do projeto: `docker-compose -up`;
* Após iniciar, os recursos do LocalStack estarão disponíveis na porta: `4566` e região: `sa-east-1`
* As configurações podem ser alteradas no arquivo [docker-compose.yaml](docker-compose.yaml), além de (des)ativar os serviços desejados.

### Detalhes da tabela User:
![img.png](img.png)
