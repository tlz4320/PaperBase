package Sql;

import util.DBField;

public class Tag {

    public String getTag() {
        return Tag;
    }

    public void setTag(String tag) {
        Tag = tag;
    }

    @DBField
    public String Tag;
}
