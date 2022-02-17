package pd.g46.RestService.data.structures.groupsUsers;


import pd.g46.RestService.data.database.queryable.Deletable;
import pd.g46.RestService.data.database.queryable.Insertable;
import pd.g46.RestService.data.database.queryable.Updatable;

public class GroupsUsersDB implements Deletable, Updatable, Insertable {


    public static final String TABLE = "GROUPS_USERS";
    public static final String ID_GROUP = "ID_GROUP";
    public static final String ID_USER = "ID_USER";
    public static final String IS_ACCEPTED = "IS_ACCEPTED";

    private int idUser;
    private int idGroup;
    private boolean isAccepted;

    public int getIdUser() {
        return idUser;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public GroupsUsersDB(int idUser, int idGroup) {
        this.idUser = idUser;
        this.idGroup = idGroup;
        this.isAccepted = false;

    }



    @Override
    public int[] ids() {
        return new int[]{idGroup,idUser};
    }

    @Override
    public String[] idsName() {
        return new String[]{ID_GROUP,ID_USER};
    }

    @Override
    public Object[] valuesToUpdate() {
        return new Object[]{isAccepted};
    }

    @Override
    public String[] valuesToUpdateName() {
        return new String[]{IS_ACCEPTED};
    }

    @Override
    public Object[] insertableValues() {
        return new Object[]{idUser,idGroup,isAccepted};
    }

    @Override
    public String table() {
        return TABLE;
    }

}
