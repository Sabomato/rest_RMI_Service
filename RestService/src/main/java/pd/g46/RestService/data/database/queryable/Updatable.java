package pd.g46.RestService.data.database.queryable;

import java.io.Serializable;

public interface Updatable extends Serializable {

    static final long serialVersionUID = 1L;
    public int[] ids();

    public String[] idsName();

    public Object[] valuesToUpdate();

    public String[] valuesToUpdateName();

    public String table();
}
