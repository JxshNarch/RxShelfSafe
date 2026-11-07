package panels;

import database.DataStore;
import frame.MainFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import model.Account;
import popups.AccountPopup;

public class AccountPanel extends JPanel implements ActionListener {

    private DataStore data;
    private MainFrame main;
    private DefaultTableModel model;
    private JTable table;

    private JButton addButton, editButton, removeButton;

    //colors









    private Color pageGray = new Color(230, 230, 230);
    private Color barGray = new Color(217, 217, 217);
    private Color selectColor = new Color(200, 225, 250);

    public AccountPanel(DataStore data, MainFrame main) {

        this.data = data;
        this.main = main;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));












        //tile 
        JLabel title = new JLabel("MANAGE ACCOUNTS");
        title.setFont(new Font("Arial", Font.BOLD, 28));

        JLabel subtitle = new JLabel("Manage system users and their access roles");
        subtitle.setFont(new Font("Arial", Font.BOLD, 16));
        subtitle.setForeground(new Color(130, 130, 130));

        JPanel top = new JPanel(new BorderLayout());

        top.add(title, BorderLayout.NORTH);
        top.add(subtitle, BorderLayout.CENTER);










        //button
        addButton = makeButton("+ Add Account", Color.BLACK, Color.WHITE, 160);

       editButton = makeButton("Edit", barGray, Color.BLACK, 90);

      JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));

        buttons.add(addButton);
        buttons.add(editButton);

       top.add(buttons, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
















        // table
        model = new DefaultTableModel(
                new String[]{"Username", "Role"}, 0
        );

        table = new JTable(model);

        table.setDefaultEditor(Object.class, null);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setRowHeight(40);
        table.setIntercellSpacing(new Dimension(0, 8));
        table.setShowGrid(false);
        table.setBackground(pageGray);
        table.setFillsViewportHeight(true);

        table.setDefaultRenderer(Object.class, new RowRenderer());

        // 
        
        
        
        
        
        
        
        
        
        
        
        //table headers


        JTableHeader header = table.getTableHeader();

        header.setDefaultRenderer(new HeaderRenderer());
        header.setPreferredSize(new Dimension(100, 45));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBackground(pageGray);

        //
        JPanel headerHolder = new JPanel(new BorderLayout());

        headerHolder.setBackground(pageGray);
        headerHolder.setBorder(
                BorderFactory.createEmptyBorder(0, 15, 0, 15)
        );

        headerHolder.add(header, BorderLayout.CENTER);









        //scroll
        JScrollPane scroll = new JScrollPane(table);

        scroll.setColumnHeaderView(headerHolder);

        scroll.setViewportBorder(
                BorderFactory.createEmptyBorder(0, 15, 0, 15)
        );

        scroll.setBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2)
        );

        scroll.getViewport().setBackground(pageGray);

        scroll.getVerticalScrollBar().setUnitIncrement(20);

        add(scroll, BorderLayout.CENTER);

        //remove button
        removeButton = makeButton(
                "Delete Account", barGray, Color.BLACK, 160
        );

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));

        bottom.add(removeButton);

        add(bottom, BorderLayout.SOUTH);
    }









   
    private JButton makeButton(String text, Color bg, Color fg, int width) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setPreferredSize(new Dimension(width, 35));

        button.setBackground(bg);
        button.setForeground(fg);

        button.setBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2)
        );

        button.setFocusPainted(false);
        button.addActionListener(this);

        return button;
    }

    //row
    private class RowRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {

            super.getTableCellRendererComponent(
                    t, value, isSelected, false, row, column
            );

            setHorizontalAlignment(CENTER);
            setFont(new Font("Arial", Font.PLAIN, 16));
            setForeground(Color.BLACK);

            if (isSelected) {

                setBackground(selectColor);

                if (column == 0) {
                    setBorder(BorderFactory.createMatteBorder(
                            2, 2, 2, 0, Color.BLACK
                    ));
                } else {
                    setBorder(BorderFactory.createMatteBorder(
                            2, 0, 2, 2, Color.BLACK
                    ));
                }

            } else {

                setBackground(barGray);
                setBorder(BorderFactory.createEmptyBorder());

            }

            return this;
        }
    }

    // header style
    private class HeaderRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable t, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {

            super.getTableCellRendererComponent(
                    t, value, false, false, row, column
            );

            setHorizontalAlignment(CENTER);
            setFont(new Font("Arial", Font.BOLD, 18));
            setForeground(Color.BLACK);
            setBackground(pageGray);
            setBorder(BorderFactory.createEmptyBorder());

            return this;
        }
    }

    //refresg
    public void refresh() {

        model.setRowCount(0);

        for (Account a : data.getAccounts()) {

            model.addRow(new Object[]{
                    a.getUsername(),
                    a.getRole()
            });
        }
    }









    //autton action
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == addButton) {

            openAddDialog();

        } else if (e.getSource() == editButton) {

            openEditDialog();

        } else if (e.getSource() == removeButton) {

            removeAccount();
        }
    }








    // add acco
    private void openAddDialog() {

        AccountPopup dialog = new AccountPopup(main, "Add", null, null);
        dialog.setVisible(true);

        if (dialog.isSaved()) {

            String username = dialog.getNameValue();
            String password = dialog.getPasswordValue();
            String role = dialog.getRoleValue();

            for (Account a : data.getAccounts()) {

                if (a.getUsername().equalsIgnoreCase(username)) {

                    JOptionPane.showMessageDialog(this,
                            "That username is already used.");
                    return;
                }
            }

            data.getAccounts().add(new Account(username, password, role));

            main.addLog("ACCOUNT: " + role + " account \"" +
                    username + "\" was created.");

            data.save();
            main.refreshAll();
        }
    }

    // edit
    private void openEditDialog() {

        int row = table.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(this,
                    "Please select an account first.");
            return;
        }

        Account selected = data.getAccounts().get(row);

        AccountPopup dialog = new AccountPopup(main, "Edit",
                selected.getUsername(), selected.getRole());

        dialog.setVisible(true);

        if (dialog.isSaved()) {

            String newName = dialog.getNameValue();
            String newPass = dialog.getPasswordValue();
            String newRole = dialog.getRoleValue();

            for (int i = 0; i < data.getAccounts().size(); i++) {

                if (i != row && data.getAccounts().get(i)
                        .getUsername().equalsIgnoreCase(newName)) {

                    JOptionPane.showMessageDialog(this,
                            "That username is already used.");
                    return;
                }
            }

            selected.setUsername(newName);
            selected.setPassword(newPass);
            selected.setRole(newRole);

            main.addLog("ACCOUNT: account \"" +
                    newName + "\" was updated.");

            data.save();
            main.refreshAll();
        }
    }

    //delete
    private void removeAccount() {

        int row = table.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(this,
                    "Please click an account in the table first.");
            return;
        }

        Account a = data.getAccounts().get(row);

        if (a.getUsername().equalsIgnoreCase(
                main.getUser().getUsername())) {

            JOptionPane.showMessageDialog(this,
                    "You cannot remove the account you are using.");
            return;
        }

        if (a.getRole().equals("ADMIN") &&
                data.countRole("ADMIN") <= 1) {

            JOptionPane.showMessageDialog(this,
                    "There must be at least one admin.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this,
                "Remove the account \"" + a.getUsername() + "\"?",
                "Confirm", JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {

            data.getAccounts().remove(row);

            main.addLog("ACCOUNT: account \"" +
                    a.getUsername() + "\" was removed.");

            data.save();
            main.refreshAll();
        }
    }
}
