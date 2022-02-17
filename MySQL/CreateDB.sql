/*==============================================================*/
/* Created on:     23/12/2021 20:04:34                          */
/*==============================================================*/
CREATE DATABASE IF NOT EXISTS MSG_SERVICE;
USE MSG_SERVICE;

drop table if exists CONTACTS, MESSAGES_USER;

drop table if exists GROUPS1, MESSAGES_GROUP,GROUPS_USERS;

drop table if exists USERS;

/*==============================================================*/
/* Table: GROUPS_USERS                                               */
/*==============================================================*/
create table GROUPS_USERS
(
   ID_GROUP             int not null,
   ID_USER              int not null,
   IS_ACCEPTED          boolean not null,
   primary key (ID_GROUP, ID_USER)
);

/*==============================================================*/
/* Table: CONTACTS                                              */
/*==============================================================*/
create table CONTACTS
(
   ID_USER           	int not null,
   ID_CONTACT           int not null,
   primary key (ID_USER, ID_CONTACT)
);

/*==============================================================*/
/* Table: GROUPS1                                               */
/*==============================================================*/
create table GROUPS1
(
   ID_GROUP             int not null AUTO_INCREMENT,
   ID_ADMIN             int not null ,
   NAME_GROUP           varchar(50) not null,
   primary key (ID_GROUP),
   unique(NAME_GROUP,ID_ADMIN)
);

/*==============================================================*/
/* Table: MESSAGES_GROUP                                        */
/*==============================================================*/
create table MESSAGES_GROUP
(
   ID_MSG               int not null,
   BODY                 text not null,
   TIMESTAMP            timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   IS_FILE              bool not null,
   WAS_SEEN             bool not null,
   ID_SENDER            int not null ,
   ID_GROUP          	int not null ,
   primary key (ID_MSG,ID_SENDER,ID_GROUP)
);

/*==============================================================*/
/* Table: MESSAGES_USER                                         */
/*==============================================================*/
create table MESSAGES_USER
(
   ID_MSG               int not null,
   BODY                 text not null,
   TIMESTAMP            timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   IS_FILE              bool not null,
   WAS_SEEN             bool not null,
   ID_SENDER            int not null ,
   ID_RECEIVER          int not null ,
   primary key (ID_MSG,ID_SENDER,ID_RECEIVER)
);

/*==============================================================*/
/* Table: USERS                                                 */
/*==============================================================*/
create table USERS 
(
   ID_USER              int not null AUTO_INCREMENT,
   NAME_USER            varchar(100) not null ,
   USERNAME             varchar(50) not null ,
   PASSWORD             int not null,
   -- IS_ONLINE            bool not null,
   LAST_SEEN            timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   primary key (ID_USER),
   unique(NAME_USER,USERNAME)
   
) ;

alter table GROUPS_USERS add constraint FK_GROUPS_USERS foreign key (ID_USER)
      references USERS (ID_USER) on delete cascade on update restrict;

alter table GROUPS_USERS add constraint FK_GROUPS_USERS2 foreign key (ID_GROUP)
      references GROUPS1 (ID_GROUP) on delete cascade on update restrict;

alter table CONTACTS add constraint FK_ISUSER foreign key (ID_USER)
      references USERS (ID_USER) on delete restrict on update restrict;

alter table CONTACTS add constraint FK_ISCONTACT foreign key (ID_CONTACT)
      references USERS (ID_USER) on delete restrict on update restrict;

alter table GROUPS1 add constraint FK_MANAGES foreign key (ID_ADMIN)
      references USERS (ID_USER) on delete restrict on update restrict;

-- alter table MESSAGES_GROUP add constraint FK_INHERITANCEGROUP foreign key (ID_MSG)
--      references MESSAGES (ID_MSG) on delete restrict on update restrict;

alter table MESSAGES_GROUP add constraint FK_RECEIVE_GROUP foreign key (ID_GROUP,ID_SENDER)
      references GROUPS_USERS (ID_GROUP,ID_USER) on delete cascade on update restrict;

-- alter table MESSAGES_GROUP add constraint FK_SEND_GROUP foreign key (ID_SENDER)
--      references USERS (ID_USER) on delete restrict on update restrict;

-- alter table MESSAGES_USER add constraint FK_INHERITANCEUSER foreign key (ID_MSG)
--       references MESSAGES (ID_MSG) on delete restrict on update restrict;

alter table MESSAGES_USER add constraint FK_SEND_RECEIVE foreign key (ID_SENDER, ID_RECEIVER)
      references CONTACTS (ID_USER, ID_CONTACT) on delete cascade on update restrict;

DELIMITER //

 CREATE TRIGGER INC_USER_ID_MSG BEFORE INSERT ON MESSAGES_USER
	FOR EACH ROW BEGIN
		SET NEW.ID_MSG = (
			SELECT ifnull(MAX(ID_MSG),0) + 1
			FROM MESSAGES_USER
			WHERE (MESSAGES_USER.ID_SENDER = NEW.ID_SENDER OR
				MESSAGES_USER.ID_SENDER = NEW.ID_RECEIVER)AND
				(MESSAGES_USER.ID_RECEIVER = NEW.ID_SENDER OR
				MESSAGES_USER.ID_RECEIVER = NEW.ID_RECEIVER) );
END // 

 CREATE TRIGGER INC_GROUP_ID_MSG BEFORE INSERT ON MESSAGES_GROUP
	FOR EACH ROW BEGIN
		SET NEW.ID_MSG = (
			SELECT ifnull(MAX(ID_MSG),0) + 1
			FROM MESSAGES_GROUP
			WHERE MESSAGES_GROUP.ID_GROUP = NEW.ID_GROUP);
END //            
 
 CREATE TRIGGER ADD_ADMIN_TO_GROUP AFTER INSERT ON GROUPS1
	FOR EACH ROW BEGIN
		INSERT INTO GROUPS_USERS VALUES(NEW.ID_GROUP, NEW.ID_ADMIN,TRUE);
END // 

CREATE TRIGGER IGNORE_ID_GROUP BEFORE INSERT ON GROUPS1
	FOR EACH ROW BEGIN
		SET NEW.ID_GROUP = NULL;
END // 

CREATE TRIGGER IGNORE_ID_USER BEFORE INSERT ON USERS
	FOR EACH ROW BEGIN
		SET NEW.ID_USER = NULL, NEW.LAST_SEEN = CURRENT_TIMESTAMP;
END // 

CREATE TRIGGER UPDATE_LAST_SEEN BEFORE UPDATE ON USERS
	FOR EACH ROW BEGIN
		SET NEW.LAST_SEEN = CURRENT_TIMESTAMP;
END // 

DELIMITER ;

