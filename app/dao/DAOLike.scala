package dao


import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}

abstract class DAOLike(dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfig[PGDriver] {

  protected val dbConfig = dbConfigProvider.get[PGDriver]

}