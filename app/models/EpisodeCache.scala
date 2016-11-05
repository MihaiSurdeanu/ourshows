package models

import scala.collection.mutable

/**
  *
  * User: mihais
  * Date: 11/4/16
  */
object EpisodeCache {
  private val episodeCache = new mutable.HashMap[String, List[Episode]]()
  private val lastCrawlInMinutes = new mutable.HashMap[String, Long]()
  val MAX_CACHE_TIME_IN_MINUTES = 60 // the max number of minutes to cache crawled episodes

  def validCache(showName:String):Boolean = {
    this.synchronized {
      val crtTime = System.currentTimeMillis() / 60000
      if (!lastCrawlInMinutes.contains(showName) ||
        crtTime - lastCrawlInMinutes.get(showName).get > MAX_CACHE_TIME_IN_MINUTES) {
        false
      } else {
        true
      }
    }
  }

  def add(showName:String, episodes:List[Episode], time:Long) {
    this.synchronized {
      episodeCache += showName -> episodes
      lastCrawlInMinutes += showName -> time
    }
  }

  def episodes(showName:String):Option[List[Episode]] = {
    this.synchronized {
      episodeCache.get(showName)
    }
  }

}
