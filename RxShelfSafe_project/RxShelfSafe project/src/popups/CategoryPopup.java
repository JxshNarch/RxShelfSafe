package popups;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import database.DataStore;










// The small window for adding a category or renaming one.
//
public class CategoryPopup extends JDialog implements ActionListener {

    private DataStore data;
    private String oldName;
    private boolean saved = false;       

    private JTextField nameField;
    private JButton okButton, cancelButton;

    public CategoryPopup(JFrame owner, DataStore data, String oldName) {
        super(owner, true);              // 
        this.data = data;
        this.oldName = oldName;

        if (oldName == null) {
            setTitle("Add Category");
        } else {
            setTitle("Edit Category");
        }

        // the label and the box 






        JLabel label = new JLabel("Name");
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setPreferredSize(new Dimension(70, 40));

        nameField = new JTextField();
        nameField.setPreferredSize(new Dimension(260, 40));
        nameField.setFont(new Font("Arial", Font.PLAIN, 16));
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        nameField.addActionListener(this);                 // Enter works
        if (oldName != null) {
            nameField.setText(oldName);
            nameField.selectAll();
        }

        JPanel form = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(30, 20, 10, 20));
        form.add(label);
        form.add(nameField);

        //da buttons
        String okText = "+ Add";
        if (oldName != null) {
            okText = "Save";
        }
        okButton = new JButton(okText);
        okButton.setFont(new Font("Arial", Font.PLAIN, 16));
        okButton.setPreferredSize(new Dimension(120, 35));
        okButton.setBackground(Color.BLACK);
        okButton.setForeground(Color.WHITE);
        okButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        okButton.setFocusPainted(false);
        okButton.addActionListener(this);

        cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 16));
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.setBackground(new Color(190, 190, 190));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(this);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        buttons.add(cancelButton);
        buttons.add(okButton);

        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(okButton);
        setSize(460, 230);                                
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    public boolean isSaved() {
        return saved;
    }





    // the name the user typed
    public String getCategoryName() {
        return nameField.getText().trim();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
            dispose();
            return;
        }

        // the button or enter is used
        String name = nameField.getText().trim();
        if (name.equals("")) {
            JOptionPane.showMessageDialog(this, "Please type a category name.",
                    "Check your input", JOptionPane.ERROR_MESSAGE);
            return;
        }
        for (int i = 0; i < data.getCategories().size(); i++) {
            String other = data.getCategories().get(i);
            // the category that is being edited may keep its own name (or only change the letter case)
            boolean sameAsEdited = (oldName != null && other.equals(oldName));
            if (other.equalsIgnoreCase(name) && !sameAsEdited) {
                JOptionPane.showMessageDialog(this, "That category already exists.",
                        "Check your input", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        saved = true;
        dispose();
    }
}