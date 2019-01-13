package models


case class UserEntity(username: String,
                      password: String,
                      email: Option[String],
                      nickname: Option[String])
