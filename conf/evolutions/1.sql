CREATE TYPE "PostType" AS ENUM ('Post', 'Page');
CREATE TYPE "PostStatus" AS ENUM ('Published', 'Draft');
CREATE EXTENSION hstore;

CREATE TABLE "OPTIONS" (
  "NAME" VARCHAR(64) NOT NULL PRIMARY KEY,
  "VALUE" hstore NOT NULL
);

INSERT INTO "OPTIONS" ("NAME", "VALUE") VALUES ('blog_name', '"en"=>"My Blog", "zh-CN"=>"我的博客"');
INSERT INTO "OPTIONS" ("NAME", "VALUE") VALUES ('blog_description', '"en"=>"A Blog Built with Virblog", "zh-CN"=>"使用Virblog构建的博客"');
INSERT INTO "OPTIONS" ("NAME", "VALUE") VALUES ('locales', '"en"=>"English", "zh-CN"=>"简体中文"');
INSERT INTO "OPTIONS" ("NAME", "VALUE") VALUES ('datetime_format', '"en"=>"MMMM d, yyyy", "zh-CN"=>"yyyy年M月d日"');
INSERT INTO "OPTIONS" ("NAME", "VALUE") VALUES ('default_locale', '"value"=>"en"');
INSERT INTO "OPTIONS" ("NAME", "VALUE") VALUES ('page_size', '"value"=>"10"');

CREATE TABLE "POST_TAGS" (
  "SLUG" VARCHAR(64) NOT NULL PRIMARY KEY,
  "NAME" hstore NOT NULL
);

CREATE TABLE "POSTS" (
  "SLUG" VARCHAR(64) PRIMARY KEY,
  "TIME" timestamp NOT NULL,
  "TITLE" hstore NOT NULL,
  "SUBTITLE" hstore NOT NULL ,
  "EXCERPT" hstore NOT NULL ,
  "CONTENT" hstore NOT NULL,
  "HEADER_IMAGE" VARCHAR(256),
  "POST_STATUS" "PostStatus" NOT NULL,
  "POST_TYPE" "PostType" NOT NULL,
  "TAGS" TEXT ARRAY NOT NULL
);

CREATE INDEX time_index ON "POSTS" ("TIME" DESC);