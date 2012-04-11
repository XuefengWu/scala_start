# --- Sample dataset

# --- !Ups
insert into testaker (id,name) values (  1,'Jason');

insert into theme (id,name) values (  1,'PMP');

insert into tag (id,name,theme_id) values (  1,'2012',1);

insert into qtag (id,question_id,tag_id) values (  1,1,1);

insert into node (id) values (  1);
insert into node (id) values (  2);
insert into node (id) values (  3);
insert into node (id) values (  4);
insert into node (id) values (  5);

insert into question (id,node_id,theme_id,description) values (  1,1,1,'你最喜欢什么水果');

insert into choice (id,node_id,question_id,title,correct) values (  1,2,1,'苹果',0);
insert into choice (id,node_id,question_id,title,correct) values (  2,3,1,'香蕉',0);
insert into choice (id,node_id,question_id,title,correct) values (  3,4,1,'橘子',1);
insert into choice (id,node_id,question_id,title,correct) values (  4,5,1,'水蜜桃',0);

insert into node (id) values (  6);
insert into node (id) values (  7);
insert into node (id) values (  8);
insert into node (id) values (  9);
insert into node (id) values (  10);

insert into question (id,node_id,theme_id,description) values (  2,6,1,'你最喜欢什么宠物');

insert into choice (id,node_id,question_id,title,correct) values (  5,7,2,'狗',0);
insert into choice (id,node_id,question_id,title,correct) values (  6,8,2,'猫',0);
insert into choice (id,node_id,question_id,title,correct) values (  7,9,2,'兔子',1);
insert into choice (id,node_id,question_id,title,correct) values (  8,10,2,'狮子',0);

# --- !Downs

delete from choice;
delete from qtag;
delete from question;
delete from tag;
delete from theme;
delete from node;
delete from testaker;
