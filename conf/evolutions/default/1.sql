# --- !Ups

DROP TYPE IF EXISTS "PostType";
DROP TYPE IF EXISTS "PostStatus";
CREATE TYPE "PostType" AS ENUM ('Post', 'Page');
CREATE TYPE "PostStatus" AS ENUM ('Published', 'Draft');

DROP TABLE IF EXISTS "OPTIONS";
CREATE TABLE "OPTIONS" (
  "NAME" TEXT NOT NULL PRIMARY KEY,
  "VALUE" hstore NOT NULL
);

INSERT INTO "OPTIONS" ("NAME", "VALUE") VALUES ('blog_name', '"en"=>"My Blog", "zh-Hans"=>"我的博客"');
INSERT INTO "OPTIONS" ("NAME", "VALUE") VALUES ('blog_description', '"en"=>"A Blog Built with Virblog", "zh-Hans"=>"使用Virblog构建的博客"');
INSERT INTO "OPTIONS" ("NAME", "VALUE") VALUES ('locales', '"en"=>"English", "zh-Hans"=>"简体中文"');
INSERT INTO "OPTIONS" ("NAME", "VALUE") VALUES ('datetime_format', '"en"=>"MMMM d, yyyy", "zh-Hans"=>"yyyy年M月d日"');
INSERT INTO "OPTIONS" ("NAME", "VALUE") VALUES ('default_locale', '"value"=>"en"');
INSERT INTO "OPTIONS" ("NAME", "VALUE") VALUES ('page_size', '"value"=>"5"');
INSERT INTO "OPTIONS" ("NAME", "VALUE") VALUES ('disqus_short_name', '"value"=>""');
INSERT INTO "OPTIONS" ("NAME", "VALUE") VALUES ('ga_id', '"value"=>""');

DROP TABLE IF EXISTS "POST_TAGS";
CREATE TABLE "POST_TAGS" (
  "SLUG" TEXT NOT NULL PRIMARY KEY,
  "NAME" hstore NOT NULL
);

DROP TABLE IF EXISTS "POSTS";
CREATE TABLE "POSTS" (
  "ID" SERIAL PRIMARY KEY,
  "SLUG" TEXT NOT NULL,
  "TIME" TIMESTAMP NOT NULL,
  "TITLE" hstore NOT NULL,
  "SUBTITLE" hstore NOT NULL ,
  "EXCERPT" hstore NOT NULL ,
  "CONTENT" hstore NOT NULL,
  "HEADER_IMAGE" VARCHAR(256) NOT NULL,
  "POST_STATUS" "PostStatus" NOT NULL,
  "POST_TYPE" "PostType" NOT NULL,
  "TAGS" TEXT ARRAY NOT NULL
);

CREATE UNIQUE INDEX slug_index ON "POSTS" ("SLUG");
CREATE INDEX time_index ON "POSTS" ("TIME" DESC);

DROP TABLE IF EXISTS "USERS";
CREATE TABLE "USERS" (
  "USERNAME" TEXT PRIMARY KEY,
  "PASSWORD" TEXT NOT NULL,
  "EMAIL" TEXT,
  "NICKNAME" TEXT
);
INSERT INTO "USERS" VALUES ('admin', '$2a$10$ArtwaN4ox9StfbpyngEmAur3p3nQzbQ9w/zw1zRvBplLIB/lZkhSy', 'admin@admin.com', 'Admin');

DROP TABLE IF EXISTS "BLOGROLLS";
CREATE TABLE "BLOGROLLS" (
  "NAME" TEXT,
  "LINK" TEXT
);

# --- !Downs
DROP TABLE "BLOGROLLS";
DROP TABLE "USERS";
DROP TABLE "POSTS";
DROP TABLE "POST_TAGS";
DROP TABLE "OPTIONS";
DROP TYPE "PostType";
DROP TYPE "PostStatus";
