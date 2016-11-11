package controllers

import models._
import play.api._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.mvc.BodyParsers._
import play.api.libs.json.Json
import play.api.libs.json.Json._

object Application extends Controller{

  //create an instance of the table
  val shows = TableQuery[ShowsTable] //see a way to architect your app in the computers-database-slick sample

  //JSON read/write macro
  implicit val showFormat = Json.format[Show]

  def index = DBAction { implicit rs =>
    Ok(views.html.index(shows.list.sortBy(_.name)))
  }

  val showForm = Form(
    mapping(
      "name" -> text(),
      "episodesUrl" -> text()
    )(Show.apply)(Show.unapply)
  )

  def insert = DBAction { implicit rs =>
    val show = showForm.bindFromRequest.get

    if(show.episodesUrl.startsWith("http") && EpisodeParser.knownDomain(show.episodesUrl))
      shows.insert(show)

    Redirect(routes.Application.edit)
  }

  def delete = DBAction { implicit rs =>
    val show = showForm.bindFromRequest.get

    val q = shows.filter(_.name === show.name)
    q.delete

    Redirect(routes.Application.edit)
  }

  def edit = DBAction { implicit rs =>
    Ok(views.html.edit(shows.list))
  }

  def jsonFindAll = DBAction { implicit rs =>
    Ok(toJson(shows.list))
  }

  def jsonInsert = DBAction(parse.json) { implicit rs =>
    rs.request.body.validate[Show].map { show =>
        shows.insert(show)
        Ok(toJson(show))
    }.getOrElse(BadRequest("invalid json"))    
  }
  
}
