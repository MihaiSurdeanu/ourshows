package models

import java.net.URL

import play.api.db.slick.Config.driver.simple._

case class Show(name: String, episodesUrl: String) {

  def episodes:List[Episode] = {
    val crtTimeInMinutes = System.currentTimeMillis() / 60000

    // cache expired; crawl from scratch
    if(! EpisodeCache.validCache(name)) {
      val html = scala.io.Source.fromURL(episodesUrl.toString).mkString
      //val html = scala.io.Source.fromFile("test/modern_family.html").mkString
      val es = EpisodeParser.parseEpisodes(new URL(episodesUrl.toString), html)
      EpisodeCache.add(name, es, crtTimeInMinutes)
      es
    } else {
      println(s"Using cache for show $name")
      EpisodeCache.episodes(name).get
    }

  }
}

/* Table mapping
 */
class ShowsTable(tag: Tag) extends Table[Show](tag, "SHOW") {

  def name = column[String]("name", O.PrimaryKey)
  def episodesUrl = column[String]("episodesUrl", O.NotNull)

  def * = (name, episodesUrl) <> (Show.tupled, Show.unapply _)
}
