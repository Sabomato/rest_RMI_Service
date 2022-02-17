package pd.g46.RestService.data.database;

import pd.g46.RestService.data.database.queryable.Deletable;
import pd.g46.RestService.data.database.queryable.Insertable;
import pd.g46.RestService.data.database.queryable.Updatable;
import pd.g46.RestService.data.structures.group.Group;
import pd.g46.RestService.data.structures.txtMsg.TxtMsg;
import pd.g46.RestService.data.structures.txtMsg.TxtMsgGroup;
import pd.g46.RestService.data.structures.txtMsg.TxtMsgUser;
import pd.g46.RestService.data.structures.user.UserDB;
import pd.g46.RestService.data.structures.contact.ContactDB;
import pd.g46.RestService.data.structures.groupsUsers.GroupsUsersDB;
import pd.g46.RestService.data.structures.user.UserPublic;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import static pd.g46.RestService.data.structures.contact.ContactDB.ID_CONTACT;
import static pd.g46.RestService.data.structures.groupsUsers.GroupsUsersDB.ID_GROUP;

public  class DBManager {
    private static final int DUPLICATE_ROW = 1062;
    public static final int N_MSGS = 20;

    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private static final String DB_URL = "jdbc:mysql://localhost";
    private static final String DB_NAME = "msg_service";

    static Connection connection = null;

    static {
        if(!connect())
            System.exit(-1);


    }

    private static synchronized Statement createStatement() throws SQLException {

        return connection.createStatement();
    }

    private static synchronized Statement createStatementScrollable() throws SQLException {

        return connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
    }

    public static boolean connect(){

        try {

            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            connection.createStatement().execute("USE " + DB_NAME + ";");

            return connection != null;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Exception raised when trying to connect with database!");
        }
        return connection != null;

    }

    public static void disconnect(){
        try {
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();

        }

    }

    public static StatusMsg insertRow(Insertable insertable){

        String query = QueryHandler.InsertRow(insertable.table(), insertable.insertableValues());
        try {
            Statement smt = createStatement();

            int result = smt.executeUpdate(query);

            return StatusMsg.SUCESS;

        } catch (SQLException e) {

            if (e.getErrorCode() == DUPLICATE_ROW) {
                return StatusMsg.ROW_ALREADY_EXISTS;
            }
            e.printStackTrace();
            System.err.println("Query:\n" + query);

            return StatusMsg.SQL_ERROR;
        }

    }

    public static boolean deleteRow(Deletable deletable) {

        String query = QueryHandler.deleteByPK(deletable.table(), deletable.ids(),deletable.idsName());
        try {
            Statement smt = createStatement();

            int result = smt.executeUpdate(query);
            return result > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Query:\n" + query);
            return false;
        }

    }

    public static StatusMsg  registerUser(UserDB user){

        try {
            Statement smt = createStatement();

            String aux = QueryHandler.InsertRow(user.table(),user.insertableValues());

            smt.executeUpdate(aux);

            return StatusMsg.SUCESS;

        }

        catch (SQLException e){

            if (e.getErrorCode() == DUPLICATE_ROW) {
                //System.err.println(e.getErrorCode());
                return StatusMsg.PAIR_NAME_USERNAME_ALREADY_EXISTS;
            }
            e.printStackTrace();
            return StatusMsg.SQL_ERROR;

        }

    }

    public static StatusMsg  addContact(ContactDB contactDB){

    String query = QueryHandler.InsertRow(contactDB.table(), contactDB.insertableValues());
        try {

            Statement smt = createStatement();
            smt.executeUpdate(query);

            return StatusMsg.SUCESS;

        }

        catch (SQLException e){

            if (e.getErrorCode() == DUPLICATE_ROW) {
                return StatusMsg.CONTACT_ALREADY_EXISTS;
            }
            e.printStackTrace();
            System.err.println("Query: \n" + query);
            return StatusMsg.SQL_ERROR;

        }

    }

    public static StatusMsg  createGroup(Group group){

        String query = QueryHandler.InsertRow(group.table(),group.insertableValues());
        try {

            Statement smt = createStatement();
            smt.executeUpdate(query);

            return StatusMsg.SUCESS;

        }

        catch (SQLException e){

            if (e.getErrorCode() == DUPLICATE_ROW) {
                return StatusMsg.PAIR_GROUP_NAME_ADMIN_ALREADY_EXISTS;
            }
            e.printStackTrace();
            System.err.println("Query: \n" + query);
            return StatusMsg.SQL_ERROR;

        }

    }

    public static boolean updateRow(Updatable upd){

    String query = QueryHandler.updateByPK(
            upd.table(), upd.ids(), upd.idsName() , upd.valuesToUpdate(), upd.valuesToUpdateName());

        if(query.isEmpty())
            return false;
        try {
            Statement smt = createStatement();

            int result = smt.executeUpdate(query);
            
            return result > 0;

        } catch (SQLException e) {
            
            e.printStackTrace();
            System.err.println(e.getErrorCode()); 
            return false;
        }
        

    }

    public static boolean getContactInfo(UserPublic[] user, int idContact){

        String query =
                "\nSELECT * " +
                "\nFROM " + UserDB.TABLE +
                "\nWHERE" + UserDB.ID_USER + " = " + idContact;

        try {
            Statement smt = createStatement();

            ResultSet rs = smt.executeQuery(query);

            user[0] = new UserPublic(rs);
            return true;

        } catch (SQLException e) {

            e.printStackTrace();
            System.err.println("Query1:\n" + query);
            return false;
        }

    }

    public static boolean getContactRequests(int idUser,ArrayList <UserPublic> contacts){

        String query =
                "\nSELECT *\n" +
                "\nFROM " + UserDB.TABLE +
                "\nWHERE " + UserDB.ID_USER + " IN" +
                "\n\t\t\t\t         (" +
                "\n\t\t\t\t         SELECT " + UserDB.ID_USER +
                "\n\t\t\t\t         FROM " + ContactDB.TABLE +
                "\n\t\t\t\t         WHERE " +  ContactDB.ID_CONTACT + "=" + idUser +
                "\n\t\t\t\t         )" +
                "\nAND " + UserDB.ID_USER + " NOT IN" +
                "\n\t\t\t\t         (" +
                "\n\t\t\t\t         SELECT "+ ContactDB.ID_CONTACT +
                "\n\t\t\t\t         FROM " + ContactDB.TABLE +
                "\n\t\t\t\t         WHERE " + ContactDB.ID_USER + " = " + idUser +
                "\n\t\t\t\t         ) ";

        try {
            Statement smt = createStatementScrollable();

            ResultSet rs = smt.executeQuery(query);

            while (rs.next()){
                contacts.add(new UserPublic(rs));
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();

            System.err.println("Query1:\n" + query);


            return false;
        }

    }

    public static boolean getJoinGroupRequests(int idUser,HashMap <Integer,ArrayList<UserPublic>> requests){

        String query =
                    "\nSELECT " + UserDB.TABLE + "." + UserDB.ID_USER +","+ UserDB.NAME +
                            ", " +UserDB.USERNAME + ", " +UserDB.LAST_SEEN + ", REQUESTS." + Group.ID_GROUP +
                    "\nFROM " + UserDB.TABLE + ", " +
                                    "\n(" +
                                    "\nselect *" +
                                    "\nFROM " + GroupsUsersDB.TABLE  +
                                    "\nWHERE " + ID_GROUP + " IN" +
                                            "\n(" +
                                            "\nSELECT "+ Group.ID_GROUP +
                                            "\nFROM " + Group.TABLE +
                                            "\nWHERE " + Group.ID_ADMIN + " = " + idUser  +
                                            "\n)"+
                                    "\nAND " + GroupsUsersDB.IS_ACCEPTED + " = FALSE"+
                                    "\n)as REQUESTS" +
                    "\n WHERE " +UserDB.TABLE + "." + UserDB.ID_USER + " = REQUESTS." + GroupsUsersDB.ID_USER +
                    "\n;";

        try {
            ArrayList< UserPublic > auxArray;
            Statement smt = createStatementScrollable();

            ResultSet rs = smt.executeQuery(query);

            while (rs.next()){

                int idGroup = rs.getInt(GroupsUsersDB.ID_GROUP);

                auxArray = requests.computeIfAbsent(idGroup, k -> new ArrayList<>());

                auxArray.add(new UserPublic(rs));

            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();

            System.err.println("Query1:\n" + query);


            return false;
        }

    }

    public static boolean getGroupInfo(Group[] group,int idGroup ){

        String query =
                "SELECT * " +
                "\nFROM " + Group.TABLE + ", " + UserDB.TABLE +
                "\nWHERE "+ Group.ID_GROUP + " = " + idGroup +
                "\nAND " + UserDB.TABLE + "." + UserDB.ID_USER + " IN (" +
                "\nSELECT " + UserDB.ID_USER +
                "\nFROM " + GroupsUsersDB.TABLE +
                "\nWHERE " + GroupsUsersDB.TABLE +"." + ID_GROUP + " = " + Group.TABLE + "." + Group.ID_GROUP +
                "\nAND "+ GroupsUsersDB.IS_ACCEPTED + " = TRUE)" +
                ";";

        try {
            Statement smt = createStatement();

            ResultSet rs = smt.executeQuery(query);

            group[0] = new Group(rs);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();

            System.err.println("Query1:\n" + query);


            return false;
        }
    }

    public static boolean getAllContacts(int idUser, ArrayList <UserPublic> contacts ){

        String query =
                "SELECT *" +
                        "\nFROM " + UserDB.TABLE +
                        "\nWHERE " + UserDB.ID_USER + " IN (" +
                        "\nSELECT " + ContactDB.ID_CONTACT +
                        "\nFROM " + ContactDB.TABLE +
                        "\nWHERE " + ContactDB.ID_USER + " = " + idUser +
                        ") " +
                        "\nAND " + UserDB.ID_USER + " IN (" +
                        "\nSELECT " + ContactDB.ID_USER +
                        "\nFROM " + ContactDB.TABLE +
                        "\nWHERE " + ID_CONTACT + " = " + idUser +
                        ")"+
                        ";";

        try {
            Statement smt = createStatementScrollable();

            ResultSet rs = smt.executeQuery(query);

            while (rs.next()){

                contacts.add( new UserPublic(rs));
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();

            System.err.println("Query1:\n" + query);


            return false;
        }
    }

    public static boolean getAllGroups(int idUser, ArrayList <Group> groups ){

        String query =
                "SELECT * " +
                        "\nFROM " + Group.TABLE + ", " + UserDB.TABLE +
                        "\nWHERE "+ Group.ID_GROUP + " IN (" +
                        "\nSELECT " + Group.ID_GROUP +
                        "\nFROM "+ GroupsUsersDB.TABLE +
                        "\nWHERE " + GroupsUsersDB.ID_USER + " = " + idUser +
                        "\nAND "+ GroupsUsersDB.IS_ACCEPTED + " = TRUE" +
                        "\n)" +
                        "\nAND " + UserDB.TABLE + "." + UserDB.ID_USER + " IN (" +
                        "\nSELECT " + UserDB.ID_USER +
                        "\nFROM " + GroupsUsersDB.TABLE +
                        "\nWHERE " + GroupsUsersDB.TABLE +"." + ID_GROUP + " = " + Group.TABLE + "." + Group.ID_GROUP +
                        "\nAND "+ GroupsUsersDB.IS_ACCEPTED + " = TRUE)" +
                        "\nORDER BY " + Group.ID_GROUP +
                        ";";


        try {
            Statement smt = createStatementScrollable();

            ResultSet rs = smt.executeQuery(query);

            while (rs.next()){

                groups.add(new Group(rs));
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();

            System.err.println("Query1:\n" + query);


            return false;
        }
    }

    public static boolean updateUserState(UserDB user){

        Object[] stateObj = new Object[]{null};
        String query = QueryHandler.updateByPK(
                user.table(), user.ids(), user.idsName() , stateObj, new String[]{UserDB.LAST_SEEN});
        try {
            Statement smt = createStatement();

            int result = smt.executeUpdate(query);

            return result > 0;

        } catch (SQLException e) {

            e.printStackTrace();
            System.err.println(e.getErrorCode());
            return false;
        }

    }

    public static StatusMsg getMsgsContactAfter(UserDB userDB, int idContact, ArrayList<TxtMsgUser> msgs, int nMsgs, int idMsg){

        return getMsgsContactAfter(userDB.getIdUser(), idContact, msgs, nMsgs, idMsg);
    }

    public static StatusMsg getMsgsContactAfter(int userId, int idContact, ArrayList<TxtMsgUser> msgs, int nMsgs, int idMsg){


        String query =
                "SELECT * " +
                "\nFROM " + TxtMsgUser.TABLE +
                "\nWHERE (" + TxtMsgUser.ID_RECEIVER + " = " + userId +
                "\n\tAND " + TxtMsgUser.ID_SENDER + " = " + idContact + ") " +
                "\nOR (" + TxtMsgUser.ID_SENDER + " = " + userId +
                "\n\tAND " + TxtMsgUser.ID_RECEIVER + " = " + idContact + ") " +
                "\nAND " + TxtMsg.ID_MSG + " = " + idMsg +
                "\nORDER BY " + TxtMsg.ID_MSG + " DESC" +
                "\nLIMIT " + nMsgs + ";";

        try {

            Statement smt = createStatement();
            ResultSet rs;

            rs = smt.executeQuery(query);

            while (rs.next()){
                msgs.add(new TxtMsgUser(rs));
            }

            return StatusMsg.SUCESS;


        } catch (SQLException e) {
            e.printStackTrace();

            System.err.println("Query1:\n" + query);
            return StatusMsg.SQL_ERROR;
        }
    }

    public static StatusMsg getMsgsContactAfterId(int userId, int idContact, ArrayList<TxtMsgUser> msgs, int nMsgs, int idMsg){

        nMsgs = nMsgs == 0 ? N_MSGS : nMsgs;
        String query =
                "SELECT * " +
                "\nFROM " + TxtMsgUser.TABLE +
                "\nWHERE (" + TxtMsgUser.ID_RECEIVER + " = " + userId +
                "\n\tAND " + TxtMsgUser.ID_SENDER + " = " + idContact + ") " +
                "\nOR (" + TxtMsgUser.ID_SENDER + " = " + userId +
                "\n\tAND " + TxtMsgUser.ID_RECEIVER + " = " + idContact + ") " +
                "\nAND " + TxtMsg.ID_MSG + "  > " + idMsg +
                "\nAND " + TxtMsg.IS_FILE + " = false" +
                "\nORDER BY " + TxtMsg.ID_MSG + " DESC" +
                "\nLIMIT " + nMsgs + ";";

        try {

            Statement smt = createStatement();
            ResultSet rs;

            rs = smt.executeQuery(query);

            while (rs.next()){
                msgs.add(new TxtMsgUser(rs));
            }

            return StatusMsg.SUCESS;


        } catch (SQLException e) {
            e.printStackTrace();

            System.err.println("Query1:\n" + query);
            return StatusMsg.SQL_ERROR;
        }
    }

    public static StatusMsg getNotSeenMsgsContact(int idUser, int idContact, HashMap<Integer,ArrayList<TxtMsgUser>> msgs){


        String query = idContact != 0?
                "SELECT * " +
                "\nFROM " + TxtMsgUser.TABLE +
                "\nWHERE " + TxtMsgUser.ID_RECEIVER + " = " + idUser +
                "\nAND " + TxtMsg.WAS_SEEN + " = " + "FALSE" +
                "\nAND " + TxtMsgUser.ID_SENDER + " = " + idContact + ";"
                :
                "SELECT * " +
                "\nFROM " + TxtMsgUser.TABLE +
                "\nWHERE " + TxtMsgUser.ID_RECEIVER + " = " + idUser +
                "\nAND " + TxtMsg.WAS_SEEN + " = " + "FALSE";
        try {
            Statement smt = createStatement();
            ResultSet rs;
            ArrayList<TxtMsgUser> auxMsg;


            rs = smt.executeQuery(query);


            while (rs.next()){

                int idSender = rs.getInt(TxtMsgUser.ID_SENDER);

                auxMsg = msgs.computeIfAbsent(idSender, k -> new ArrayList<>());

                auxMsg.add(new TxtMsgUser(rs));

            }

            return StatusMsg.SUCESS;


        } catch (SQLException e) {
            e.printStackTrace();
            return StatusMsg.SQL_ERROR;
        }
    }

    public static StatusMsg getLastMsgGroupFrom(int idGroup, ArrayList<TxtMsgGroup> msgs, int nMsgs, int idMsg){

        String query =
                "SELECT * " +
                        "\nFROM " + TxtMsgGroup.TABLE +
                        "\nWHERE " + TxtMsgGroup.ID_GROUP + " = " + idGroup +
                        "\n\tAND " + TxtMsg.ID_MSG + " > " + idMsg +
                        "\nLIMIT " + nMsgs + ";";

        try {

            Statement smt = createStatement();
            ResultSet rs;

            rs = smt.executeQuery(query);


            while (rs.next()){

                msgs.add(new TxtMsgGroup(rs));
            }

            return StatusMsg.SUCESS;


        } catch (SQLException e) {
            e.printStackTrace();

            System.err.println("Query1:\n" + query);
            return StatusMsg.SQL_ERROR;
        }
    }

    public static StatusMsg getMsgsGroupAfterId( int idGroup, ArrayList<TxtMsgGroup> msgs, int nMsgs,int idMsg){

        nMsgs = nMsgs == 0 ? N_MSGS : nMsgs;
        String query =
                "SELECT * " +
                "\nFROM " + TxtMsgGroup.TABLE +
                "\nWHERE " + TxtMsgGroup.ID_GROUP + " = " + idGroup +
                "\n\tAND " + TxtMsg.ID_MSG + " > " + idMsg +
                "\n\tAND " + TxtMsg.IS_FILE + " = false" +
                "\nLIMIT " + nMsgs + ";";

        try {

            Statement smt = createStatement();
            ResultSet rs;

            rs = smt.executeQuery(query);


            while (rs.next()){

                msgs.add(new TxtMsgGroup(rs));
            }

            return StatusMsg.SUCESS;


        } catch (SQLException e) {
            e.printStackTrace();

            System.err.println("Query1:\n" + query);
            return StatusMsg.SQL_ERROR;
        }
    }

    public static StatusMsg getMsgsGroupAfter( int idGroup, ArrayList<TxtMsgGroup> msgs, int nMsgs,int idMsg){

        nMsgs = nMsgs == 0 ? N_MSGS : nMsgs;
        String query =
                "SELECT * " +
                "\nFROM " + TxtMsgGroup.TABLE +
                "\nWHERE " + TxtMsgGroup.ID_GROUP + " = " + idGroup +
                "\n\tAND " + TxtMsg.ID_MSG + " > " + idMsg +
                "\nLIMIT " + nMsgs + ";";

        try {

            Statement smt = createStatement();
            ResultSet rs;

            rs = smt.executeQuery(query);


            while (rs.next()){

                msgs.add(new TxtMsgGroup(rs));
            }

            return StatusMsg.SUCESS;


        } catch (SQLException e) {
            e.printStackTrace();

            System.err.println("Query1:\n" + query);
            return StatusMsg.SQL_ERROR;
        }
    }

    public static StatusMsg login(UserDB[] userDB){

        String query = "SELECT * " +
                "\nFROM " + UserDB.TABLE +
                "\nWHERE STRCMP(" + UserDB.USERNAME + ",\"" +
                userDB[0].getUsername() + "\") = 0;";

        try {
            Statement smt = createStatement();

            ResultSet rs = smt.executeQuery(query);

            if(!rs.next())
                return StatusMsg.UNREGISTERED_CLIENT;

            int password = rs.getInt(UserDB.PASSWORD);
            if( password  == userDB[0].getPassword() ){
                userDB[0] = new UserDB(rs);

                return getContactsAndGroups(userDB);

            }
            return StatusMsg.WRONG_PASSWORD;


        } catch (SQLException e) {

            e.printStackTrace();
            System.err.println("Query1:\n" + query);
            return StatusMsg.SQL_ERROR;
        }

    }

    public static StatusMsg loginSimple(UserDB[] userDB){

        String query = "SELECT * " +
                "\nFROM " + UserDB.TABLE +
                "\nWHERE STRCMP(" + UserDB.USERNAME + ",\"" +
                userDB[0].getUsername() + "\") = 0;";

        try {
            Statement smt = createStatement();

            ResultSet rs = smt.executeQuery(query);

            if(!rs.next())
                return StatusMsg.UNREGISTERED_CLIENT;

            int password = rs.getInt(UserDB.PASSWORD);
            if( password  == userDB[0].getPassword() ){
                userDB[0] = new UserDB(rs);

                return StatusMsg.SUCESS;

            }
            return StatusMsg.WRONG_PASSWORD;

        } catch (SQLException e) {

            e.printStackTrace();
            System.err.println("Query1:\n" + query);
            return StatusMsg.SQL_ERROR;
        }

    }



    public static StatusMsg getContactsAndGroups(UserDB[] userDB){

        String query =
                "SELECT *" +
                "\nFROM " + UserDB.TABLE +
                "\nWHERE " + UserDB.ID_USER + " IN (" +
                    "\nSELECT " + ContactDB.ID_CONTACT +
                    "\nFROM " + ContactDB.TABLE +
                    "\nWHERE " + ContactDB.ID_USER + " = " + userDB[0].getIdUser() +
                    ") " +
                "\nAND " + UserDB.ID_USER + " IN (" +
                    "\nSELECT " + ContactDB.ID_USER +
                    "\nFROM " + ContactDB.TABLE +
                    "\nWHERE " + ID_CONTACT + " = " + userDB[0].getIdUser() +
                    ")"+
                ";";

        String query2 =
                "SELECT * " +
                "\nFROM " + Group.TABLE + ", " + UserDB.TABLE +
                "\nWHERE "+ Group.ID_GROUP + " IN (" +
                    "\nSELECT " + Group.ID_GROUP +
                    "\nFROM "+ GroupsUsersDB.TABLE +
                    "\nWHERE " + GroupsUsersDB.ID_USER + " = " + userDB[0].getIdUser() +
                    "\nAND "+ GroupsUsersDB.IS_ACCEPTED + " = TRUE" +
                    "\n)" +
                "\nAND " + UserDB.TABLE + "." + UserDB.ID_USER + " IN (" +
                    "\nSELECT " + UserDB.ID_USER +
                    "\nFROM " + GroupsUsersDB.TABLE +
                    "\nWHERE " + GroupsUsersDB.TABLE +"." + ID_GROUP + " = " + Group.TABLE + "." + Group.ID_GROUP +
                    "\nAND "+ GroupsUsersDB.IS_ACCEPTED + " = TRUE)" +
                "\nORDER BY " + Group.ID_GROUP +
                ";";

        try {
            Statement smt = createStatementScrollable();

            ResultSet rs = smt.executeQuery(query);

            while (rs.next()){

                userDB[0].contacts.add(new UserPublic(rs));
            }

            rs = smt.executeQuery(query2);

            while (rs.next()){
                userDB[0].groups.add(new Group(rs));
            }

            return StatusMsg.SUCESS;

        } catch (SQLException e) {
            e.printStackTrace();

            System.err.println("Query1:\n" + query);
            System.err.println("Query2:\n" + query2);

            return StatusMsg.SQL_ERROR;
        }

    }

    public static StatusMsg searchUsers(String username,String name, ArrayList<UserPublic> users ){

        try {
            Statement smt = createStatement();

            ResultSet rs = smt.executeQuery("SELECT *" +
                    "FROM " + UserDB.TABLE +
                    " WHERE LOWER(" + UserDB.NAME + ") LIKE LOWER('%"+ name + "%') OR LOWER(" +
                     UserDB.USERNAME + ") LIKE LOWER('%"+ username + "%');");


            while (rs.next()){
                users.add(new UserPublic(rs));
            }

            return StatusMsg.SUCESS;

        }
        catch (SQLException e){
            e.printStackTrace();
            return StatusMsg.SQL_ERROR;

        }

    }

    public static StatusMsg searchGroups(String name, ArrayList<Group> groups ){

        String query =
                "SELECT * " +
                "\nFROM " + Group.TABLE + ", " + UserDB.TABLE +
                "\nWHERE LOWER(" + Group.NAME + ") LIKE LOWER('%"+ name + "%') " +
                    "\nAND " + UserDB.TABLE + "." + UserDB.ID_USER + " IN (" +
                    "\nSELECT " + UserDB.ID_USER +
                    "\nFROM " + GroupsUsersDB.TABLE +
                    "\nWHERE " + GroupsUsersDB.TABLE +"." + ID_GROUP + " = " + Group.TABLE + "." + Group.ID_GROUP +
                        "\nAND "+ GroupsUsersDB.IS_ACCEPTED + " = TRUE);";


        try {
            Statement smt = createStatementScrollable();

            ResultSet rs = smt.executeQuery(query);


            while (rs.next()){
                groups.add(new Group(rs));
            }

            return StatusMsg.SUCESS;

        }
        catch (SQLException e){
            System.err.println("Query1:\n" + query);
            e.printStackTrace();
            return StatusMsg.SQL_ERROR;

        }

    }


}
