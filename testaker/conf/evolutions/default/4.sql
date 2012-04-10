#Theme schema

# --- !Ups

CREATE SEQUENCE theme_id_seq;
CREATE TABLE theme (
    id bigint NOT NULL DEFAULT nextval('theme_id_seq'),
    name varchar(255) NOT NULL
    ,constraint pk_theme primary key (id)
);


# --- !Downs
DROP TABLE theme;
DROP SEQUENCE theme_id_seq;
    