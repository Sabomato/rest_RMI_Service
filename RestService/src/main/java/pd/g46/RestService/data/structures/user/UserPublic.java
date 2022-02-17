package pd.g46.RestService.data.structures.user;


import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UserPublic implements Serializable {

    static final long serialVersionUID = 1L;


    private int id;
    private String name;
    private String username;
    private Timestamp lastSeen;


    public UserPublic(int id, String name, String username, Timestamp lastSeen) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.lastSeen = lastSeen;
    }

    public UserPublic(ResultSet rs) throws SQLException {

        this.id = rs.getInt(UserDB.ID_USER);
        this.name = rs.getString(UserDB.NAME);
        this.username = rs.getString(UserDB.USERNAME);
        this.lastSeen = rs.getTimestamp(UserDB.LAST_SEEN);
    }

    public String toString() {
        return String.format("%s(%s) - %s",name,username, isOnline() ?"online":"offline");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public Timestamp getlastSeen() {
        return lastSeen;
    }

    public boolean isOnline() {
        return lastSeen.getTime() - System.currentTimeMillis() < UserDB.ONLINE_TIME;
    }


}
