package Gui;

import Sql.CodeBase;
import Sql.Sql;
import util.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class Exquery extends JDialog implements Runnable{
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea Query;
    private JCheckBox allFile;
    private JCheckBox Next;
    private JProgressBar progressBar;
    private JCheckBox NoUpLow;
    private JComboBox<MEditor> TagOp;
    private JComboBox<MEditor> ClassOp;

    public Exquery() {
        setContentPane(contentPane);
//        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }
    CBGui parent;
    public Exquery(CBGui p) {
        this();
        parent = p;
    }
    private void onOK() {
        // add your code here
        new Thread(this).start();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        Exquery dialog = new Exquery();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    @Override
    public void run() {
        String query = Query.getText();
        boolean nouplow = NoUpLow.isSelected();

        if(!allFile.isSelected()){
            ArrayList<Integer> needremove = new ArrayList<>();
            int rowcount = ((DefaultTableModel)parent.ResTable.getModel()).getRowCount();
            for(int index = 0; index < rowcount; index++){
                if(!Utils.checkFile(query, (String) ((DefaultTableModel)parent.ResTable.getModel()).getValueAt(index, 2), nouplow))
                    needremove.add(index);
                progressBar.setValue(index * 100 / rowcount);
            }
            int acc = 0;//删除后 后面的行会上移
            for(int index : needremove){
                ((DefaultTableModel)parent.ResTable.getModel()).removeRow(index - acc);
                acc++;
            }
        }
        else{
            ((DefaultTableModel)parent.ResTable.getModel()).setRowCount(0);
            List<CodeBase> codeBases = Sql.getAll();
            progressBar.setValue(10);
            int index = 0;
            int rowcount = codeBases.size();
            for(CodeBase c : codeBases){
                if(Utils.checkFile(query, c.getPath(), nouplow))
                    ((DefaultTableModel)parent.ResTable.getModel()).addRow(c.getRow());
                index++;
                progressBar.setValue(10 + index * 90 / rowcount);
            }
        }
        if(Next.isSelected())
            dispose();
    }
    static class MEditor extends JButton implements ComboBoxEditor{

        public MEditor(String text) {
            super(text);
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    super.mouseClicked(e);
                    System.out.println(text);
                }
            });
        }

        @Override
        public Component getEditorComponent() {
            return this;
        }
        @Override
        public void setItem(Object anObject) {}

        @Override
        public Object getItem() {
            return null;
        }
        @Override
        public void selectAll() {}
        @Override
        public String toString(){
            return getText();
        }
    }
    static class MComboBox<E> extends JComboBox<E>{
        public MComboBox(){
            setEditable(true);
            addItemListener(e -> {
                int stateChange = e.getStateChange();// 获得事件类型
                Object item = e.getItem();// 获得触发此次事件的选项
                if (stateChange == ItemEvent.SELECTED) {// 查看是否由选中选项触发
                    setEditor((MEditor)item);
                    setSelectedItem(item);
                }
            });
        }

    }
    private void createUIComponents() {
//        UIManager.getDefaults().put("ComboBox.buttonWhenNotEditable", true);
        TagOp = new MComboBox<MEditor>();
        ClassOp = new MComboBox<MEditor>();


//        UIManager.getDefaults().put("ComboBox.buttonWhenNotEditable", false);
        TagOp.addItem(new MEditor("And Tag"));
        TagOp.addItem(new MEditor("Or Tag"));
        TagOp.setSelectedIndex(0);
        ClassOp.addItem(new MEditor("And Class"));
        ClassOp.addItem(new MEditor("Or Class"));
        ClassOp.setSelectedIndex(0);
    }
}
