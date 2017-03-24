# Ubeer

## Query examples

### Arguments

```graphql
{
  beer(id: 360) {
    name
    description(charLimit: 50)
  }
}
```

<a href="http://localhost:8080/graphiql/index.html?query=%7B%0A%20%20beer(id%3A%20360)%20%7B%0A%20%20%20%20name%0A%20%20%20%20description(charLimit%3A%2050)%0A%20%20%7D%0A%7D%0A&variables=">Run this example</a>

### Aliases

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

<a href="http://localhost:8080/graphiql/index.html?query=%7B%0A%20%20beerOne%3A%20beer(id%3A%20360)%20%7B%0A%20%20%20%20name%0A%20%20%7D%0A%20%20beerTwo%3A%20beer(id%3A%20440)%20%7B%0A%20%20%20%20name%0A%20%20%7D%0A%7D&variables=">Run this example</a>

### Reusable fragments

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

<a href="http://localhost:8080/graphiql/index.html?query=%7B%0A%20%20beerOne%3A%20beer(id%3A%20360)%20%7B%0A%20%20%20%20...beerSummary%0A%20%20%7D%0A%20%20beerTwo%3A%20beer(id%3A%20440)%20%7B%0A%20%20%20%20...beerSummary%0A%20%20%7D%0A%7D%0A%0Afragment%20beerSummary%20on%20Beer%20%7B%0A%20%20name%0A%20%20brewery%20%7B%0A%20%20%20%20name%0A%20%20%7D%0A%7D%0A&variables=">Run this example</a>

### Variables

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

<a href="http://localhost:8080/graphiql/index.html?query=query%20(%24city%3A%20String)%20%7B%0A%20%20breweries(city%3A%20%24city)%20%7B%0A%20%20%20%20name%0A%20%20%20%20address%0A%20%20%20%20website%0A%20%20%7D%0A%7D%0A&variables=%0A%7B%0A%09%22city%22%3A%20%22Brooklyn%22%0A%7D">Run this example</a>

### Directives

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

<a href="http://localhost:8080/graphiql/index.html?query=query%20(%24skipBeers%3A%20Boolean!)%20%7B%0A%20%20breweries%20%7B%0A%20%20%20%20name%0A%20%20%20%20address%0A%20%20%20%20website%0A%20%20%20%20beers%20%40skip(if%3A%20%24skipBeers)%20%7B%0A%20%20%20%20%20%20name%0A%20%20%20%20%20%20abv%0A%20%20%20%20%7D%0A%20%20%7D%0A%7D%0A&variables=%7B%0A%20%20%22skipBeers%22%3A%20false%0A%7D">Run this example</a>
