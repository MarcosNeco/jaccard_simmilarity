<h1>Arquitetura</h1>

  
   <h2>Provisionamento de ambiente</h2>
    Para o provisionamento da infraestrutura foi escolhido o ansible, que automatiza o processo de configuração
    de infraestrutura, possibilitando inclusive o seu versionamento. Uma outra opção seria o chef, porém após pesquisa cheguei a conclusão de que
    é mais simples e leve para começar a utilizar.


   <h2>Serviço de Sincronização e Configuração</h2>
   Como ferramenta de configuração e sincronização foi escolhido o zookeeper. Para fins desse projeto ele só vai ser utilizado para busca de configurações, porém se a solução fosse evoluir para algo mais robusto como, por exemplo salvar a data que foi disponibilizado.

   <h2>Evoluções</h2>
   <h4>Camada de armazenamento</h4>
   Com uma futura evolução do sistema para atender uma demanda real, o modelo de armazenamento que foi utilizado teria que ser repensado, ou seja, o armazenamento
   em memória seria alterado para um banco chave e valor distribuído, nesse caso, seria utilizado o banco redis que tem já tem essa característica. Isso evitaria de ter
   que trazer todos os dados dos Executors para o Driver de uma única máquina, sendo esse um possível causador de exceção de memória. Além de ter que precorrer os dados de maneira 
   sequêncial.

   <h4>Configuração e sincronização</h4>
    Para gerenciamento de configurações e seria utilizado o zookeeper para TODAS as configurações possíveis do sistema, seria útil principalmente se o projeto fosse quebrado em serviços menores exigindo assim o sincronismo entre os diferentes componentes. No status atual o zookeeper
    é utilizado apenas como depêndencia do kafka.

   <h4>Sistema de Log</h4>
    Aqui seria configurado uma biblioteca de log como o log4j, que facilitaria a divisão de tipos de logs, no cenário atual foi utilizado apenas alguns prints simples.

   <h4>Componentização</h4>
    A quebra do projeto em serviços menores que possam trabalhar de forma isolada é um ponto que eu julgo muito importante como evolução futura do sistema. No cenário atual vejo a necessidade de 4 projetos distintos com papéis bem definidos, são eles:
      1. Webservice(spring boot) responsável por receber as requisições e controlar as devidas chamadas
      2. Componente responsável pelo gerenciamento das configurações e sincronismo do, inicialmente utilizando zookeeper
      3. Componente core, com o algoritmo de calculo de similaridade, spark streaming para o consumo e execução do algoritmo em paralelo
      4. Componente para armazenamento dos dados distribuído e em memória, inicialmente utilizando o apache ignite ou derrepente o banco memsql(porém esse último precisaria de um estudo maior, já que não conheço muito)

    Obs:Tive alguns problemas com conflitos de bibliotecas, por isso a necessidade de alguns excludes no build.sbt, com a componentização esse problema deixaria de existir.

<h1>Iniciar Aplicação</h1>
Para provisionamento do ambiente foi criado um shell script que tem a tarefa de extrair os serviços do kafka e zookeeper, além disso ira realizar a subida desses serviços
para na máquina local e realizar outras tarefas de build do projeto. Para executar o script de provisionamento deve se entrar na pasta e:

    ```cd ./dir_projeto//jaccard_similarity//provisioning.sh run```




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
> .\kafka_2.11\bin\windows\kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic view_doc
```
