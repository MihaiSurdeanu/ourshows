package models

import java.net.URL
import java.util.regex.Pattern

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Parses shows' HTML pages to extract URLs of specific episodes
  * User: mihais
  * Date: 10/28/16
  */
object EpisodeParser {
  def parseEpisodes(url:URL, html:String):List[Episode] = {
    if(url.getHost.contains("abc.go.com")) {
      parseABCEpisodes(url, html)
    } else if(url.getHost.contains("cbs.com")) {
      // TODO
      List.empty
    } else if(url.getHost.contains("fox.com")) {
      // TODO
      List.empty
    } else {
      throw new RuntimeException(s"ERROR: Unknown domain for URL: $url!")
    }
  }

  def parseABCEpisodes(url:URL, html:String):List[Episode] = {
    val episodes = new ListBuffer[Episode]
    val matcher = ABC_EPISODE.matcher(html)
    //println(s"Trying to match: ${ABC_EPISODE.toString}")
    //println(html)
    var count = 0
    while(matcher.find() && count < MAX_EPISODES) {
      val text = matcher.group()
      val episodeUrl = url.getProtocol + "://" + url.getHost + matcher.group(1)
      val seasonNumber = matcher.group(2).toInt
      val episodeNumber = matcher.group(3).toInt
      val episodeTitle = matcher.group(4)
      //println(s"Found URL [$url] and title [$episode] in text [$text]")
      episodes += new Episode(episodeTitle, episodeUrl, seasonNumber, episodeNumber)
      count += 1
    }
    //println(s"Found $count episodes.")
    episodes.toList.sortBy(0 - _.number)
  }

  def main(args:Array[String]): Unit = {
    val html = Source.fromFile(args(0)).mkString.replaceAll("""\n""", " ")
    parseABCEpisodes(new URL("http://abc.go.com"), html)
  }

  val MAX_EPISODES = 5 // max number of episodes to show per show

  // 1: URL, 2: season number, 3: episode number, 4: title
  val ABC_EPISODE = Pattern.compile("""<a\s+href="([^"]+)"[^>]+>\s*<span\s+class="season-number\s*light">S(\d+)\s*</span>\s+<span\s+class="episode-number">E(\d+)\s+</span>([^><]+)</a>""", Pattern.CASE_INSENSITIVE)
}









