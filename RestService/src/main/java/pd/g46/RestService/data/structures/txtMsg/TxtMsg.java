package pd.g46.RestService.data.structures.txtMsg;

import pd.g46.RestService.data.database.queryable.Insertable;

import java.io.Serializable;
import java.sql.*;

abstract public class TxtMsg implements Serializable, Insertable {

    static final long serialVersionUID = 1L;

    public static final String ID_MSG = "ID_MSG";
    public static final String BODY = "BODY";
    public static final String TIMESTAMP = "TIMESTAMP";
    public static final String IS_FILE = "IS_FILE";
    public static final String WAS_SEEN = "WAS_SEEN";

    protected int idMsg;
    protected String body;
    protected Timestamp timestamp;

    public boolean isFile() {
        return isFile;
    }

    protected boolean isFile;
    protected boolean wasSeen;

    public TxtMsg(String body, boolean isFile) {
        this.body = body;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.isFile = isFile;
        wasSeen = false;
    }

    public TxtMsg(ResultSet rs) throws SQLException {
        idMsg = rs.getInt(1);
        body = rs.getString(2);
        timestamp = rs.getTimestamp(3);
        isFile = rs.getBoolean(4);
        wasSeen =rs.getBoolean(5);

    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {

        //lastIndexOf("\") para ver nome do ficheiro
        return isFile?"File :":":" + body + '\n' +
                "(" + timestamp +"):" + (wasSeen?"":"Not " + "Seen");
    }

    @Override
    public Object[] insertableValues() {

        return  new Object[]{ null,body,timestamp,isFile,wasSeen,null,null };
    }

    @Override
    public String table() {
        return null;
    }
}
