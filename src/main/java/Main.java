import Gui.CBGui;
import cn.treeh.ToNX.util.PropsUtil;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.util.Properties;

public class Main {

    private static void SystemTrayInitial(JFrame frame) {
        if (!SystemTray.isSupported()) {//判断系统是否支持托盘
            return;
        }
        try {
            String title = "PaperBase";//系统栏通知标题
            SystemTray systemTray = SystemTray.getSystemTray();//获取系统默认托盘
            Image imageIcon = new ImageIcon("Rec/ICO32.png").getImage();
            TrayIcon trayIcon =
                    new TrayIcon(imageIcon, title + "\n" + "文章管理", createMenu(frame));//添加图标,标题,内容,菜单
            trayIcon.setImageAutoSize(true);//设置图像自适应
            trayIcon.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.setVisible(true);//显示当前窗口
                    frame.toFront();
                }
            });//双击打开窗口
            systemTray.add(trayIcon);//添加托盘
            trayIcon.displayMessage(title, "文章管理", TrayIcon.MessageType.INFO);//弹出一个info级别消息框
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    //托盘中的菜单
    private static PopupMenu createMenu(JFrame frame) {
        PopupMenu menu = new PopupMenu();//创建弹出式菜单
        MenuItem exitItem = new MenuItem("Exit");//创建菜单项
        exitItem.addActionListener(new ActionListener() {//给菜单项添加事件监听器，单击时退出系统
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);

            }
        });
        MenuItem openItem = new MenuItem("Open");//创建菜单项
        openItem.addActionListener(new ActionListener() {//给菜单项添加事件监听器，单击时打开系统
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(true);//显示当前窗口
                frame.toFront();
            }
        });
        menu.add(openItem);//添加打开系统菜单
        menu.addSeparator();//菜单分割符
        menu.add(exitItem);//添加退出系统菜单
        return menu;
    }
    public static void main(String[] args) {
        Properties conf = PropsUtil.loadProps("mconfig.properties");//new Properties();
        try{
            UIManager.setLookAndFeel(new javax.swing.plaf.nimbus.NimbusLookAndFeel());
        }catch (Exception e){
            e.printStackTrace();
        }
        JFrame frame = new JFrame("CBGui");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);//关闭不退出
        frame.setContentPane(new CBGui(conf).MPanel);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                PropsUtil.saveProps(conf, "mconfig.properties");
                super.windowClosing(e);
            }
        });
        Image imageIcon = new ImageIcon("Rec/ICO32.png").getImage();
        frame.setIconImage(imageIcon);
        SystemTrayInitial(frame);
        frame.setSize(500,600);
        frame.setVisible(true);


    }
}
