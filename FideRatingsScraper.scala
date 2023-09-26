//> using scala "3.3.0"
//> using jvm "temurin:17"
//> using lib "net.ruippeixotog::scala-scraper::3.1.0"
//> using lib "io.circe::circe-generic::0.14.6"
//> using lib "com.monovore::decline::2.4.1"

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Document

import io.circe.generic.auto.deriveEncoder
import io.circe.syntax._

import com.monovore.decline.CommandApp
import com.monovore.decline.Opts

import FideRatingsScraper._

object FideRatingsScraper {
  def fideRatingsUrl(fideId: Long): String = s"https://ratings.fide.com/profile/$fideId/chart"

  def getRatingsPage(fideId: Long): Document = JsoupBrowser().get(fideRatingsUrl(fideId))

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
        case _                                           => Ratings.empty
      }

  def error(message: String) = {
    System.err.println(message)
    sys.exit(1)
  }
}

object FideRatingScraperCLI
    extends CommandApp(
      name = "fide-ratings-scraper",
      header = "Fetch chess ratings as JSON from the FIDE Ratings Website.",
      main = {
        val fideIdArg = Opts.argument[Long]("fide-id")

        fideIdArg.map { fideId =>
          val page = getRatingsPage(fideId)
          val ratings = getRatings(page)

          if (ratings.isEmpty) error(s"No information found for player with FIDE ID ${fideId}!")
          else println(ratings.asJson)
        }
      }
    )
