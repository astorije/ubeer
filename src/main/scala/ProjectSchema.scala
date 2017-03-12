import sangria.macros.derive._
import sangria.schema._

object ProjectSchema {
  val Id = Argument("id", IntType)

  // Type must be specified because of recursion: beer > brewery > beers > ...
  val BeerType: ObjectType[Repository, Beer] = deriveObjectType[Repository, Beer](
    ObjectTypeDescription("A beer"),
    ExcludeFields("brewery_id"),
    AddFields(
      Field("brewery", BreweryType,
        resolve = c => c.value.brewery(c.ctx.breweries)
      )
    )
  )

  val BreweryType = deriveObjectType[Repository, Brewery](
    ObjectTypeDescription("A brewery"),
    AddFields(
      Field("beers", ListType(BeerType),
        resolve = c => c.value.beers(c.ctx.beers)
      )
    )
  )

  val QueryType = ObjectType("Query", fields[Repository, Unit](
    Field("beers", ListType(BeerType),
      description = Some("Returns a list of all beers"),
      resolve = _.ctx.beers
    ),
    Field("breweries", ListType(BreweryType),
      description = Some("Returns a list of all breweries"),
      resolve = _.ctx.breweries
    )
  ))

  val schema = Schema(QueryType)
}
