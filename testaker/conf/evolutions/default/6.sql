#Question schema

# --- !Ups

CREATE SEQUENCE question_id_seq;
CREATE TABLE question (
    id bigint NOT NULL DEFAULT nextval('question_id_seq'),
    node_id bigint,
	theme_id bigint,
	desc varchar(255)
    ,constraint pk_question primary key (id)
);


# --- !Downs
DROP TABLE question;
DROP SEQUENCE question_id_seq;
    