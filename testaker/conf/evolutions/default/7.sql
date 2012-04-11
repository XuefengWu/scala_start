#Qtag schema

# --- !Ups

CREATE TABLE qtag (
    id bigint NOT NULL auto_increment,
    question_id bigint,
	tag_id bigint
    ,constraint pk_qtag primary key (id)
);


# --- !Downs
DROP TABLE qtag;
    