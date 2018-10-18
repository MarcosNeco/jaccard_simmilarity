## Marcos Neco
Desafio Big Data
================

## Considerações Gerais

Você deverá usar este repositório para desenvolver seu projeto, ou seja, todos os seus commits devem estar registrados aqui, pois queremos ver como você trabalha.

Use uma das seguites linguagens de programação: Python, Java ou Scala.

Sua solução deve ser simples de ser executada, seguindo as condições abaixo:

* Registre tudo: testes que forem executados, ideias que gostaria de implementar se tivesse tempo (explique como você as resolveria, se houvesse tempo), decisões que foram tomadas e os seus porquês, arquiteturas que foram testadas e os motivos de terem sido modificadas ou abandonadas;
* **Para facilitar a solução pode usar somente memória para manter o estado**, ou seja, sem usar banco de dados. 

## O Problema

O seu desafio consiste em recomendar documentos similares a um dado documento no estilo "quem viu isso, também viu...".
Seu algoritmo deve se basear no cálculo de covisitação. Isto é, dois documentos são considerados similares se um
conjunto grande de usuários visitou os dois documentos. Mais precisamente, a similaridade entre o documento A e B é dada pela
quantidade de usuários que visitaram os dois documentos (A e B) dividido pela quantidade de usuários únicos que visitaram
qualquer um dos documentos (A ou B). Essa métrica é conhecida como [Jaccard similarity coefficient](https://en.wikipedia.org/wiki/Jaccard_index).

Você deve criar uma API HTTP com duas interfaces:

### POST `/<url>/view`:

  Esse interface será chamada cada vez que um usuário ver um documento. Recebe o parâmetro `user`.

  Exemplo de uso: `$ curl -d"user=user1" http://localhost:8080/www.globoplay.com/view/`

### GET `/<url>/similar`:

Essa interface deve retornar no formato json a lista dos dez documentos mais similares em ordem decrescente. Esse cálculo não precisa ser online.

Exemplo de uso: `$ curl http://localhost:8080/www.globoplay.com/similar/`

Exemplo de retorno:

    [{
        "url": "www.globoplay.globo.com/v/4455325/",
        "score": 1.0
     }, {
        "url": "www.globoplay.globo.com/v/4455292/",
        "score": 0.5
    }]

## Requisitos

### Escalabilidade

Faça uma bateria de testes de performance para garantir que sua solução possa ser usada na escala da globo.com:
* ~200 milhões de pageviews por dia
* ~3k recomendações por minuto.

Para isto, disponibilizamos dentro da pasta load uma possível configuração do http://locust.io.

Um exemplo de execução seria:

```
locust -c 500 -r 50 -n 12000 -f "$(ROOT_DIR)/load/rng_view.py" --host=http://localhost --no-web
```

### Inicialização

Devemos ser capazes de rodar sua aplicação e iniciar o serviço com os seguintes passos
```
git clone .../repositorio.git
cd repositorio
./configure
make
make run
```

### Extras

Ao invés de receber o registro de visualização de documentos via POST, consuma essas visualizações de uma fila. Você pode fazer processamento em stream utilizando Kafka, Apache Samza, Apache Storm, Spark Streaming, uma combinação desses ou outra biblioteca de sua preferência.

## Avaliação

1. Você deverá entregar seu código e uma documentação (basta um README.md atualizado) descrevendo
a arquitetura escolhida, decisões tomadas, aprendizados, o que faria se tivesse mais tempo,
requisitos necessários, explicação de como rodar o código e testes.

2. Seu código e documentação serão observados por uma equipe de desenvolvedores que avaliará a simplicidade e clareza da solução, arquitetura, estilo de código, testes unitários, testes funcionais, teste de carga, nível de automação dos testes, implementação do código, e a organização geral projeto.


### Dicas

- Use ferramentas e bibliotecas open-source (desde que não façam todo o trabalho para você);
- Documente as decisões e porquês;
- Automatize o máximo possível;
- Em caso de dúvidas, pergunte.
