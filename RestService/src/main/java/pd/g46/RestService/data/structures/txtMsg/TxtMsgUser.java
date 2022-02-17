package pd.g46.RestService.data.structures.txtMsg;


import pd.g46.RestService.data.database.queryable.Deletable;
import pd.g46.RestService.data.database.queryable.Insertable;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TxtMsgUser  extends TxtMsg implements Serializable, Insertable, Deletable {

    static final long serialVersionUID = 1L;

    public static final String TABLE = "MESSAGES_USER";

    public static final String ID_SENDER = "ID_SENDER";
    public static final String ID_RECEIVER = "ID_RECEIVER";


    private int idSender;
    private int idReceiver;


    public TxtMsgUser(String body, boolean isFile, int idSender,int idReceiver ) {

        super(body,isFile);

        this.idSender = idSender;
        this.idReceiver = idReceiver;

    }


    public TxtMsgUser(ResultSet rs) throws SQLException {
        super(rs);
        idSender = rs.getInt(ID_SENDER);
        idReceiver = rs.getInt(ID_RECEIVER);

    }



    @Override
    public Object[] insertableValues() {
        Object[] array1 = super.insertableValues();
        array1[array1.length - 2] = idSender;
        array1[array1.length - 1] = idReceiver;

        return array1 ;
    }

    @Override
    public int[] ids() {
        return new int[]{super.idMsg,idSender,idReceiver};
    }

    @Override
    public String[] idsName() {
        return new String[]{TxtMsg.ID_MSG,ID_SENDER,ID_RECEIVER};
    }

    @Override
    public String table() {
        return TABLE;
    }


}
