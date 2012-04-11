#Choice schema

# --- !Ups

CREATE TABLE choice (
    id bigint NOT NULL auto_increment,
    node_id bigint,
	question_id bigint,
	title varchar(255) NOT NULL,
	correct BIT
    ,constraint pk_choice primary key (id)
);


# --- !Downs
DROP TABLE choice;
    