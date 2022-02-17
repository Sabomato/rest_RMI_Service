package pd.g46.RestService.data.database.queryable;


import java.io.Serializable;

public interface Deletable extends Serializable {

    static final long serialVersionUID = 1L;

    public int[] ids();

    public String[] idsName();

    public String table();
}
