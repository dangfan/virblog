package models

import java.time._

import models.enums.{PostStatuses, PostTypes}


case class PostEntity(id: Option[Int],
                      slug: String,
                      time: LocalDateTime,
                      title: Map[String, String],
                      subtitle: Map[String, String],
                      excerpt: Map[String, String],
                      content: Map[String, String],
                      headerImage: String,
                      status: PostStatuses.PostStatus,
                      postType: PostTypes.PostType,
                      tags: List[String])
