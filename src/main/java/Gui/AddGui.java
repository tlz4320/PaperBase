package Gui;

import Sql.Class;
import Sql.Sql;
import Sql.Tag;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

public class AddGui extends JDialog {
    public JPanel contentPane;
    public JButton buttonOK;
    public JButton buttonCancel;
    public JTextField FilePath;
    public JButton Direction;
    public JTextArea Description;
    public JPanel DatePickerPanel;
    private JButton AddClass;
    private JButton AddTag;
    private JPanel TagList;
    private JPanel ClassList;
    public DatePicker datePicker;// = new DatePicker();

    public HashMap<Integer, String> tags;

    public HashMap<Integer, String> classs;
    static class MComboBox extends JComboBox<String>{
        int id;
        HashMap<Integer, String> l;
        public MComboBox(int id, HashMap<Integer, String> l){
            super();
            this.id = id;
            this.l = l;
            setEditable(true);
            addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    System.out.println("test");
                }

                @Override
                public void focusLost(FocusEvent e) {
                    setSelectedItem(getEditor().getItem());
                }
            });
            addItemListener(e -> {
                int stateChange = e.getStateChange();// 获得事件类型
                String item = e.getItem().toString();// 获得触发此次事件的选项
                if (stateChange == ItemEvent.SELECTED) {// 查看是否由选中选项触发
                    l.put(id, item);
                } else if (stateChange == ItemEvent.DESELECTED) {
                    l.remove(id);
                } else {// 由其他原因触发
                    System.out.println("此次事件由其他原因触发！");
                }
            });
        }
    }

    public AddGui() {
        tags = new HashMap<>();
        classs = new HashMap<>();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        JDialog dialog = this;
        Direction.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseClicked(e);
                String lastpath = configuration == null ? "." : configuration.getProperty("lastpath");
                JFileChooser chooser = new JFileChooser(lastpath == null ? "." : lastpath);
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int res = chooser.showOpenDialog(dialog);
                if(res == JFileChooser.CANCEL_OPTION)
                    return;
                FilePath.setText(chooser.getSelectedFile().getAbsolutePath());
                selected_file = chooser.getSelectedFile();
                configuration.setProperty("lastpath", selected_file.isFile() ? selected_file.getParent() : selected_file.getAbsolutePath());
            }
        });
        AddTag.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseReleased(MouseEvent e) {
                addTagBox();
            }
        });
        AddClass.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                addClassBox();
            }
        });
    }
    public void addTagBox(String s){
        JComboBox<String> mComboBox = new MComboBox(tags.size() + 1, tags);
        Collection<Tag> tgs = Sql.getAllTag();
        for(Tag t : tgs){
            mComboBox.addItem(t.getTag());
        }
        if(s != null)
            mComboBox.setSelectedItem(s);
        TagList.add(mComboBox);
        TagList.updateUI();

    }
    public void addTagBox(){
        addTagBox(null);
    }
    public void addClassBox(String s){
        JComboBox<String> mComboBox = new MComboBox(classs.size() + 1, classs);
        Collection<Class> tgs = Sql.getAllClass();
        for(Class t : tgs){
            mComboBox.addItem(t.getClassList());
        }
        if(s != null)
            mComboBox.setSelectedItem(s);
        ClassList.add(mComboBox);
        ClassList.updateUI();
    }
    public void addClassBox(){
        addClassBox(null);
    }
    Properties configuration;
    public AddGui(String Path, String time, String description, Properties properties){
        this();
        FilePath.setText(Path);
        selected_file = new File(Path);
        LocalDate localDate = LocalDate.parse(time);
        datePicker.setDate(localDate);
        Description.setText(description);
        configuration = properties;

    }
    public AddGui(String Path, String time, String description, String t, String c, Properties properties){
        this();
        FilePath.setText(Path);
        selected_file = new File(Path);
        LocalDate localDate = LocalDate.parse(time);
        datePicker.setDate(localDate);
        Description.setText(description);
        t = t == null ? "" : t;
        c = c == null ? "" : c;
        String[] tagList = t.split("[,]");
        String[] classList = c.split("[,]");
        for(String tag : tagList) {
            if(tag.length() > 0) {
                tags.put(tags.size() + 1, tag);
                addTagBox(tag);
            }
        }
        for(String tag : classList) {
            if(tag.length() > 0) {
                classs.put(classs.size() + 1, tag);
                addClassBox(tag);
            }
        }
        configuration = properties;

    }
    public AddGui(Properties properties){
        this();
        configuration = properties;
    }
    public String description;
    public File selected_file;
    public boolean valid;
    private void onOK() {
        // add your code here
        description = Description.getText();
        if(selected_file != null && description.length() != 0)
            valid = true;
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        valid = false;
        dispose();
    }

    public static void main(String[] args) {
        AddGui dialog = new AddGui();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);

    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        UIManager.put("TextField.inactiveForeground", new Color(0, 0, 0));
        datePicker = new DatePicker();
        datePicker.setDateToToday();
        datePicker.getComponentDateTextField().setEnabled(false);

        DatePickerPanel = new JPanel();
        DatePickerPanel.add(datePicker);
    }
}
