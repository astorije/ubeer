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

  val route: Route =
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
          case Success(queryAst) =>
            val reposiroty = new Repository

            complete(
              Executor.execute(
                ProjectSchema.schema,
                queryAst,
                reposiroty,
                variables = vars,
                operationName = operation,
                deferredResolver = reposiroty.deferredResolver
              ).map(OK -> _).recover {
                case error: QueryAnalysisError => BadRequest -> error.resolveError
                case error: ErrorWithResolver => InternalServerError -> error.resolveError
              })

          case Failure(error) =>
            complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
        }
      }}
    } ~
    get {
      getFromResource("graphiql.html")
    }

  // Prints a human-readable representation of the schema upon server startup
  println(SchemaRenderer.renderSchema(ProjectSchema.schema))

  Http().bindAndHandle(route, "0.0.0.0", 8080)
}
