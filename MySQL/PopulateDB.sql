USE MSG_SERVICE;
INSERT INTO USERS ( NAME_USER,USERNAME,PASSWORD,LAST_SEEN) VALUES ("Manuel","manuel17",3433489,default);
INSERT INTO USERS ( NAME_USER,USERNAME,PASSWORD,LAST_SEEN) VALUES ("Joana","joana123",13242,default);
INSERT INTO USERS ( NAME_USER,USERNAME,PASSWORD,LAST_SEEN) VALUES ("Hugo","hugo121",12242,default);
INSERT INTO CONTACTS VALUES(1,2);
INSERT INTO CONTACTS VALUES(2,1);
INSERT INTO MESSAGES_USER VALUES(NULL,"A Beautiful test",DEFAULT,false,false,1,2);
INSERT INTO MESSAGES_USER VALUES(NULL,"A Beautiful test Indeed",DEFAULT,false,false,2,1);
INSERT INTO MESSAGES_USER VALUES(NULL,"Hello then",DEFAULT,false,false,2,1);

INSERT INTO GROUPS1 VALUES(NULL,1,"Grupo Fixe");
INSERT INTO GROUPS1 VALUES(NULL,2,"Grupo Fixe");
INSERT INTO GROUPS1 VALUES(NULL,1,"Grupo Fixe3");
INSERT INTO GROUPS_USERS VALUES(1,2,true);
INSERT INTO GROUPS_USERS VALUES(1,3,false);
INSERT INTO GROUPS_USERS VALUES(3,3,false);
INSERT INTO CONTACTS VALUES(1,3);
INSERT INTO CONTACTS VALUES(3,1);


INSERT INTO MESSAGES_USER VALUES(NULL,"A Beautiful test 3",DEFAULT,false,false,1,3);
INSERT INTO MESSAGES_USER VALUES(NULL,"A Beautiful test Indeed 3",DEFAULT,false,false,3,1);
INSERT INTO MESSAGES_USER VALUES(NULL,"Hello then 3",DEFAULT,false,false,3,1);

INSERT INTO MESSAGES_GROUP VALUES(NULL,"A Beautiful test",DEFAULT,false,false,2,2);
INSERT INTO MESSAGES_GROUP VALUES(NULL,"A Beautiful test Indeed",DEFAULT,false,false,1,1);
INSERT INTO MESSAGES_GROUP VALUES(NULL,"Hello then",DEFAULT,false,false,1,1);
