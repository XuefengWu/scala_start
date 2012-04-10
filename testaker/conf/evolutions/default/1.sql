#Testaker schema

# --- !Ups

CREATE SEQUENCE testaker_id_seq;
CREATE TABLE testaker (
    id bigint NOT NULL DEFAULT nextval('testaker_id_seq'),
    name varchar(255) NOT NULL
    ,constraint pk_testaker primary key (id)
);


# --- !Downs
DROP TABLE testaker;
DROP SEQUENCE testaker_id_seq;
    