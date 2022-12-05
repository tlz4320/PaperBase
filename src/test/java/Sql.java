import Sql.CodeBase;
import util.DataBaseUtil;

public class Sql {
    public static void main(String[] args) {
        CodeBase codeBase = new CodeBase(0, "test"," String _path","2020-11-12"," String _dis");
        DataBaseUtil.insertEntity(codeBase);
    }
}
