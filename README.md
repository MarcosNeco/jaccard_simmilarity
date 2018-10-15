<h1>Arquitetura</h1>

   <h2>Provisionamento de ambiente</h2>
    Para o provisionamento da infraestrutura foi escolhido o ansible, que automatiza o processo de configuração
    de infraestrutura, possibilitando inclusive o seu versionamento. Uma outra opção seria o chef, porém após pesquisa cheguei a conclusão de que
    é mais simples e leve para começar a utilizar.


   <h2>Serviço de Sincronização e Configuração</h2>
   Como ferramenta de configuração e sincronização foi escolhido o zookeeper. Para fins desse projeto ele só vai ser utilizado para busca de configurações, porém se a solução fosse evoluir para algo mais robusto como, por exemplo salvar a data que foi disponibilizado.




<h1>Iniciar Aplicação</h1>

<h3>Zookeeper</h3>

Start server

```
>cd C:\
>.\zookeeper-3.4.12\bin\zkServer.cmd
```


Create Znodes Zookeeper

```
>create /jacsim /configs
>create /jacsim/configs /kafka.server
>create /jacsim/configs/kafka.server "localhost"

```

<h3>Kafka</h3>

start kafka server

```
> .\kafka_2.11\bin\windows\kafka-server-start.bat \kafka_2.11\config\server.properties
```

create kafka topic

```
> .\kafka_2.11\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic viewdoc
```