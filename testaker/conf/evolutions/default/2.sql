#Node schema

# --- !Ups

CREATE TABLE node (
    id bigint NOT NULL auto_increment,
    createdAt TIMESTAMP NOT NULL  ,
	lastUpdateAt TIMESTAMP NOT NULL,
    constraint pk_node primary key (id)
);


# --- !Downs
DROP TABLE node;
    