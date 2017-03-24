# Ubeer

## Running the project

In your console, run:

```sh
sbt run
```

This will compile the project, load the JSON files in memory, print the GraphQL
schema in the console and start the Akka HTTP server.

Once the server is started you can:

- Play with the client app at <http://localhost:8080/ubeer/index.html>.
- Run queries interactively using [GraphiQL](https://github.com/graphql/graphiql)at <http://localhost:8080/graphiql/index.html> (see below for some examples).

## Query examples

The following examples illustrate some capabilities GraphQL offers on this project.

Running these from the links given below assumes that you are running this
project locally (see above).

### Arguments

<a href="http://localhost:8080/graphiql/index.html?query=%7B%0A%20%20beer(id%3A%20360)%20%7B%0A%20%20%20%20name%0A%20%20%20%20description(charLimit%3A%2050)%0A%20%20%7D%0A%7D%0A&variables=">Run this example</a>

```graphql
{
  beer(id: 360) {
    name
    description(charLimit: 50)
  }
}
```

### Aliases

<a href="http://localhost:8080/graphiql/index.html?query=%7B%0A%20%20beerOne%3A%20beer(id%3A%20360)%20%7B%0A%20%20%20%20name%0A%20%20%7D%0A%20%20beerTwo%3A%20beer(id%3A%20440)%20%7B%0A%20%20%20%20name%0A%20%20%7D%0A%7D&variables=">Run this example</a>

```graphql
{
  beerOne: beer(id: 360) {
    name
  }
  beerTwo: beer(id: 440) {
    name
  }
}
```

### Reusable fragments

<a href="http://localhost:8080/graphiql/index.html?query=%7B%0A%20%20beerOne%3A%20beer(id%3A%20360)%20%7B%0A%20%20%20%20...beerSummary%0A%20%20%7D%0A%20%20beerTwo%3A%20beer(id%3A%20440)%20%7B%0A%20%20%20%20...beerSummary%0A%20%20%7D%0A%7D%0A%0Afragment%20beerSummary%20on%20Beer%20%7B%0A%20%20name%0A%20%20brewery%20%7B%0A%20%20%20%20name%0A%20%20%7D%0A%7D%0A&variables=">Run this example</a>

```graphql
{
  beerOne: beer(id: 360) {
    ...beerSummary
  }
  beerTwo: beer(id: 440) {
    ...beerSummary
  }
}

fragment beerSummary on Beer {
  name
  brewery {
    name
  }
}
```

### Variables

<a href="http://localhost:8080/graphiql/index.html?query=query%20(%24city%3A%20String)%20%7B%0A%20%20breweries(city%3A%20%24city)%20%7B%0A%20%20%20%20name%0A%20%20%20%20address%0A%20%20%20%20website%0A%20%20%7D%0A%7D%0A&variables=%0A%7B%0A%09%22city%22%3A%20%22Brooklyn%22%0A%7D">Run this example</a>

```graphql
query ($city: String) {
  breweries(city: $city) {
    name
    address
    website
  }
}
```

```json
{
  "city": "Brooklyn"
}
```

### Directives

<a href="http://localhost:8080/graphiql/index.html?query=query%20(%24skipBeers%3A%20Boolean!)%20%7B%0A%20%20breweries%20%7B%0A%20%20%20%20name%0A%20%20%20%20address%0A%20%20%20%20website%0A%20%20%20%20beers%20%40skip(if%3A%20%24skipBeers)%20%7B%0A%20%20%20%20%20%20name%0A%20%20%20%20%20%20abv%0A%20%20%20%20%7D%0A%20%20%7D%0A%7D%0A&variables=%7B%0A%20%20%22skipBeers%22%3A%20false%0A%7D">Run this example</a>

```graphql
query ($skipBeers: Boolean!) {
  breweries {
    name
    address
    website
    beers @skip(if: $skipBeers) {
      name
      abv
    }
  }
}
```

```json
{
  "skipBeers": false
}
```
