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
import net.ruippeixotog.scalascraper.model._
import net.ruippeixotog.scalascraper.browser.JsoupBrowser

object FideRatingsScraper {
  final val FideId = "1962000"
  final val FideRatingsUrl = s"https://ratings.fide.com/profile/$FideId"

  def main(args: Array[String]): Unit = {
    val page = getFideRatingsPage()
    val ratings = getRatings(page)

    println(ratings)
  }
//    val res = Http()
//      .singleRequest(request)
//      .flatMap(_.entity.toStrict(2.seconds))
//      .map(_.data.utf8String)
//      .foreach(println)

//  given system: ActorSystem = ActorSystem()

//  final lazy val request = HttpRequest(
//    method = HttpMethods.GET,
//    uri = "https://ratings.fide.com/profile/1962000"
//  )

  def getFideRatingsPage(): Document = JsoupBrowser().get(FideRatingsUrl)

  case class Ratings(standard: Int, rapid: Int, blitz: Int)

  def getRatings(page: Document): Ratings = (1 to 3)
    .map(i => page >> text(s"div.profile-top-rating-data:nth-child($i)"))
    .map(_.toString.split(" ")(1).toInt) match {
    case Seq(a, b, c) => Ratings(a, b, c)
  }
}
