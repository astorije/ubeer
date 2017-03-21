import sangria.macros.derive._
import sangria.schema._

object ProjectSchema {
  val Id = Argument("id", IntType)
  val OptionalCity = Argument("city", OptionInputType(StringType))

  val CategoryType: ObjectType[Repository, Category] = deriveObjectType(
    ObjectTypeDescription("A category"),
    AddFields(
      Field("styles", ListType(StyleType),
        resolve = c => c.value.styles(c.ctx.styles)
      )
    )
  )

  val StyleType: ObjectType[Repository, Style] = deriveObjectType(
    ObjectTypeDescription("A style"),
    ExcludeFields("category_id"),
    AddFields(
      Field("beers", ListType(BeerType),
        resolve = c => c.value.beers(c.ctx.beers)
      ),
      Field("category", CategoryType,
        resolve = c => c.value.category(c.ctx.categories)
      )
    )
  )

  val BreweryType: ObjectType[Repository, Brewery] = deriveObjectType(
    ObjectTypeDescription("A brewery"),
    AddFields(
      Field("beers", ListType(BeerType),
        resolve = c => c.value.beers(c.ctx.beers)
      )
    )
  )

  // Type must be specified because of recursion: beer > brewery > beers > ...
  val BeerType: ObjectType[Repository, Beer] = deriveObjectType(
    ObjectTypeDescription("A beer"),
    ExcludeFields("brewery_id", "style_id"),
    AddFields(
      Field("style", StyleType,
        resolve = c => c.value.style(c.ctx.styles)
      ),
      Field("brewery", BreweryType,
        resolve = c => c.value.brewery(c.ctx.breweries(None))
      )
    )
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
