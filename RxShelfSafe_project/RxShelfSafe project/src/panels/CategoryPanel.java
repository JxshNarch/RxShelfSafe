package panels;
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

import database.DataStore;
import frame.MainFrame;
import model.Batch;
import popups.CategoryPopup;

public class CategoryPanel extends JPanel implements ActionListener {

    private DataStore data;
    private MainFrame main;
    private DefaultTableModel model;
    private JTable table;
    private JButton addButton, editButton, deleteButton;

    private Color pageGray = new Color(245, 245, 245);          // background of the table area
    private Color barGray = new Color(217, 217, 217);           // color of one row
    private Color selectColor = new Color(200, 225, 250);       // color of the selected row

    public CategoryPanel(DataStore data, MainFrame main) {
        this.data = data;
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // top: title, the gray text under it, and the buttons
        JLabel title = new JLabel("CATEGORIES");
        title.setFont(new Font("Arial", Font.BOLD, 28));

        JLabel subtitle = new JLabel("The groups that the medicine batches belong to");   // change this text if you want
        subtitle.setFont(new Font("Arial", Font.BOLD, 16));
        subtitle.setForeground(new Color(130, 130, 130));

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.NORTH);
        top.add(subtitle, BorderLayout.CENTER);

        // only the admin can add, edit and delete a category
        if (main.isAdmin()) {
            addButton = makeButton("+ Add category", Color.BLACK, Color.WHITE, 160);
            editButton = makeButton("Edit", barGray, Color.BLACK, 90);
            deleteButton = makeButton("Delete Category", barGray, Color.BLACK, 160);

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
            buttons.add(addButton);
            buttons.add(editButton);
            buttons.add(deleteButton);
            top.add(buttons, BorderLayout.SOUTH);
        }
        add(top, BorderLayout.NORTH);

        // middle: the table
        model = new DefaultTableModel(new String[]{"Category", "Number of Batches"}, 0);
        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);             // the cells cannot be typed in
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(40);
        table.setIntercellSpacing(new Dimension(0, 8));         // the space between the rows
        table.setShowGrid(false);
        table.setBackground(pageGray);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, new RowRenderer());

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer());
        header.setPreferredSize(new Dimension(100, 45));
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        header.setBackground(pageGray);

        // the header is inside a panel with the same space at the sides as the rows, so they line up
        JPanel headerHolder = new JPanel(new BorderLayout());
        headerHolder.setBackground(pageGray);
        headerHolder.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        headerHolder.add(header, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setColumnHeaderView(headerHolder);
        scroll.setViewportBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));   // space at the sides of the rows
        scroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        scroll.getViewport().setBackground(pageGray);
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        add(scroll, BorderLayout.CENTER);
    }

    // makes one button with the same look as the other screens
    private JButton makeButton(String text, Color bg, Color fg, int width) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setPreferredSize(new Dimension(width, 35));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setFocusPainted(false);
        button.addActionListener(this);
        return button;
    }

    // draws one cell: a gray bar, blue with a black outline when the row is selected
    private class RowRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(t, value, isSelected, false, row, column);
            setHorizontalAlignment(CENTER);
            setFont(new Font("Arial", Font.PLAIN, 16));
            setForeground(Color.BLACK);
            if (isSelected) {
                setBackground(selectColor);
                if (column == 0) {
                    setBorder(BorderFactory.createMatteBorder(2, 2, 2, 0, Color.BLACK));
                } else {
                    setBorder(BorderFactory.createMatteBorder(2, 0, 2, 2, Color.BLACK));
                }
            } else {
                setBackground(barGray);
                setBorder(BorderFactory.createEmptyBorder());
            }
            return this;
        }
    }

    // draws the header names (Category, Number of Batches)
    private class HeaderRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(t, value, false, false, row, column);
            setHorizontalAlignment(CENTER);
            setFont(new Font("Arial", Font.BOLD, 18));
            setForeground(Color.BLACK);
            setBackground(pageGray);
            setBorder(BorderFactory.createEmptyBorder());
            return this;
        }
    }

    public void refresh() {
        model.setRowCount(0);
        for (int i = 0; i < data.getCategories().size(); i++) {
            String name = data.getCategories().get(i);
            model.addRow(new Object[]{name, data.getBatches().countCategory(name)});
        }
    }

    // ADD: open the small window, then add the new name to the list
    private void addCategory() {
        CategoryPopup dialog = new CategoryPopup(main, data, null);
        dialog.setVisible(true);                    // the program waits here until the window is closed
        if (!dialog.isSaved()) {
            return;
        }
        String name = dialog.getCategoryName();
        data.getCategories().add(name);
        main.addLog("CATEGORY: Admin added the category \"" + name + "\".");
        data.save();
        main.refreshAll();
    }

    // EDIT: change the name of the selected category (the batches that use it get the new name too)
    private void editCategory() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a category first.");
            return;
        }
        String oldName = data.getCategories().get(row);

        CategoryPopup dialog = new CategoryPopup(main, data, oldName);
        dialog.setVisible(true);
        if (!dialog.isSaved()) {
            return;
        }
        String newName = dialog.getCategoryName();
        if (newName.equals(oldName)) {
            return;                                 // nothing changed
        }

        data.getCategories().set(row, newName);

        // go through the linked list and rename the category of every batch that used the old name
        Batch current = data.getBatches().getHead();
        while (current != null) {
            if (current.getCategory().equals(oldName)) {
                current.setCategory(newName);
            }
            current = current.getNext();
        }

        main.addLog("CATEGORY: Admin renamed \"" + oldName + "\" to \"" + newName + "\".");
        data.save();
        main.refreshAll();
    }

    // DELETE: remove the selected category, but only when no batch uses it
    private void deleteCategory() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a category first.");
            return;
        }
        String name = data.getCategories().get(row);

        int count = data.getBatches().countCategory(name);
        if (count > 0) {
            JOptionPane.showMessageDialog(this,
                    "Cannot delete \"" + name + "\" because " + count + " batch(es) still use it.\n"
                    + "Change or delete those batches first.",
                    "Delete Category", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int answer = JOptionPane.showConfirmDialog(this,
                "Delete the category \"" + name + "\"?",
                "Delete Category", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        data.getCategories().remove(row);
        main.addLog("CATEGORY: Admin deleted the category \"" + name + "\".");
        data.save();
        main.refreshAll();
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == addButton) {
            addCategory();
        } else if (source == editButton) {
            editCategory();
        } else if (source == deleteButton) {
            deleteCategory();
        }
    }
}