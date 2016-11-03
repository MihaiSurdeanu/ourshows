# --- Created by Slick DDL
# To stop Slick DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table "SHOW" ("name" VARCHAR(254) PRIMARY KEY NOT NULL,"episodesUrl" VARCHAR(254) NOT NULL);

# --- !Downs

drop table "SHOW";

