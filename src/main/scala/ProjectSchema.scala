import sangria.schema._

object ProjectSchema {
  val Id = Argument("id", IntType)
  
  val QueryType = ObjectType("Query", fields[Repository, Unit](
    Field("categories", ListType(Category.graphqlType),
      description = Some("Returns a list of all categories"),
      resolve = _.ctx.categories),
    Field("styles", ListType(Style.graphqlType),
      description = Some("Returns a list of all styles"),
      resolve = _.ctx.styles),
    Field("beers", ListType(Beer.graphqlType),
      description = Some("Returns a list of all beers"),
      resolve = _.ctx.beers),
    Field("breweries", ListType(Brewery.graphqlType),
      description = Some("Returns a list of all breweries"),
      resolve = _.ctx.breweries),

    Field("category", OptionType(Category.graphqlType),
      description = Some("Returns a category"),
      arguments = Id :: Nil,
      resolve = c => c.ctx.categoryFetcher.deferOpt(c.arg(Id))),
    Field("style", OptionType(Style.graphqlType),
      description = Some("Returns a style"),
      arguments = Id :: Nil,
      resolve = c => c.ctx.styleFetcher.deferOpt(c.arg(Id))),
    Field("brewery", OptionType(Brewery.graphqlType),
      description = Some("Returns a brewery"),
      arguments = Id :: Nil,
      resolve = c => c.ctx.breweryFetcher.deferOpt(c.arg(Id))),
    Field("beer", OptionType(Beer.graphqlType),
      description = Some("Returns a beer"),
      arguments = Id :: Nil,
      resolve = c => c.ctx.beerFetcher.deferOpt(c.arg(Id)))))

  val schema = Schema(QueryType)
}
