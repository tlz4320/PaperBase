package Gui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class MTableRender extends JTextArea implements TableCellRenderer{
    public MTableRender(){
        setLineWrap(true);
        setEditable(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable jTable, Object o, boolean b, boolean b1, int i, int i1) {
        setText(o == null ? "" : o.toString());
        return this;
    }
}
