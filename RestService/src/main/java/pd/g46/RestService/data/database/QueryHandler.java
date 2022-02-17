package pd.g46.RestService.data.database;

public class QueryHandler {

    public static String InsertRow(String table,Object[]values){

        StringBuilder buffer = new StringBuilder();
        int i = 0;

        buffer.append("INSERT INTO ").append(table).append(" VALUES(");


        for (; i < values.length - 1; ++i) {

            convertToQueryValue(buffer,values[i]);

            buffer.append(",");

        }

        convertToQueryValue(buffer,values[i]);

        buffer.append(")");

        return buffer.toString();

    }

    public static String deleteByPK(String table, int[] pk, String[] pkName){

        return "DELETE FROM " + table + pkCondition(pk, pkName);
    }

    private static void convertToQueryValue(StringBuilder buffer,Object obj){


        if(obj == null){
            buffer.append("null");
        }
        else if(obj.getClass() == String.class){
            buffer.append("\"").append(obj).append("\"");
        }else{
            buffer.append(obj);
        }

    }

    public static String updateByPK(String table, int[] pk, String[] pkName,Object[] values, String[] valuesName){

        StringBuilder buffer = new StringBuilder();
        boolean notNullFlag = false;
        int i = 0;
        buffer.append("UPDATE ").append(table).append("\nSET ");

        for (; i < values.length - 1; ++i) {
            if(values[i] == null)
                continue;
            buffer.append(valuesName[i]).append(" = ");
            convertToQueryValue(buffer,values[i]);
            if(values[i + 1] != null)
                buffer.append(", ");
            notNullFlag = true;

        }
        if(values[i] != null){
            buffer.append(valuesName[i]).append(" = ");
            convertToQueryValue(buffer,values[i]);
        }


        buffer.append(pkCondition(pk,pkName));

        return notNullFlag ? buffer.toString(): "";

    }

    private static String pkCondition(int[] pk, String[] pkName){

        StringBuilder buffer = new StringBuilder();
        buffer.append("\nWHERE ");
        int i = 0;
        for (; i<pk.length - 1; ++i) {
            buffer.append(pkName[i]).append(" = ").append(pk[i]);
            buffer.append("\nAND ");
        }
        buffer.append(pkName[i]).append(" = ").append(pk[i]).append(";");

        return buffer.toString();

    }

    public static String search(String table, String vars,Object[] values){

        int i = 0;


        return "SELECT * FROM" + table;
    }



}
