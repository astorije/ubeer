import org.scalatest.{AsyncWordSpec, Matchers}
import sangria.ast.Document
import sangria.execution.Executor
import spray.json._
import sangria.macros._
import sangria.marshalling.sprayJson._

class BeerSpec extends AsyncWordSpec with Matchers {
  "Project GraphQL Schema" should {
    "provide information about breweries, styles and categories" in {
      val query =
        graphql"""
        {
          brewery(id: 3) {
            name
            country

            beers {
              name
              style {
                name
                category {
                  name
                }
              }
            }
          }
        }
        """

      executeQuery(query) map (_ should be (
        """
        {
          "data": {
            "brewery": {
              "name": "3 Fonteinen Brouwerij Ambachtelijke Geuzestekerij",
              "country": "Belgium",
              "beers": [
                {
                  "name": "Oude Geuze",
                  "style": {
                    "name": "Belgian-Style Fruit Lambic",
                    "category": {
                      "name": "Belgian and French Ale"
                    }
                  }
                },
                {
                  "name": "Drie Fonteinen Kriek",
                  "style": {
                    "name": "Belgian-Style Fruit Lambic",
                    "category": {
                      "name": "Belgian and French Ale"
                    }
                  }
                }
              ]
            }
          }
        }
        """.parseJson))
    }
  }

  def executeQuery(query: Document) = {
    val repository = new Repository

    Executor.execute(ProjectSchema.schema, query, repository,
      deferredResolver = repository.deferredResolver)
  }

}
