#Tag schema

# --- !Ups

CREATE SEQUENCE tag_id_seq;
CREATE TABLE tag (
    id bigint NOT NULL DEFAULT nextval('tag_id_seq'),
    name varchar(255) NOT NULL,
	theme_id bigint
    ,constraint pk_tag primary key (id)
);


# --- !Downs
DROP TABLE tag;
DROP SEQUENCE tag_id_seq;
    