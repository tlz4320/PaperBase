package Sql;

import util.DBField;

import java.awt.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class CodeBase {
    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getPath() {
        return Path;
    }

    public void setPath(String path) {
        Path = path;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String discription) {
        Description = discription;
    }

    public String getClassList() {
        return Class;
    }

    public void setClass(String discription) {
        Class = discription;
    }
    public String getTag() {
        return Tag;
    }

    public void setTag(String t) {
        Tag = t;
    }
    @Override
    public int hashCode() {
        return (int)ID;
    }

    @DBField
    public long ID;
    @DBField
    public String FileName;
    @DBField
    public String Path;
    @DBField()
    public String Time;
    @DBField
    public String Description;
    @DBField
    public String Class;
    @DBField
    public String Tag;
    public CodeBase(long _ID, String _FileName, String _Path) {
        ID = _ID;
        FileName = _FileName;
        Path = _Path;
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        Time = sdf.format(new Date());
        Description = "123";
    }

    public CodeBase(long _ID, String _filename, String _path, String _date, String _dis) {
        ID = _ID;
        FileName = _filename;
        Path = _path;
        Time = _date;
        Description = _dis;
    }
    public CodeBase(long _ID, String _filename, String _path, String _date, String _dis, String _tag, String _class) {
        ID = _ID;
        FileName = _filename;
        Path = _path;
        Time = _date;
        Description = _dis;
        Tag = _tag;
        Class = _class;
    }
    public CodeBase() {
    }

    public String[] getRow() {
        String[] res = new String[7];
        res[0] = "" + ID;
        res[1] = FileName;
        res[2] = Path;
        res[3] = Time;
        res[4] = Description;
        res[5] = Tag;
        res[6] = Class;
        return res;
    }
}
