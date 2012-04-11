#Comment schema

# --- !Ups

CREATE TABLE comment (
    id bigint NOT NULL auto_increment,
    node_id bigint,
	replyto_id bigint,
	context varchar(255)
    ,constraint pk_comment primary key (id)
);


# --- !Downs
DROP TABLE comment;
    