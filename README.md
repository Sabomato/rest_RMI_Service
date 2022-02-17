# rest_RMI_Service

## Overview

This project was made in the context of the "Distributted Programming" subject, computer engineering course, in [ISEC](https://www.isec.pt/PT/Default.aspx).

The purpose of this project was to learn the basic standards of the **REST architecture**, **spring boot framework**, **Java RMI**, **observer-pattern** and as a bonus **MySQL database system**.

The database's tables and its relations represent a  messaging system, with users, their contacts and groups, and respective messages.  

The Java RMI server simply notifies the registered clients whenever a http request is received.  



## Usage

### Requirements

- IDE that supports maven projects
- MySQLServer 8.0.27
- JDK 17

### Steps 

1. Download the repostory as a **.zip**.
2. Open it as maven project in your favorite IDE
3. run the scripts in the folder **[MySQL](https://github.com/Sabomato/rest_RMI_Service/tree/master/MySQL)** (first "CreateDB.sql" to create the database, and then "PopulateDB.sql" to populate it).
4. run "RestService" module to start the server.
5. run "RMI" module to start the Java RMI client (**not required**)
6. Make a http request([these are the possible requests]()).  
These are already a couple pre-made requests
[here](https://god.gw.postman.com/run-collection/18094519-2a4431a6-4472-422e-81cb-f770810c988c?action=collection%2Ffork&collection-url=entityId%3D18094519-2a4431a6-4472-422e-81cb-f770810c988c%26entityType%3Dcollection%26workspaceId%3D9a029073-1bcd-4de7-824e-28dd6ebff09a)
(**requires a postman account**);

 



