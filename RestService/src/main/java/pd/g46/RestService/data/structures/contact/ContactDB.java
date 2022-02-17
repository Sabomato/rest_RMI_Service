package pd.g46.RestService.data.structures.contact;


import pd.g46.RestService.data.database.queryable.Deletable;
import pd.g46.RestService.data.database.queryable.Insertable;

public class ContactDB implements Deletable, Insertable {

    public static final String TABLE = "CONTACTS";
    public static final String ID_USER = "ID_USER";
    public static final String ID_CONTACT = "ID_CONTACT";
    private int idUser;
    private int idContact;


    public ContactDB(int idUser, int idContact) {
        this.idUser = idUser;
        this.idContact = idContact;

    }



    @Override
    public int[] ids() {
        return new int[]{idUser,idContact};
    }

    @Override
    public String[] idsName() {
        return new String[]{ID_USER,ID_CONTACT};
    }

    @Override
    public Object[] insertableValues() {
        return new Object[]{idUser,idContact};
    }

    @Override
    public String table() {
        return TABLE;
    }


}
