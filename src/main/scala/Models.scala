case class Category(
  id: Int,
  name: String
)

case class Style(
  id: Int,
  category_id: Int,
  name: String
)

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
  description: String
)

case class Beer(
  id: Int,
  brewery_id: Int,
  style_id: Int,
  name: String,
  abv: Double,
  description: String
) {
  def filteredDescription(charLimit: Option[Int]): String = charLimit match {
    case Some(n) if description.length > n - 1 => description.take(n - 1) + "…"
    case _ => description
  }
}
