#Tag schema

# --- !Ups

CREATE TABLE tag (
    id bigint NOT NULL auto_increment,
    name varchar(255) NOT NULL,
	theme_id bigint
    ,constraint pk_tag primary key (id)
);


# --- !Downs
DROP TABLE tag;
    