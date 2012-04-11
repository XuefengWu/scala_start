#Theme schema

# --- !Ups

CREATE TABLE theme (
    id bigint NOT NULL auto_increment,
    name varchar(255) NOT NULL
    ,constraint pk_theme primary key (id)
);


# --- !Downs
DROP TABLE theme;
    