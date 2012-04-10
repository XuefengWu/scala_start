# --- Sample dataset

# --- !Ups
insert into testaker (id,name) values (  1,'Jason');

insert into node (id,title) values (  1,'你最喜欢什么水果');
insert into node (id,title) values (  2,'苹果');
insert into node (id,title) values (  3,'香蕉');
insert into node (id,title) values (  4,'橘子');
insert into node (id,title) values (  5,'水蜜桃');

insert into theme (id,name) values (  1,'PMP');

insert into tag (id,name,theme_id) values (  1,'2012',1);

insert into question (id,node_id,theme_id,desc) values (  1,1,1,'choose one');

insert into qtag (id,question_id,tag_id) values (  1,1,1);

insert into choice (id,node_id,question_id,correct) values (  1,2,1,0);
insert into choice (id,node_id,question_id,correct) values (  2,3,1,0);
insert into choice (id,node_id,question_id,correct) values (  3,4,1,1);
insert into choice (id,node_id,question_id,correct) values (  4,5,1,0);

# --- !Downs

delete from choice;
delete from qtag;
delete from question;
delete from tag;
delete from theme;
delete from node;
delete from testaker;
