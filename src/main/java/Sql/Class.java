package Sql;

import util.DBField;

public class Class {

    public String getClassList() {
        return Class;
    }

    public void setClass(String aClass) {
        Class = aClass;
    }

    @DBField
    public String Class;
}
