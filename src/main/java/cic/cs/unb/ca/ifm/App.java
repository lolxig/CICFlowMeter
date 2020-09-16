package cic.cs.unb.ca.ifm;

import cic.cs.unb.ca.flow.FlowMgr;
import cic.cs.unb.ca.guava.GuavaMgr;
import cic.cs.unb.ca.ifm.ui.MainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class App {
    public static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void init() {
        FlowMgr.getInstance().init();       //初始化data目录与保存目录
        GuavaMgr.getInstance().init();      //初始化事件管理器
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
		/*try {
			//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}*/

        EventQueue.invokeLater(() -> {
            try {
                init();    //系统初始化
                new MainFrame();    //初始化界面
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        });
    }
}
