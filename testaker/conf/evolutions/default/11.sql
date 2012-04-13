#Examquestion schema

# --- !Ups

CREATE TABLE examquestion (
    id bigint NOT NULL auto_increment,
    exam_id bigint,
	question_id bigint
    ,constraint pk_examquestion primary key (id)
);


# --- !Downs
DROP TABLE examquestion;
    