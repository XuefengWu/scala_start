#Examquestion schema

# --- !Ups

CREATE TABLE examQuestion (
    id bigint NOT NULL auto_increment,
    exam_id bigint,
	question_id bigint
    ,constraint pk_examQuestion primary key (id)
);


# --- !Downs
DROP TABLE examQuestion;
    