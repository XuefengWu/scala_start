#Testaker schema

# --- !Ups

CREATE TABLE testaker (
    id bigint NOT NULL auto_increment,
    name varchar(255) NOT NULL
    ,constraint pk_testaker primary key (id)
);


# --- !Downs
DROP TABLE testaker;
    