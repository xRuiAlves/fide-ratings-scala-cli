//> using scala "3.3.0"
//> using jvm "temurin:17"
//> using lib "com.typesafe.akka::akka-stream::2.8.4"
//> using lib "com.typesafe.akka::akka-http::10.5.2"
//> using lib "net.ruippeixotog::scala-scraper::3.1.0"

import akka.actor.ActorSystem
import akka.http.scaladsl._
import akka.http.scaladsl.model._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model._

object FideRatingsScraper {
  final val FideId = "1962000"
  final val FideRatingsUrl = s"https://ratings.fide.com/profile/$FideId/chart"

  def main(args: Array[String]): Unit = {
    val page = getRatingsPage()
    val ratings = getRatings(page)

    println(s"Latest ratings: ${ratings.head}")
  }

  def getRatingsPage(): Document = JsoupBrowser().get(FideRatingsUrl)

  case class Ratings(
      standard: String,
      rapid: String,
      blitz: String,
      date: String
  )

  def getRatings(page: Document): List[Ratings] =
    (page >> elementList(".profile-table_chart-table tbody tr"))
      .map(tableRow => (tableRow >> texts("td")).toList)
      .map { case List(date, standard, _, rapid, _, blitz, _) =>
        Ratings(standard, rapid, blitz, date)
      }
}
