package Sql;

import util.DataBaseUtil;
import util.Utils;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Sql {
    public static class ID {
        public void setID(long id) {
            this.id = id;
        }
        public ID(){}
        public long id;
    }

    public static HashSet<Long> Ids = null;
    public static HashMap<String, Tag> tags = null;
    public static HashMap<String, Class> classList = null;
    static public HashSet<Long> getIDs(){
        String sql = "select ID from CodeBase";
        List<ID> qr = DataBaseUtil.queryEntityList(ID.class, sql);
        HashSet<Long> res = new HashSet<>();
        for(ID id : qr)
            res.add(id.id);
        return res;
    }
    static public CodeBase updateEntity(File file, String des, String date, long id){
        CodeBase entity = new CodeBase(id, file.getName(), file.getAbsolutePath(), date, des);
        return DataBaseUtil.updateEntity(entity, id) ? entity : null;
    }
    static public CodeBase updateEntity(File file, String des, String date, long id, String t, String c){
        CodeBase entity = new CodeBase(id, file.getName(), file.getAbsolutePath(), date, des, t, c);
        return DataBaseUtil.updateEntity(entity, id) ? entity : null;
    }
    static public void addEntity(String path, String des){

    }
    static public List<CodeBase> fuzzy_query(String lookingfor){
        String sql = "select * from CodeBase where FileName || Path || Time || Description || Tag || Class like '%" +
                lookingfor +
                "%' order by Time desc ";
        return DataBaseUtil.queryEntityList(CodeBase.class, sql);
    }
    static public List<CodeBase> getAll(){
        String sql = "select * from CodeBase";
        return DataBaseUtil.queryEntityList(CodeBase.class, sql);
    }
    static public Collection<Tag> getAllTag(){
        if(tags == null) {
            String sql = "select * from Tag";
            List<Tag> ts = DataBaseUtil.queryEntityList(Tag.class, sql);
            tags = new HashMap<>();
            for(Tag t : ts) {
                tags.put(t.getTag(), t);
            }
        }
        return tags.values();
    }
    static public Tag addTag(String tag){
        Tag entity = new Tag();
        entity.setTag(tag);
        if(DataBaseUtil.insertEntity(entity)){
            tags.put(tag, entity);
            return entity;
        }
        return null;
    }
    static public boolean containsTag(String t){
        return tags.containsKey(t);
    }
    static public boolean containsClass(String t){
        return classList.containsKey(t);
    }
    static public Class addClass(String c){
        Class entity = new Class();
        entity.setClass(c);
        if(DataBaseUtil.insertEntity(entity)){
            classList.put(c, entity);
            return entity;
        }
        return null;
    }
    static public Collection<Class> getAllClass() {
        if (classList == null) {
            String sql = "select * from Class";
            List<Class> cs = DataBaseUtil.queryEntityList(Class.class, sql);
            classList = new HashMap<>();
            for (Class c : cs) {
                classList.put(c.getClassList(), c);
            }
        }
        return classList.values();
    }
    static public List<CodeBase> getFirst100(){
        String sql = "select * from CodeBase order by Time desc LIMIT 50";
        return DataBaseUtil.queryEntityList(CodeBase.class, sql);
    }
    static public CodeBase addEntity(String filename, String path, String des, String date, String _tag, String _class){
        long ID = Utils.generateID(filename, path, des, date);
        CodeBase entity = new CodeBase(ID, filename, path, date, des, _tag, _class);
        if(DataBaseUtil.insertEntity(entity)){
            Ids.add(ID);
            return entity;
        }
        return null;
    }
    static public CodeBase addEntity(String filename, String path, String des, String date){
        long ID = Utils.generateID(filename, path, des, date);
        CodeBase entity = new CodeBase(ID, filename, path, date, des);
        if(DataBaseUtil.insertEntity(entity)){
            Ids.add(ID);
            return entity;
        }
        return null;
    }
    static public boolean checkExist(String path){
        if(path == null || path.length() == 0)
            return true;
        String sql = "select * from CodeBase where Path='" + path + "'";
        List<CodeBase> existed = DataBaseUtil.queryEntityList(CodeBase.class, sql);
        return !existed.isEmpty();
    }
    static public boolean checkExist(File select_file, String des, String date){
//        long ID = Utils.generateID(select_file.getName(), select_file.getAbsolutePath(), des, date);
//        String sql = "select * from CodeBase where ID='" + ID + "'";
        return select_file.exists() && checkExist(select_file.getAbsolutePath());
    }
    static public CodeBase addEntity(File select_file, String des, String date){
        return addEntity(select_file.getName(), select_file.getAbsolutePath(), des, date);
    }
    static public CodeBase addEntity(File select_file, String des, String date, String tag, String classs){
        return addEntity(select_file.getName(), select_file.getAbsolutePath(), des, date, tag, classs);
    }
    static public boolean delEntity(long id){
        boolean isremoved = DataBaseUtil.deleteEntity(CodeBase.class, id);
        if(Ids == null)
            Ids = getIDs();
        if(isremoved)
            Ids.remove(id);
        return isremoved;
    }
}
