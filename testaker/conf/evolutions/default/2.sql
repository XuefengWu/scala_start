#Node schema

# --- !Ups

CREATE SEQUENCE node_id_seq;
CREATE TABLE node (
    id bigint NOT NULL DEFAULT nextval('node_id_seq'),
    title varchar(255) NOT NULL
    ,constraint pk_node primary key (id)
);


# --- !Downs
DROP TABLE node;
DROP SEQUENCE node_id_seq;
    