package Gui;

import Sql.CodeBase;
import Sql.Sql;
import cn.treeh.ToNX.O;
import util.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

public class CBGui {
    public JTable ResTable;
    public JPanel MPanel;
    public JButton Query;
    public JButton Add;
    public JButton Del;
    public JTextArea Querytext;
    public JButton Update;
    private JButton ExQuery;


    public CBGui() {
        Add.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                super.mouseClicked(e);
                AddGui addGui = new AddGui(configuration);
                addGui.pack();
                addGui.setVisible(true);
                if(!addGui.valid){
                    int res = JOptionPane.showConfirmDialog(null,
                            "输入内容不全，是否继续保存(可能会产生错误)",
                            "是否继续", JOptionPane.YES_NO_OPTION);
                    if(res == JOptionPane.YES_OPTION){
                        addGui.valid = true;
                    }
                }
                if(addGui.valid){
                    if(Sql.checkExist(addGui.selected_file, addGui.description, addGui.datePicker.getDate().toString())){
                        int res = JOptionPane.showConfirmDialog(null,
                                "文件错误或者已经有相同名称的文件(夹)，是否继续",
                                "是否继续", JOptionPane.YES_NO_OPTION);
                        if(res == JOptionPane.NO_OPTION){
                            return;
                        }
                    }
                    StringBuilder tag = new StringBuilder();
                    for(String t : addGui.tags.values()){
                        if(t == null || t.trim().length() == 0)
                            continue;
                        if(!Sql.containsTag(t))
                            Sql.addTag(t);
                        tag.append(t + ",");
                    }
                    if(tag.length() > 0)
                        tag.deleteCharAt(tag.length() - 1);
                    StringBuilder classs = new StringBuilder();
                    for(String t : addGui.classs.values()){
                        if(t == null || t.trim().length() == 0)
                            continue;
                        if(!Sql.containsClass(t))
                            Sql.addClass(t);
                        classs.append(t + ",");
                    }
                    if(classs.length() > 0)
                        classs.deleteCharAt(classs.length() - 1);
                    CodeBase added = Sql.addEntity(addGui.selected_file, addGui.description, addGui.datePicker.getDate().toString(),
                            tag.toString(), classs.toString());
                    if(added != null) {
                        JOptionPane.showConfirmDialog(null,
                                "新增成功",
                                "成功", JOptionPane.DEFAULT_OPTION);
                        ((DefaultTableModel) ResTable.getModel()).addRow(added.getRow());
                    }
                    else
                        JOptionPane.showConfirmDialog(null,
                                "奇怪的事情发生了",
                                "成功", JOptionPane.DEFAULT_OPTION);
                }
            }
        });
        Query.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                super.mouseClicked(e);
                String query = Querytext.getText();
                ((DefaultTableModel)ResTable.getModel()).setRowCount(0);
                if(query == null || query.length() < 1){
                    List<CodeBase> query_result = Sql.getFirst100();
                    for(CodeBase codeBase : query_result)
                        ((DefaultTableModel)ResTable.getModel()).addRow(codeBase.getRow());
                }
                else{
                    query = query.replaceAll("'", "");
                    if(query.length() < 4){
                        int res = JOptionPane.showConfirmDialog(null,
                                "查询条件过于简单，结果易不准确，是够继续",
                                "是否继续", JOptionPane.YES_NO_OPTION);
                        if(res == JOptionPane.NO_OPTION)
                            return;
                    }
                    String timeformat = Utils.isDateFormat(query);
                    List<CodeBase> query_result1 = null;
                    if(timeformat != null){
                        query_result1 = Sql.fuzzy_query(timeformat);
                    }
                    List<CodeBase> query_result2 = Sql.fuzzy_query(query);
                    if(query_result1 == null || query_result1.isEmpty()){
                        for(CodeBase codeBase : query_result2)
                            ((DefaultTableModel)ResTable.getModel()).addRow(codeBase.getRow());
                    }
                    else {
                        HashSet<CodeBase> hashSet = new HashSet<CodeBase>();
                        hashSet.addAll(query_result2);
                        hashSet.addAll(query_result1);
                        for(CodeBase codeBase : hashSet)
                            ((DefaultTableModel)ResTable.getModel()).addRow(codeBase.getRow());
                    }

                }
            }
        });
        ResTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                super.mouseClicked(e);
                if(e.getClickCount() > 1){
                    int row = ResTable.getSelectedRow();
                    if(row < 0)
                        return;
                    String value = (String)((DefaultTableModel)ResTable.getModel()).getValueAt(row,2);
                    File file = new File(value);
                    if(file.exists()){
                        try {
                            if (file.isDirectory())
                                Runtime.getRuntime().exec("explorer.exe " + file.getAbsolutePath());
                            else
                                Runtime.getRuntime().exec("explorer.exe " + file.getParent());
                        } catch (IOException ex) {
                                ex.printStackTrace();
                        }
                    }
                    else{
                        JOptionPane.showConfirmDialog(null,
                                "文件不存在，请检查文件位置",
                                "文件不存在", JOptionPane.DEFAULT_OPTION);
                    }
                    O.ptln(value);
                }
            }
        });
        Del.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                mouseClicked(e);

                super.mouseClicked(e);
                int row = ResTable.getSelectedRow();
                if(row < 0)
                    return;
                String ID = (String)((DefaultTableModel)ResTable.getModel()).getValueAt(row,0);
                String filename = (String)((DefaultTableModel)ResTable.getModel()).getValueAt(row,1);
                int ask = JOptionPane.showConfirmDialog(null,
                        "确定删除文件(夹):"+filename+"?(此操作不影响真实文件)",
                        "是否继续", JOptionPane.YES_NO_OPTION);
                if(ask == JOptionPane.NO_OPTION)
                    return;
                if(Sql.delEntity(Long.parseLong(ID))) {
                    JOptionPane.showConfirmDialog(null,
                            "操作成功",
                            "操作成功", JOptionPane.DEFAULT_OPTION);
                    ((DefaultTableModel)ResTable.getModel()).removeRow(row);
                }
                else
                    JOptionPane.showConfirmDialog(null,
                            "操作失败",
                            "操作失败", JOptionPane.DEFAULT_OPTION);
            }
        });
        Update.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                super.mouseClicked(e);
                int row = ResTable.getSelectedRow();
                if(row < 0)
                    return;
                DefaultTableModel model = (DefaultTableModel)ResTable.getModel();
                String filePath = (String)model.getValueAt(row, 2);
                String time = (String)model.getValueAt(row, 3);
                String description =  (String)model.getValueAt(row, 4);
                String tags = (String) model.getValueAt(row, 5);
                String c = (String) model.getValueAt(row, 6);
                AddGui addGui = new AddGui(filePath, time, description,tags, c, configuration);
                addGui.pack();
                addGui.setVisible(true);
                if(!addGui.valid)
                    return;
                StringBuilder tag = new StringBuilder();
                for(String t : addGui.tags.values()){
                    if(t == null || t.trim().length() == 0)
                        continue;
                    if(!Sql.containsTag(t))
                        Sql.addTag(t);
                    tag.append(t + ",");
                }
                if(tag.length() > 0)
                    tag.deleteCharAt(tag.length() - 1);
                StringBuilder classs = new StringBuilder();
                for(String t : addGui.classs.values()){
                    if(t == null || t.trim().length() == 0)
                        continue;
                    if(!Sql.containsClass(t))
                        Sql.addClass(t);
                    classs.append(t + ",");
                }
                if(classs.length() > 0)
                    classs.deleteCharAt(classs.length() - 1);
                if(addGui.selected_file.getPath().equals(filePath) && addGui.description.equals(description) &&
                classs.toString().equals(c) && tag.toString().equals(tags)){
                    int ask = JOptionPane.showConfirmDialog(null,
                            "未更改有效信息，是否继续",
                            "是否继续", JOptionPane.YES_NO_OPTION);
                    if(ask == JOptionPane.NO_OPTION)
                        return;
                }
                CodeBase codeBase = Sql.updateEntity(addGui.selected_file, addGui.description,
                        addGui.datePicker.getDate().toString(),
                        Long.parseLong((String)model.getValueAt(row, 0)), tag.toString(), classs.toString());

                if(codeBase != null){
                    JOptionPane.showConfirmDialog(null,
                            "更新成功",
                            "操作成功", JOptionPane.DEFAULT_OPTION);
                    String[] newvalues = codeBase.getRow();
                    for(int index = 0; index < newvalues.length; index++)
                        model.setValueAt(newvalues[index], row, index);
                }
            }
        });
        CBGui parent = this;
        ExQuery.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                super.mouseClicked(e);
                Exquery exquery = new Exquery(parent);
                exquery.pack();
                exquery.setVisible(true);
            }
        });
    }
    Properties configuration;
    public CBGui(Properties conf){
        this();
        if(conf == null)
            conf = new Properties();
        configuration = conf;
    }

    public String reFormat(String tip, int length){
        if(tip == null || tip.length() <= length)
            return tip;
        StringBuilder builder = new StringBuilder("<html><b>");
        int index = 0;
        boolean quote = false, liquote = false;
        for(char c : tip.toCharArray()) {
            builder.append(c);
            index++;
            if (c == '"' || c == '“' || c == '”')
                quote = !quote;
            if (c == '\'' || c == '‘' || c == '’')
                liquote = !liquote;
            if (index >= length) {
                if ((quote || liquote) && index < 1.2 * length)
                    continue;
                if ((" ,|!@#$%^&*\\`-'+=_~/…！？?").contains("" + c)) {
                    index = 0;
                    builder.append("<br>");
                } else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                } else {
                    index = 0;
                    builder.append("<br>");
                }
            }
        }
        builder.append("</b></html>");
        return builder.toString();
    }
    private void createUIComponents() {
        // TODO: place custom component creation code here

        ResTable = new JTable(){
            @Override
            public String getToolTipText(MouseEvent event) {
                String tip = null;
                Point point = event.getPoint();
                int row = rowAtPoint(point);
                int col = columnAtPoint(point);
                if(row < 0 || col < 0)
                    return "";
                String value = (String) getModel().getValueAt(row, convertColumnIndexToModel(col));
                return reFormat(value, 20);
//                return super.getToolTipText(event);
            }
        };
        ResTable.setModel(new DefaultTableModel());
        ((DefaultTableModel)ResTable.getModel()).setColumnIdentifiers(new String[]{"ID", "FileName", "FilePath", "Date", "Description", "Tags", "Class"});
//        ResTable.setDefaultRenderer(Object.class, new MTableRender());
        ResTable.setDefaultEditor(Object.class, null);
        ResTable.getColumnModel().removeColumn(ResTable.getColumnModel().getColumn(0));
    }
}
