<h1>Requisitos</h1>

    *scala 2.11.2
    *java 1.8+
    *variáveis de ambiente setado

<h1>Arquitetura</h1>

   As tecnologias que foram utilizadas no projeto são:

        * Scala
        * SpringBoot
        * Kafka
        * Spark
        * SparkStreming

   As tecnologias foram escolhidas por serem bem conhecidas e utilizadas no mundo de BigData. Principalmente spark e kafka, com o spark conseguimos realizar processamento de uma grande massa de dados utlizando o poder de várias máquinas em paralelo, além disso
   criar operações de tranformação são simples, já que toda a complexidade de uma aplicação distribuída tem é abstraida por chamadas simples oferecidas pelo spark. Como no ambiente de teste a quantidade de recursos é bem limitada, não foi possível tirar quase nada
   o poder real de processamento, apenas serviu como meio para os testes.
       Como sistema de mensageria utilizando stream foi utilizado o kafka, ele tem a característica de ser muito rápido e assim como o spark é um sistema distribuído, onde eu consigo definir partições e localidades de acordo com a característica dos meus daoos,
   atualmente a chave de cada mensagem publicada é a mesma, porém uma abordagem interessante seria salvar como chave o ip da máquina que produziu a mensagem, dessa forma eu iria talvez conseguir realizar uma distribuição de mensagens por loocalidade.
       Scala foi escolhida por ser uma linguagem muito concisa, utilizando o poder do paradigma funcional quanto no paradigma orientado a objetos, além disso o spark tem a característica de liberar primeiramente as suas versões em scala e depois
   nas outras linguagens (java, python e R).
       SpringBoot foi utilizado para criação de duas chamadas de webService, uma para visualização e outra para retonar os 10 documentos mais similares. Já que ele é super rápido e fácil de subir com quase nenhuma configuração, além de poder ser utilizado diretamente
   na linguagem scala

   <h2>Fluxo do sistema</h2>
   Basicamente dois endpoints são publicados utilizando o springBoot, após isso é iniciado o consumo do topico view_doc utilizando no spark uma biblioteca de integração com o karaf, a cada 15 segundos é realizado um novo consumo no tópico, e um novo calculo do
    coeficiente de similaridade é calculado considerando o agregamento necessário com os dados que foram processados nos streams anteriores. Para manter o estado da minha aplicação eu optei por utlizar um temp table em memória que o spark oferece, porém
    em um ambiente maior é evidente a necessidade de um banco distribuído em memória, uma possível opção seria o apache ignite que acredito que adequaria bem para esse caso.



   <h2>Provisionamento de ambiente</h2>
    Para o provisionamento da infraestrutura foi criado um bash script que fica dentro do projeto, chama-se provioning.sh, basicamente o papel dele é extrair os servidores kafka e zookeeper além de subir os serviços necesários.
    A ideia era criar um arquivo .bat também para evitar incompatibilidade entre os sistemas operacionais, porém pelo tempo não consegui.


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

       cd ./dir_projeto/
        ./provisioning.sh run