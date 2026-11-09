
import database.DataStore;
import frame.LoginScreen;

import java.awt.AWTEvent;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import javax.swing.JButton;


import javax.swing.JButton;
import javax.swing.UIManager;

public class RxShelfSafe {

    public static void main(String[] args) {

UIManager.put("ScrollBarUI", "panels.ScrollBarStyle");

Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
    public void eventDispatched(AWTEvent e) {
        if (e.getSource() instanceof JButton) {
            ((JButton) e.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }
}, AWTEvent.MOUSE_EVENT_MASK);








        DataStore data = new DataStore();            //loads the saved data
        LoginScreen login = new LoginScreen(data);     // show the login window first   
        login.setVisible(true);
    }
}

// tasks 
/* fresh new gui , log-out confirmation , 
new add acount look as well as edit acount and username can be viewd on top of the panel
hightlight buttons, type shiii */

//done

/*new task
requested by staff name and cancel request removal */


/* 0 - 25%
Basic ui, Bsic button functions, Log in frame, Manage batches panel, removal queue,  code backbone
25% added expiry report, categories panel, basic buttons functions and logic
50% refined the seperate panel button functions, refined the UI, 
70% refined the ui even more added more button functions and window recolored the the UI*/