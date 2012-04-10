#Answer schema

# --- !Ups

CREATE SEQUENCE answer_id_seq;
CREATE TABLE answer (
    id bigint NOT NULL DEFAULT nextval('answer_id_seq'),
    node_id bigint,
	question_id bigint,
	choice_id bigint,
	testaker_id bigint
    ,constraint pk_answer primary key (id)
);


# --- !Downs
DROP TABLE answer;
DROP SEQUENCE answer_id_seq;
    