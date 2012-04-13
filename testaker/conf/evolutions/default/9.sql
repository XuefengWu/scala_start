#Answer schema

# --- !Ups

CREATE TABLE answer (
    id bigint NOT NULL auto_increment,
    node_id bigint,
	question_id bigint,
	choice_id bigint,
	exam_id bigint,
    constraint pk_answer primary key (id)
);


# --- !Downs
DROP TABLE answer;
    