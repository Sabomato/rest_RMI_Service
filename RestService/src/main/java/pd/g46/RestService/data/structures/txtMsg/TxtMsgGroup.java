package pd.g46.RestService.data.structures.txtMsg;


import pd.g46.RestService.data.database.queryable.Deletable;
import pd.g46.RestService.data.database.queryable.Insertable;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TxtMsgGroup extends TxtMsg implements  Serializable, Insertable, Deletable {


    static final long serialVersionUID = 1L;

    public static final String TABLE = "MESSAGES_GROUP";

    public static final String ID_SENDER = "ID_SENDER";
    public static final String ID_GROUP = "ID_GROUP";

    public int getIdSender() {
        return idSender;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public  int getIdMsg(){
        return idMsg;
    }

    private final int idSender;
    private final int idGroup;


    public TxtMsgGroup(String body, boolean isFile, int idSender,int idGroup) {
        super(body,isFile);

        this.idSender = idSender;
        this.idGroup = idGroup;
    }

    public TxtMsgGroup(ResultSet rs) throws SQLException {
        super(rs);
                idSender = rs.getInt(ID_SENDER);
        idGroup = rs.getInt(ID_GROUP);

    }



    @Override
    public Object[] insertableValues() {
        Object[] array = super.insertableValues();
        array[array.length - 2] = idSender;
        array[array.length - 1] = idGroup;

        return array;
    }

    @Override
    public int[] ids() {
    return new int[] {super.idMsg,idGroup,idSender};
    }

    @Override
    public String[] idsName() {
        return new String[]{ID_MSG,ID_GROUP,ID_SENDER};
    }

    @Override
    public  String table() {

        return TABLE;
    }

}
