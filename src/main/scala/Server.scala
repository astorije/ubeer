import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import sangria.marshalling.sprayJson._
import sangria.parser.QueryParser
import sangria.renderer.SchemaRenderer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
import spray.json._

object Server extends App {
  implicit val system = ActorSystem("sangria-server")
  implicit val materializer = ActorMaterializer()
  val repository = new Repository

  val route: Route =
    pathSingleSlash {
      getFromResource("root.html")
    } ~
    pathPrefix("ubeer") {
      getFromDirectory("ubeer-client")
    } ~
    pathPrefix("graphiql") {
      getFromDirectory("graphiql")
    } ~
    (post & path("graphql")) {
      entity(as[JsValue]) { requestJson => {
        val JsObject(fields) = requestJson
        val JsString(query) = fields("query")
        val operation = fields.get("operationName") collect {
          case JsString(op) => op
        }

        val vars = fields.get("variables") match {
          case Some(obj: JsObject) => obj
          case _ => JsObject.empty
        }

        QueryParser.parse(query) match {
          // Query parsed successfully, time to execute it!
          case Success(queryAst) => complete(
            Executor.execute(
              ProjectSchema.schema,
              queryAst,
              repository,
              variables = vars,
              operationName = operation
            ).map(OK -> _)
            .recover {
              case error: QueryAnalysisError => BadRequest -> error.resolveError
              case error: ErrorWithResolver => InternalServerError -> error.resolveError
            }
          )

          // Cannot parse GraphQL query, return error
          case Failure(error) =>
            complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
        }
      }}
    }

  // Prints a human-readable representation of the schema upon server startup
  println(SchemaRenderer.renderSchema(ProjectSchema.schema))

  Http().bindAndHandle(route, "0.0.0.0", 8080)
}
