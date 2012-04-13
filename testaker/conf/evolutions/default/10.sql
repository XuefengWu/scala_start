#Exam schema

# --- !Ups

CREATE TABLE exam (
    id bigint NOT NULL auto_increment,
    name varchar(255) NOT NULL,
	testaker_id bigint,
	theme_id bigint,
    constraint pk_exam primary key (id)
);


# --- !Downs
DROP TABLE exam;
    