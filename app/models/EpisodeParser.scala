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
      parseCBSEpisodes(url, html)
    } else if(url.getHost.contains("fox.com")) {
      parseFoxEpisodes(url, html)
    } else if(url.getHost.contains("nbc.com")) {
      parseNBCEpisodes(url, html)
    } else if(url.getHost.contains("cc.com")) {
      parseCCEpisodes(url, html)
    } else {
      throw new RuntimeException(s"ERROR: Unknown domain for URL: $url!")
    }
  }

  def parseABCEpisodes(url:URL, html:String):List[Episode] = {
    val episodes = new ListBuffer[Episode]
    val matcher = ABC_EPISODE.matcher(html)
    var count = 0
    while(matcher.find()) {
      val episodeUrl = url.getProtocol + "://" + url.getHost + matcher.group(1)
      val seasonNumber = matcher.group(2).toInt
      val episodeNumber = matcher.group(3).toInt
      val episodeTitle = matcher.group(4)
      episodes += new Episode(episodeTitle, episodeUrl, seasonNumber, episodeNumber)
      count += 1
    }
    sortAndTake(episodes)
  }

  def parseCBSEpisodes(url:URL, html:String):List[Episode] = {
    // TODO: this is tricky, the carousel with episodes is hidden
    val episodes = new ListBuffer[Episode]
    val matcher = CBS_EPISODE.matcher(html)
    var count = 0
    while(matcher.find()) {
      val episodeTitle = matcher.group(1)
      val episodeUrl = matcher.group(2)
      val episodeNumber = matcher.group(3).toInt
      val seasonNumber = matcher.group(4).toInt
      episodes += new Episode(episodeTitle, episodeUrl, seasonNumber, episodeNumber)
      count += 1
    }
    sortAndTake(episodes)
  }

  def parseFoxEpisodes(url:URL, html:String):List[Episode] = {
    // TODO
    List.empty
  }

  def parseNBCEpisodes(url:URL, html:String):List[Episode] = {
    // TODO
    List.empty
  }

  def parseCCEpisodes(url:URL, html:String):List[Episode] = {
    val episodes = new ListBuffer[Episode]
    val matcher = CC_EPISODE.matcher(html)
    var count = 0
    while(matcher.find()) {
      val episodeUrl = matcher.group(1)
      val episodeTitle = matcher.group(2)
      val urlMatcher = CC_URL.matcher(episodeUrl)
      if(urlMatcher.matches()) {
        val seasonNumber = urlMatcher.group(1).toInt
        val episodeNumber = urlMatcher.group(2).toInt
        episodes += new Episode(episodeTitle, episodeUrl, seasonNumber, episodeNumber)
        count += 1
      }
    }
    sortAndTake(episodes)
  }

  def sortAndTake(episodes:Seq[Episode]):List[Episode] = {
    episodes.toList.sortBy(x => 0 - (x.season * 100 + x.number)).take(MAX_EPISODES)
  }

  def main(args:Array[String]): Unit = {
    val html = Source.fromFile(args(0)).mkString.replaceAll("""\n""", " ")
    parseABCEpisodes(new URL("http://abc.go.com"), html)
  }

  val MAX_EPISODES = 5 // max number of episodes to show per show

  // ABC:
  // 1: URL, 2: season number, 3: episode number, 4: title
  val ABC_EPISODE = Pattern.compile("""<a\s+href="([^"]+)"[^>]+>\s*<span\s+class="season-number\s*light">S(\d+)\s*</span>\s+<span\s+class="episode-number">E(\d+)\s+</span>([^><]+)</a>""", Pattern.CASE_INSENSITIVE)

  // Comedy Central:
  // 1. URL, 2: title
  val CC_EPISODE = Pattern.compile("""<a\s+class="carouselseo"\s+href="([^"]+)">([^><]+)</a>""", Pattern.CASE_INSENSITIVE)
  // 1: season number, 2: episode number
  val CC_URL = Pattern.compile("""http://\w+.cc.com/full-episodes/s(\d+)e(\d+)-[a-z0-9\-]+""", Pattern.CASE_INSENSITIVE)

  // CBS (this only picks the last episode):
  // 1: title, 2: URL, 3: episode number, 4: season number
  val CBS_EPISODE = Pattern.compile("""\"name\":\"([^\"]+)\"\s*,\s*\"description\":\"[^\"]*\"\s*,\s*\"url\":\"([^\"]+)\"\s*,\s*\"image\":\"[^\"]+\"\s*,\s*\"episodeNumber\":\"(\d+)\"\s*,\s*\"partOfSeason\":{\"@type\":\"TVSeason\",\"seasonNumber\":\"(\d+)\"""", Pattern.CASE_INSENSITIVE)
}










