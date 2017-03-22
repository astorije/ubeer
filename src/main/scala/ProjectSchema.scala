import sangria.macros.derive._
import sangria.schema._

object ProjectSchema {
  val Id = Argument("id", IntType)
  val OptionalCity = Argument("city", OptionInputType(StringType))

  val CategoryType: ObjectType[Repository, Category] = deriveObjectType(
    ObjectTypeDescription("A category"),
    AddFields(
      Field("styles", ListType(StyleType),
        resolve = c => c.ctx.stylesByCategory(c.value.id)
      )
    )
  )

  val StyleType: ObjectType[Repository, Style] = deriveObjectType(
    ObjectTypeDescription(
      "A style of beer, used to differentiate beers by factors such as " +
      "colour, flavour, strength, ingredients, production method, recipe, " +
      "history, or origin."
    ),
    AddFields(
      Field("beers", ListType(BeerType),
        resolve = c => c.ctx.beersByStyle(c.value.id)
      )
    ),
    ReplaceField("category_id", Field("category", CategoryType,
      resolve = c => c.ctx.category(c.value.category_id).get
    ))
  )

  val BreweryType: ObjectType[Repository, Brewery] = deriveObjectType(
    ObjectTypeDescription("A brewery"),
    AddFields(
      Field("beers", ListType(BeerType),
        resolve = c => c.ctx.beersByBrewery(c.value.id)
      )
    )
  )

  // Type must be specified because of recursion: beer > brewery > beers > ...
  val BeerType: ObjectType[Repository, Beer] = deriveObjectType(
    ObjectTypeDescription("A beer"),
    ReplaceField("brewery_id", Field("brewery", BreweryType,
      resolve = c => c.ctx.brewery(c.value.brewery_id).get
    )),
    ReplaceField("style_id", Field("style", StyleType,
      resolve = c => c.ctx.style(c.value.style_id).get
    ))
  )

  val QueryType = ObjectType("Query", fields[Repository, Unit](
    Field("categories", ListType(CategoryType),
      description = Some("Returns a list of all categories"),
      resolve = _.ctx.categories
    ),
    Field("styles", ListType(StyleType),
      description = Some("Returns a list of all styles"),
      resolve = _.ctx.styles
    ),
    Field("beers", ListType(BeerType),
      description = Some("Returns a list of all beers"),
      resolve = _.ctx.beers
    ),
    Field("breweries", ListType(BreweryType),
      description = Some("Returns a list of all breweries"),
      arguments = OptionalCity :: Nil,
      resolve = c => c.ctx.breweries(c.arg(OptionalCity))
    ),
    Field("category", OptionType(CategoryType),
      description = Some("Returns a category"),
      arguments = Id :: Nil,
      resolve = c => c.ctx.category(c.arg(Id))
    ),
    Field("style", OptionType(StyleType),
      description = Some("Returns a style"),
      arguments = Id :: Nil,
      resolve = c => c.ctx.style(c.arg(Id))
    ),
    Field("brewery", OptionType(BreweryType),
      description = Some("Returns a brewery"),
      arguments = Id :: Nil,
      resolve = c => c.ctx.brewery(c.arg(Id))
    ),
    Field("beer", OptionType(BeerType),
      description = Some("Returns a beer"),
      arguments = Id :: Nil,
      resolve = c => c.ctx.beer(c.arg(Id))
    )
  ))

  val schema = Schema(QueryType)
}
