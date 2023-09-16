//> using scala "3.3.0"
//> using jvm "temurin:17"
//> using lib "net.ruippeixotog::scala-scraper::3.1.0"
//> using lib "io.circe::circe-generic::0.14.6"

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Document

import io.circe.generic.auto.deriveEncoder
import io.circe.syntax._

object FideRatingsScraper {
  def main(args: Array[String]): Unit = {
    val fideId = args.headOption
    if (fideId.isEmpty) {
      error("Missing FIDE ID argument!")
    }

    val page = getRatingsPage(fideId.get)
    val ratings = getRatings(page)

    if (ratings.isEmpty) error(s"No information found for player with FIDE ID ${fideId.get}!")
    else println(ratings.asJson)
  }

  def fideRatingsUrl(fideId: String): String = s"https://ratings.fide.com/profile/$fideId/chart"

  def getRatingsPage(fideId: String): Document = JsoupBrowser().get(fideRatingsUrl(fideId: String))

  case class Ratings(
      standard: String,
      rapid: String,
      blitz: String,
      date: String
  )
  object Ratings {
    def empty = Ratings("", "", "", "")
  }

  def getRatings(page: Document): List[Ratings] =
    (page >> elementList(".profile-table_chart-table tbody tr"))
      .map(tableRow => (tableRow >> texts("td")).toList)
      .map {
        case List(date, standard, _, rapid, _, blitz, _) => Ratings(standard, rapid, blitz, date)
        case _ => Ratings.empty
      }

  def error(message: String) = {
    System.err.println(message)
    sys.exit(1)
  }
}
