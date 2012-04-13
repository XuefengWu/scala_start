# --- Sample dataset

# --- !Ups
insert into testaker (id,name) values (  1,'Jason');

insert into theme (id,name) values (  1,'PMP');

insert into exam (id,name,testaker_id,theme_id) values (1,"for excise",1,1);

insert into tag (id,name,note,theme_id) values (  1,'2012','版本号',1);

insert into qtag (id,question_id,tag_id) values (  1,1,1);

insert into node (id,lastUpdateAt) values (  1,'2012-04-10 08:00:00');
insert into node (id,lastUpdateAt) values (  2,'2012-04-10 08:00:00');
insert into node (id,lastUpdateAt) values (  3,'2012-04-10 08:00:00');
insert into node (id,lastUpdateAt) values (  4,'2012-04-10 08:00:00');
insert into node (id,lastUpdateAt) values (  5,'2012-04-10 08:00:00');

insert into question (id,node_id,theme_id,description,note) values (  1,1,1,'你最喜欢什么水果','单选题');

insert into examQuestion (id,exam_id,question_id) values (1,1,1);

insert into choice (id,node_id,question_id,title,correct) values (  1,2,1,'苹果',0);
insert into choice (id,node_id,question_id,title,correct) values (  2,3,1,'香蕉',0);
insert into choice (id,node_id,question_id,title,correct) values (  3,4,1,'橘子',1);
insert into choice (id,node_id,question_id,title,correct) values (  4,5,1,'水蜜桃',0);

insert into node (id,lastUpdateAt) values (  6,'2012-04-10 08:00:00');
insert into node (id,lastUpdateAt) values (  7,'2012-04-10 08:00:00');
insert into node (id,lastUpdateAt) values (  8,'2012-04-10 08:00:00');
insert into node (id,lastUpdateAt) values (  9,'2012-04-10 08:00:00');
insert into node (id,lastUpdateAt) values (  10,'2012-04-10 08:00:00');

insert into question (id,node_id,theme_id,description,note) values (  2,6,1,'你最喜欢什么宠物','主观题');

insert into examQuestion (id,exam_id,question_id) values (2,1,2);

insert into choice (id,node_id,question_id,title,correct) values (  5,7,2,'狗',0);
insert into choice (id,node_id,question_id,title,correct) values (  6,8,2,'猫',0);
insert into choice (id,node_id,question_id,title,correct) values (  7,9,2,'兔子',1);
insert into choice (id,node_id,question_id,title,note,correct) values (  8,10,2,'狮子','重口味',0);


insert into node (id,lastUpdateAt) values (  11,'2012-04-10 08:00:00');
insert into comment (id,node_id,replyTo_id,context) values (1,11,1,"好问题!");

insert into node (id,lastUpdateAt) values (  12,'2012-04-10 08:00:00');
insert into comment (id,node_id,replyTo_id,context) values (2,12,1,"有点不明白");

insert into node (id,lastUpdateAt) values (  13,'2012-04-10 08:00:00');
insert into comment (id,node_id,replyTo_id,context) values (3,13,6,"有些古怪的问题");

insert into node (id,lastUpdateAt) values (  14,'2012-04-10 08:00:00');
insert into comment (id,node_id,replyTo_id,context) values (4,14,6,"这个问题有意思");

# --- !Downs
delete from comment;
delete from examQuestion;
delete from choice;
delete from qTag;
delete from question;
delete from tag;
delete from exam;
delete from theme;
delete from node;
delete from testaker;
