drop table tagdetail
drop table tag
drop table article
drop table user


create table user (
	uid integer identity primary key,
	name varchar(30) not null,
	password varchar(30) not null
)
create table tag (
	tid integer identity primary key,
	tagname varchar(30) not null unique
)
create table article (
	aid integer identity primary key,
	title varchar(50) not null,
	content varchar(200) not null,
	ref integer,
	uid integer not null,
	date timestamp default now,
	visible bit default 1
)
alter table article add foreign key (uid) references user(uid)
alter table article add foreign key (ref) references article(aid)
create table tagdetail (
	tdid integer identity primary key,
	aid integer not null,
	tid integer not null,
)
alter table tagdetail add foreign key (aid) references article(aid)
alter table tagdetail add foreign key (tid) references tag(tid)

insert into user (name,password) values ('0','0'),('1','1'),('2','2'),('3','3'),('4','4'),('5','5')
insert into tag (tagname) values ('a'),('b'),('c'),('d'),('e')
insert into article (title,content,ref,uid) values ('root','',null,0)


