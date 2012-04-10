#Choice schema

# --- !Ups

CREATE SEQUENCE choice_id_seq;
CREATE TABLE choice (
    id bigint NOT NULL DEFAULT nextval('choice_id_seq'),
    node_id bigint,
	question_id bigint,
	correct BIT
    ,constraint pk_choice primary key (id)
);


# --- !Downs
DROP TABLE choice;
DROP SEQUENCE choice_id_seq;
    