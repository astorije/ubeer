import spray.json._
import DefaultJsonProtocol._
import sangria.execution.deferred.HasId
import sangria.schema._
import sangria.macros.derive._

case class Category(
  id: Int,
  name: String)

object Category {
  implicit val categoryHasId = HasId[Category, Int](_.id)
  implicit val categoryFormat = jsonFormat2(Category.apply)

  implicit val graphqlType: ObjectType[Repository, Category] = deriveObjectType(
    ObjectTypeDescription("A category"),
    AddFields(Field("styles", ListType(Style.graphqlType),
      resolve = c => c.ctx.styleFetcher.deferRelSeq(c.ctx.styleByCategory, c.value.id))))
}

case class Style(
  id: Int,
  category_id: Int,
  name: String)

object Style {
  implicit val styleHasId = HasId[Style, Int](_.id)
  implicit val styleFormat = jsonFormat3(Style.apply)

  implicit val graphqlType: ObjectType[Repository, Style] = deriveObjectType(
    ObjectTypeDescription("A style"),
    ReplaceField("category_id", Field("category", Category.graphqlType,
      resolve = c => c.ctx.categoryFetcher.defer(c.value.category_id))),
    AddFields(Field("beers", ListType(Beer.graphqlType),
      resolve = c => c.ctx.beerFetcher.deferRelSeq(c.ctx.beerByStyle, c.value.id))))
}

case class Brewery(
  id: Int,
  name: String,
  address: String,
  city: String,
  state: String,
  code: String,
  country: String,
  phone: String,
  website: String,
  description: String)

object Brewery {
  implicit val breweryHasId = HasId[Brewery, Int](_.id)
  implicit val breweryFormat = jsonFormat10(Brewery.apply)

  implicit val graphqlType: ObjectType[Repository, Brewery] = deriveObjectType(
    ObjectTypeDescription("A brewery"),
    AddFields(Field("beers", ListType(Beer.graphqlType),
      resolve = c => c.ctx.beerFetcher.deferRelSeq(c.ctx.beerByBrewery, c.value.id))))
}

case class Beer(
  id: Int,
  brewery_id: Int,
  style_id: Int,
  name: String,
  abv: Double,
  description: String
)

object Beer {
  implicit val beerHasId = HasId[Beer, Int](_.id)
  implicit val beerFormat = jsonFormat6(Beer.apply)

  implicit val graphqlType: ObjectType[Repository, Beer] = deriveObjectType(
    ObjectTypeDescription("A beer"),
    ReplaceField("brewery_id", Field("brewery", Brewery.graphqlType,
      resolve = c => c.ctx.breweryFetcher.defer(c.value.brewery_id))),
    ReplaceField("style_id", Field("style", Style.graphqlType,
      resolve = c => c.ctx.styleFetcher.defer(c.value.style_id))))
}
