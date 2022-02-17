package pd.g46.RestService.data.structures.user;


import pd.g46.RestService.data.database.queryable.Deletable;
import pd.g46.RestService.data.database.queryable.Insertable;
import pd.g46.RestService.data.database.queryable.Updatable;
import pd.g46.RestService.data.structures.group.Group;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class UserDB  implements Serializable, Insertable, Deletable, Updatable {

    static final long serialVersionUID = 1L;

    public static final int ONLINE_TIME = 30 * 1000;

    public static final String TABLE = "USERS";
    public static final String ID_USER = "ID_USER";
    public static final String NAME = "NAME_USER";
    public static final String USERNAME = "USERNAME";
    public static final String PASSWORD = "PASSWORD";
    public static final String LAST_SEEN = "LAST_SEEN";

    //It's ignored when inserted in the DB
    private Integer idUser;

    private String name;
    private String username;
    private Integer password;
    private Timestamp lastSeen;

    public ArrayList<UserPublic> contacts;
    public ArrayList<Group> groups;

    public UserDB(int idUser, String name) {

        this.idUser = idUser;
        this.name = name;
        this.username = null;
        this.password = null;
        this.lastSeen = null;

    }



    public UserDB(int idUser, String name, String username, int password, Timestamp lastSeen) {

        this.idUser = idUser;
        this.name = name;
        this.username = username;
        this.password = password;
        this.lastSeen = lastSeen;
        contacts = new ArrayList<>();
        groups = new ArrayList<>();
    }

    public UserDB( String password, String username) {

        this.idUser = null;
        this.name = null;
        this.username = username;
        this.password = password.hashCode();
        this.lastSeen = null;
        contacts = new ArrayList<>();
        groups = new ArrayList<>();
    }

    public UserDB(String name, String username, int password, Timestamp lastSeen) {

        this.idUser = null;
        this.name = name;
        this.username = username;
        this.password = password;
        this.lastSeen = lastSeen;
        contacts = new ArrayList<>();
        groups = new ArrayList<>();
    }

    public UserDB(ResultSet rs) throws SQLException {

        this.idUser = rs.getInt(1);
        this.name = rs.getString(2);
        this.username = rs.getString(3);
        this.password = rs.getInt(4);
        this.lastSeen = rs.getTimestamp(5);
        contacts = new ArrayList<>();
        groups = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        UserDB userDB = (UserDB) o;

        return username.equals(userDB.username) && name.equals(userDB.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username + name);
    }

    @Override
    public String toString() {

        return String.format("%s(%s) - %s",name != null? name:"",username, isOnline() ?"online":"offline");
    }

    public Object [] insertableValues(){

        return new Object[]{null,name,username,password,null};
    }

    @Override
    public int[] ids() {
        return new int[]{
            idUser
        };
    }

    @Override
    public String[] idsName() {
        return new String[]{ID_USER};
    }

    @Override
    public Object[] valuesToUpdate() {
        return new Object[]{name,username,password,lastSeen};
    }

    @Override
    public String[] valuesToUpdateName() {
        return new String[]{NAME,USERNAME,PASSWORD,LAST_SEEN};
    }

    @Override
    public String table() {
        return TABLE;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public int getPassword() {
        return password;
    }

    public boolean isOnline() {
        return lastSeen != null? lastSeen.getTime() - System.currentTimeMillis() < ONLINE_TIME: false;
    }

    public UserPublic searchContactsFor(String username){

        for( UserPublic usr : contacts ){
            if( username.equalsIgnoreCase( usr.getUsername() ))
                return usr;
        }
        return null;
    }

    public void deleteGroup(Group grp){
        groups.remove( grp );
    }
}
