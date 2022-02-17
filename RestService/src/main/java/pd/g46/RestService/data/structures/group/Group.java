package pd.g46.RestService.data.structures.group;

import pd.g46.RestService.data.database.queryable.Deletable;
import pd.g46.RestService.data.database.queryable.Insertable;
import pd.g46.RestService.data.database.queryable.Updatable;
import pd.g46.RestService.data.structures.user.UserPublic;
import pd.g46.RestService.data.structures.user.UserDB;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Group implements Serializable, Insertable, Deletable, Updatable {

    static final long serialVersionUID = 1L;

    public static final String TABLE = "GROUPS1";
    public static final String ID_GROUP = "ID_GROUP";
    public static final String NAME = "NAME_GROUP";
    public static final String ID_ADMIN = "ID_ADMIN";

    private int idGroup;
    private int idAdmin;
    private String name;
    private ArrayList<UserPublic> users;

    public Group(int idGroup, int idAdmin, String name) {
        this.idGroup = idGroup;
        this.idAdmin = idAdmin;
        this.name = name;
    }

    public Group(ResultSet rs) throws SQLException {
        this.idGroup = rs.getInt(ID_GROUP);
        this.idAdmin = rs.getInt(ID_ADMIN);
        this.name = rs.getString(NAME);

        users = new ArrayList<>();

        while(rs.getInt(ID_GROUP) == idGroup){

            int idUser = rs.getInt(UserDB.ID_USER);

            if(idUser == 0)
                break;

            users.add(new UserPublic(rs));
            if(!rs.next())
                break;
        }
        rs.previous();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(" Group %s :\n",name));

        for (UserPublic user: users) {

            int userId = user.getId();
            sb.append(userId == idAdmin ? "Admin: " : "User: ").append(user).append(System.lineSeparator());
        }

        return sb.toString();
    }

    public int getIdGroup() {
        return idGroup;
    }

    public int getIdAdmin() {
        return idAdmin;
    }

    public String getName() {
        return name;
    }

    public ArrayList<UserPublic> getUsers(){
        return users;
    }

    public boolean removeUser(int userId ){

        if( userId == idAdmin )
            return false;

        for( UserPublic usr : users )
            if(usr.getId() == userId){
                users.remove( usr );
                return true;
            }
        return false;
    }

    @Override
    public Object[] insertableValues() {
        return new Object[]{null,idAdmin,name};
    }

    @Override
    public int[] ids() {
        return new int[]{idGroup};
    }

    @Override
    public String[] idsName() {
        return new String[]{ID_GROUP};
    }

    @Override
    public Object[] valuesToUpdate() {
        return new Object[]{name};
    }

    @Override
    public String[] valuesToUpdateName() {
        return new String[]{NAME};
    }

    @Override
    public String table() {
        return TABLE;
    }
}
