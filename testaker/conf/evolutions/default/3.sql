#Comment schema

# --- !Ups

CREATE SEQUENCE comment_id_seq;
CREATE TABLE comment (
    id bigint NOT NULL DEFAULT nextval('comment_id_seq'),
    node_id bigint,
	replyto_id bigint
    ,constraint pk_comment primary key (id)
);


# --- !Downs
DROP TABLE comment;
DROP SEQUENCE comment_id_seq;
    