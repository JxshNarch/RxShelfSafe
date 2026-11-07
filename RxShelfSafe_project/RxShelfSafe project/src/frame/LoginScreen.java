package frame;
import database.DataStore;
import database.FileFinder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.UIManager;
import model.Account;
 


// The login window
public class LoginScreen extends JFrame implements ActionListener {

    private DataStore data;
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;

    public LoginScreen(DataStore data) {
        this.data = data;

        setTitle("RxShelfSafe - Log In");
        setSize(900, 850);  

        
        
        // buton color
        UIManager.put("Button.select", new Color(210, 210, 210)); // Change the color to your desired color
        
        
         setSize(900, 650);       //screen                           
        setExtendedState(JFrame.MAXIMIZED_BOTH);     
        
       
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
       

        //logo and shi
        ImageIcon logoIcon = new ImageIcon(FileFinder.find("icon.png"));
        Image scaled = logoIcon.getImage().getScaledInstance(230, 200, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(scaled);
        JLabel logoLabel = new JLabel(logoIcon);

        JLabel title = new JLabel("RxShelfSafe");
        title.setFont(new Font("Arial", Font.BOLD, 26));

        JLabel subtitle = new JLabel("The Liezle Pharmacy - Drug Expiration Tracker");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        //username
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        userLabel.setForeground(Color.black);
        userLabel.setPreferredSize(new Dimension(260, 25));   
            userLabel.setMaximumSize(new Dimension(260, 25));
         userLabel.setMinimumSize(new Dimension(260, 25));
        userLabel.setHorizontalAlignment(JLabel.LEFT);        

        userField = new JTextField();
        userField.setPreferredSize(new Dimension(260, 35));
        userField.setMaximumSize(new Dimension(260, 35));     
        userField.setMinimumSize(new Dimension(260, 35));
        userField.setFont(new Font("Arial", Font.PLAIN, 16));
        userField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black, 2),    
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        userField.setBackground(Color.white);      

        //password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passLabel.setForeground(Color.black);
        passLabel.setPreferredSize(new Dimension(260, 25));
        passLabel.setMaximumSize(new Dimension(260, 25));
        passLabel.setMinimumSize(new Dimension(260, 25));
        passLabel.setHorizontalAlignment(JLabel.LEFT);
        

        passField = new JPasswordField();
        passField.setPreferredSize(new Dimension(260, 35));
        passField.setMaximumSize(new Dimension(260, 35));
        passField.setMinimumSize(new Dimension(260, 35));
        passField.setFont(new Font("Arial", Font.PLAIN, 16));
        passField.addActionListener(this);                  
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black, 2),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        passField.setBackground(Color.white);

        //log button
        loginButton = new JButton("Log In");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setPreferredSize(new Dimension(260, 40));
        loginButton.setMaximumSize(new Dimension(260, 40));
    loginButton.setMinimumSize(new Dimension(260, 40));
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(this);
        loginButton.setBackground(new Color(255, 255, 255)); 
        loginButton.setForeground(Color.black);
                   loginButton.setBorder(BorderFactory.createLineBorder(Color.black));















        //alignment
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(230, 230, 230));
        content.setBorder(BorderFactory.createEmptyBorder(100, 0, 100, 0));

        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        title.setAlignmentX(CENTER_ALIGNMENT);
        subtitle.setAlignmentX(CENTER_ALIGNMENT);
                   userLabel.setAlignmentX(CENTER_ALIGNMENT);
        userField.setAlignmentX(CENTER_ALIGNMENT);
        passLabel.setAlignmentX(CENTER_ALIGNMENT);
        passField.setAlignmentX(CENTER_ALIGNMENT);
        loginButton.setAlignmentX(CENTER_ALIGNMENT); //

        content.add(logoLabel);
        content.add(Box.createVerticalStrut(0));
        content.add(title);
        content.add(Box.createVerticalStrut(10));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(30));
        content.add(userLabel);
        content.add(Box.createVerticalStrut(0));
        content.add(userField);
        content.add(Box.createVerticalStrut(20));
        content.add(passLabel);
        content.add(Box.createVerticalStrut(0));
        content.add(passField);
            content.add(Box.createVerticalStrut(150));
        content.add(loginButton);

        add(content, BorderLayout.CENTER);









        
        //Joption pane style
        UIManager.put("OptionPane.background", Color.WHITE);                
        UIManager.put("Panel.background", Color.WHITE);                     
          UIManager.put("OptionPane.messageForeground", Color.BLACK);          
          UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 16));
        UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.BOLD, 14));
         UIManager.put("Button.background", new Color( 210, 210, 210));          
         UIManager.put("Button.foreground", Color.BLACK);
         
    }

    public void actionPerformed(ActionEvent e) {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());
        
        
        





    //look for the account in the data store  CASE SENSITIVE ni
        Account found = null;
        for (int i = 0; i < data.getAccounts().size(); i++) {
            Account a = data.getAccounts().get(i);
           if (a.getUsername().equals(username)
        && a.getPassword().equals(password)) {
    found = a;
}
        }

        if (username.isEmpty() || password.isEmpty()) {  
           JOptionPane.showMessageDialog(this, "I think you forgot to type something man.",
                    "Check your input", JOptionPane.ERROR_MESSAGE);
              return;
   }

        if (found == null) {
            JOptionPane.showMessageDialog(this, "Wrong username or password.\n \nCase sensitive.",
                    "Login failed", JOptionPane.ERROR_MESSAGE);
        } else {
            MainFrame main = new MainFrame(data, found);
            main.addLog("LOGIN: " + found.getUsername() + " logged in as " + found.getRole() + ".");
            main.setVisible(true);
            dispose();
            main.showStartupAlert();
        }
    }





}