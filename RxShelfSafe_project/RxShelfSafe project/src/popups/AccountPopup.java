package popups;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import database.DataStore;
import model.Account;



//da window that pups up when you add or edit an account











public class AccountPopup extends JDialog implements ActionListener {

    private JTextField nameField;
    private JPasswordField passField;
    private JComboBox<String> roleBox;

    private JButton saveButton, cancelButton;

    private boolean saved = false;

    private String resultName;
    private String resultPassword;
    private String resultRole;

    public AccountPopup(JFrame owner, String mode,
                         String existingName, String existingRole) {

        super(owner, true);

        if (mode.equals("Edit")) {
            setTitle("Edit Account");
        } else {
            setTitle("Add Account");
        }

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        //input
        nameField = new JTextField();
        passField = new JPasswordField();

        roleBox = new JComboBox<String>(
                new String[]{"Admin", "Staff"}
        );

        //
        if (mode.equals("Edit") && existingName != null) {

            nameField.setText(existingName);

            if (existingRole != null) {

                if (existingRole.equalsIgnoreCase("ADMIN")) {
                    roleBox.setSelectedItem("Admin");
                } else {
                    roleBox.setSelectedItem("Staff");
                }
            }
        }

        //style
        JPanel form = new JPanel(new GridLayout(3, 1, 10, 10));

        form.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 5, 15)
        );

        form.add(makeRow("Username", nameField, 300, 40));
        form.add(makeRow("Password", passField, 300, 40));
        form.add(makeRow("Role", roleBox, 300, 40));

        //buttons ni
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        buttons.add(cancelButton);
        buttons.add(saveButton);

        //layout
        setLayout(new BorderLayout());

        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        styleWindow(form, buttons);

        setSize(595, 300);
        setResizable(false);
        setLocationRelativeTo(owner);
    }












    //row
    private JPanel makeRow(String text, Component box,
                           int width, int height) {

        JLabel label = new JLabel(text);

        label.setPreferredSize(
                new Dimension(250, height)
        );

        box.setPreferredSize(
                new Dimension(width, height)
        );

        JPanel row = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        row.setBackground(Color.WHITE);

        row.add(label);
        row.add(box);

        return row;
    }

    //style agian
    private void styleWindow(JPanel form, JPanel buttons) {

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);

        //labels and input 
        for (Component row : form.getComponents()) {

            for (Component c : ((JPanel) row).getComponents()) {

                if (c instanceof JLabel) {

                    c.setFont(labelFont);

                } else if (c instanceof JTextField) {

                    c.setFont(fieldFont);

                    ((JTextField) c).setBorder(
                            BorderFactory.createCompoundBorder(
                                    BorderFactory.createLineBorder(
                                            Color.BLACK, 2
                                    ),
                                    BorderFactory.createEmptyBorder(
                                            0, 8, 0, 8
                                    )
                            )
                    );

                } else if (c instanceof JComboBox) {

                    c.setFont(fieldFont);
                    c.setBackground(Color.WHITE);

                    ((JComboBox<?>) c).setBorder(
                            BorderFactory.createLineBorder(
                                    Color.BLACK, 2
                            )
                    );
                }
            }
        }

        //save butt
        saveButton.setFont(fieldFont);
        saveButton.setPreferredSize(new Dimension(100, 30));
        saveButton.setBackground(Color.BLACK);
        saveButton.setForeground(Color.WHITE);

        saveButton.setBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2)
        );

        saveButton.setFocusPainted(false);

        //cancel butt
        cancelButton.setFont(fieldFont);
        cancelButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.setBackground(new Color(190, 190, 190));
        cancelButton.setForeground(Color.BLACK);

        cancelButton.setBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2)
        );

        cancelButton.setFocusPainted(false);

        //background
        form.setBackground(Color.WHITE);
        buttons.setBackground(Color.WHITE);

        buttons.setBorder(
                BorderFactory.createEmptyBorder(0, 0, 25, 25)
        );
    }

    //button actions
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == cancelButton) {

            saved = false;
            dispose();
            return;
        }

        if (e.getSource() == saveButton) {

            String name = nameField.getText().trim();
            String pass = new String(passField.getPassword());

            if (name.length() < 3) {

                JOptionPane.showMessageDialog(this,
                        "Username must have at least 3 letters.");
                return;
            }

            if (pass.length() < 4) {

                JOptionPane.showMessageDialog(this,
                        "Password must have at least 4 characters.");
                return;
            }

            resultName = name;
            resultPassword = pass;

            resultRole = roleBox.getSelectedItem()
                    .toString().toUpperCase();

            saved = true;

            dispose();
        }
    }

    //getters
    public boolean isSaved() {
        return saved;
    }

    public String getNameValue() {
        return resultName;
    }

    public String getPasswordValue() {
        return resultPassword;
    }

    public String getRoleValue() {
        return resultRole;
    }
}
