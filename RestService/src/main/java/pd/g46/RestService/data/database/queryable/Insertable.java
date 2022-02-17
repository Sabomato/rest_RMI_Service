package pd.g46.RestService.data.database.queryable;

import java.io.Serializable;

abstract public interface Insertable extends Serializable {

    static final long serialVersionUID = 1L;

    abstract public Object[] insertableValues();

    abstract public String table ();

}
