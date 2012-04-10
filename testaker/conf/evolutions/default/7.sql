#Qtag schema

# --- !Ups

CREATE SEQUENCE qtag_id_seq;
CREATE TABLE qtag (
    id bigint NOT NULL DEFAULT nextval('qtag_id_seq'),
    question_id bigint,
	tag_id bigint
    ,constraint pk_qtag primary key (id)
);


# --- !Downs
DROP TABLE qtag;
DROP SEQUENCE qtag_id_seq;
    