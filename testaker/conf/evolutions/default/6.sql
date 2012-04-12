#Question schema

# --- !Ups

CREATE TABLE question (
    id bigint NOT NULL auto_increment,
    node_id bigint,
	theme_id bigint,
	description varchar(255),
	note TEXT,
    constraint pk_question primary key (id)
);


# --- !Downs
DROP TABLE question;
    