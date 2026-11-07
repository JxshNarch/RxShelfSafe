package frame;
import database.DataStore;
import database.FileFinder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import model.Account;
import panels.AccountPanel;
import panels.BatchPanel;
import panels.CategoryPanel;
import panels.DashboardPanel;
import panels.RemovalPanel;
import panels.ReportPanel;







public class MainFrame extends JFrame implements ActionListener {

    private DataStore data;
    private Account user;

    private CardLayout cards;          // shows one screen at a time
    private JPanel center;
    private JTextArea logArea;

    private JButton dashboardButton, batchesButton, removalButton,
            reportButton, categoryButton, accountButton, logoutButton;

    private JPanel menu;             
    private JPanel left;              

   
    private JButton selectedButton;
  

    // the screens
    private DashboardPanel dashboardPanel;
    private BatchPanel inventoryPanel;
    private RemovalPanel removalPanel;
    private ReportPanel reportPanel;
    private CategoryPanel categoryPanel;
    private AccountPanel accountPanel;     // only for the admin

    public MainFrame(DataStore data, Account user) {
        this.data = data;
        this.user = user;

        setTitle("RxShelfSafe - The Liezle Pharmacy");

        setSize(900, 650);       //screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String panelName = "STAFF PANEL";
        if (isAdmin()) {
            panelName = "ADMIN PANEL";        

        }
        menu = new JPanel(new GridLayout(0, 1, 5, 5));


        JLabel panelLabel = new JLabel(panelName, JLabel.CENTER);
panelLabel.setFont(new Font("open sans", Font.BOLD, 20));
panelLabel.setForeground(new Color(0, 0, 0));







menu.add(panelLabel);




ImageIcon profileIcon = new ImageIcon(FileFinder.find("user.png"));
Image smallIcon = profileIcon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
JLabel userLabel = new JLabel(user.getUsername(), new ImageIcon(smallIcon), JLabel.CENTER);
userLabel.setIconTextGap(8);               // space between the icon and the name










userLabel.setFont(new Font("Arial", Font.BOLD, 16));
menu.add(userLabel);



               dashboardButton = makeMenuButton("Dashboard", "dashboard.png", menu);
        batchesButton = makeMenuButton("Manage Batches", "batches.png", menu);
        removalButton = makeMenuButton("Removal Queue", "removal.png", menu);
        reportButton = makeMenuButton("Expiry Report", "expiry.png", menu);
        categoryButton = makeMenuButton("Categories", "category.png", menu);
        if (isAdmin()) {
            accountButton = makeMenuButton("Accounts", "account.png", menu);
        
        }




        
        logoutButton = new JButton("Log Out");
        logoutButton.addActionListener(this);

        logoutButton.setFont(new Font("Arial", Font.BOLD, 16));
        logoutButton.setPreferredSize(new Dimension(200, 45));
        logoutButton.setContentAreaFilled(true);
        logoutButton.setFocusPainted(true);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        logoutButton.setBackground(new Color(255, 255, 255));



  
























      

        left = new JPanel(new BorderLayout());
        left.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        left.add(menu, BorderLayout.NORTH);
        left.add(logoutButton, BorderLayout.SOUTH);
        add(left, BorderLayout.WEST);

       
        
        menu.setBackground(Color.WHITE);

       left.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 2, 0, 2, Color.BLACK),     // top, left, bottom, right thickness
        BorderFactory.createEmptyBorder(10, 10, 10, 10)));           
          

        menu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    

        //menu


        
        cards = new CardLayout();
        center = new JPanel(cards);
        dashboardPanel = new DashboardPanel(data);
        inventoryPanel = new BatchPanel(data, this);
        removalPanel = new RemovalPanel(data, this);
        reportPanel = new ReportPanel(data, this);
        categoryPanel = new CategoryPanel(data, this);
        center.add(dashboardPanel, "dashboard");
        center.add(inventoryPanel, "batches");
        center.add(removalPanel, "removal");
        center.add(reportPanel, "report");
        center.add(categoryPanel, "categories");
        if (isAdmin()) {
            accountPanel = new AccountPanel(data, this);
            center.add(accountPanel, "accounts");
        }
        /////
        /// 
        /// 
                // the system log (under the screens only, not under the sidebar)
        logArea = new JTextArea(6, 20);
        logArea.setEditable(false);
        logArea.setBackground(new Color(245, 245, 245));
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));    // space inside the box

        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));  // the black outline

        JLabel logTitle = new JLabel("SYSTEM LOG");

        JPanel logPanel = new JPanel(new BorderLayout(0, 5));
        logPanel.setBackground(new Color(230, 230, 230));
        logPanel.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        logPanel.add(logTitle, BorderLayout.NORTH);
        logPanel.add(logScroll, BorderLayout.CENTER);

        // the right side 
        JPanel right = new JPanel(new BorderLayout());
        right.add(center, BorderLayout.CENTER);
        right.add(logPanel, BorderLayout.SOUTH);
        add(right, BorderLayout.CENTER);




        refreshAll();

      
        highlight(dashboardButton);
       
    }

    //buttons
        // make one button for the menu icon on the left side
    private JButton makeMenuButton(String text, String iconFile, JPanel menu) {
        JButton button = new JButton(text);

        //buttons style
        button.setFont(new Font("Arial", Font.PLAIN, 14));                
        button.setPreferredSize(new Dimension(250, 35));                  
        button.setContentAreaFilled(false);                              
        button.setFocusPainted(false);                                  
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 8));    

        // the icon: loaded with FileFinder and made 20 x 20 (change the 20s to change the size)
        ImageIcon icon = new ImageIcon(FileFinder.find(iconFile));
        button.setIcon(new ImageIcon(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        button.setIconTextGap(12);                                      // space between the icon and the text
        button.setHorizontalAlignment(SwingConstants.LEFT);             // icon and text start at the left

        button.addActionListener(this);
        menu.add(button);
        return button;
    
    }






   
   
    private void highlight(JButton button) {
        if (selectedButton != null) {
            selectedButton.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        }
        selectedButton = button;
       

        selectedButton.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.BLACK, 2),
        BorderFactory.createEmptyBorder(6, 30, 6, 6)));
  
           
    }
   

    public boolean isAdmin() {
        return user.getRole().equals("ADMIN");
    }

    public Account getUser() {
        return user;
    }

    //system log
    public void addLog(String message) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
        logArea.append("[" + time + "] " + message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());   // scroll to the bottom
    }

    //refresh
    public void refreshAll() {
        dashboardPanel.refresh();
        inventoryPanel.refresh();
        removalPanel.refresh();
        reportPanel.refresh();
        categoryPanel.refresh();
        if (accountPanel != null) {
            accountPanel.refresh();
        }
    }

    // show one screen
    public void showPanel(String name) {
        refreshAll();
        cards.show(center, name);
    }

    //warnind
    public void showStartupAlert() {
        int expired = data.getBatches().countStatus("Expired");
        int near = data.getBatches().countStatus("Near expiry");
        if (expired + near > 0) {
            JOptionPane.showMessageDialog(this,
                    "Expiration alert:\n" + expired + " expired batch(es)\n" + near
                    + " near-expiry batch(es)\n\nOpen the Expiry Report to see them.",
                    "Expiration Alerts", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void actionPerformed(ActionEvent e) {
       

        
        
        if (e.getSource() != logoutButton) {
            highlight((JButton) e.getSource());
        }
    

        if (e.getSource() == dashboardButton) {
            showPanel("dashboard");
        } else if (e.getSource() == batchesButton) {
            showPanel("batches");
        } else if (e.getSource() == removalButton) {
            showPanel("removal");
        } else if (e.getSource() == reportButton) {
            showPanel("report");
        } else if (e.getSource() == categoryButton) {
            showPanel("categories");
        } else if (e.getSource() == accountButton) {
            showPanel("accounts");
       } else if (e.getSource() == logoutButton) {
    int answer = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to log out?",
            "Log Out", JOptionPane.YES_NO_OPTION);
    if (answer == JOptionPane.YES_OPTION) {
        data.save();
        dispose();
        new LoginScreen(data).setVisible(true);
    }
}
        }
    }
