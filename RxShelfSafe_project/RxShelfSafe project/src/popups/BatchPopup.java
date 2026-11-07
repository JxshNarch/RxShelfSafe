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
import java.time.LocalDate;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import database.DataStore;
import model.Batch;

public class BatchPopup extends JDialog implements ActionListener {

    private DataStore data;
    private Batch editing;           // 
    private boolean saved = false;   // becomes true when the user clicks Save

    private JTextField nameField, idField, quantityField, dateField, dosageField;
    private JComboBox<String> categoryBox;
    private JButton saveButton, cancelButton;

    public BatchPopup(JFrame owner, DataStore data, Batch editing) {




        super(owner, true);          // true = the user must close this window first
        this.data = data;
        this.editing = editing;

        if (editing == null) {
            setTitle("Add New Batch");
        } else {
            setTitle("Edit Batch");
        }

        nameField = new JTextField();
        idField = new JTextField();
        quantityField = new JTextField();
        dateField = new JTextField();
        dosageField = new JTextField();
        categoryBox = new JComboBox<String>();
        for (int i = 0; i < data.getCategories().size(); i++) {
            categoryBox.addItem(data.getCategories().get(i));
        }

        //
        if (editing != null) {
            nameField.setText(editing.getName());
            idField.setText(editing.getBatchId());
            idField.setEditable(false);
            quantityField.setText("" + editing.getQuantity());
            dateField.setText(editing.getExpiration().format(Batch.DATE_FORMAT));
            dosageField.setText(editing.getDosage());
            categoryBox.setSelectedItem(editing.getCategory());
        }

        //table style
        JPanel form = new JPanel(new GridLayout(6, 1, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        form.add(makeRow("Medicine Name", nameField, 300, 40));
        form.add(makeRow("Batch ID", idField, 300, 40));
        form.add(makeRow("Quantity", quantityField, 300, 40));
        form.add(makeRow("Expiration Date (MM/DD/YYYY)", dateField, 300, 40));
        form.add(makeRow("Category", categoryBox, 300, 40));
        form.add(makeRow("Dosage", dosageField, 300, 40));

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(cancelButton);
        buttons.add(saveButton);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        styleWindow(form, buttons);
        setSize(595, 480);                
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    //row
    private JPanel makeRow(String text, Component box, int width, int height) {
        JLabel label = new JLabel(text);
        label.setPreferredSize(new Dimension(250, height));
        box.setPreferredSize(new Dimension(width, height));
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setBackground(Color.WHITE);
        row.add(label);
        row.add(box);
        return row;
    }

    //row 2 style
    private void styleWindow(JPanel form, JPanel buttons) {
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);

        for (Component row : form.getComponents()) {
            for (Component c : ((JPanel) row).getComponents()) {
                if (c instanceof JLabel) {
                    c.setFont(labelFont);
                } else if (c instanceof JTextField) {
                    c.setFont(fieldFont);
                    ((JTextField) c).setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(Color.BLACK, 2),
                            BorderFactory.createEmptyBorder(0, 8, 0, 8)));
                } else if (c instanceof JComboBox) {
                    c.setFont(fieldFont);
                    c.setBackground(Color.WHITE);
                    ((JComboBox<?>) c).setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                }
            }
        }

        saveButton.setFont(fieldFont);
        saveButton.setPreferredSize(new Dimension(100, 30));
        saveButton.setBackground(Color.BLACK);
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        saveButton.setFocusPainted(false);

        cancelButton.setFont(fieldFont);
        cancelButton.setPreferredSize(new Dimension(100, 30));
        cancelButton.setBackground(new Color(190, 190, 190));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        cancelButton.setFocusPainted(false);

        form.setBackground(Color.WHITE);
        buttons.setBackground(Color.WHITE);
        buttons.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 25));   // space under and right of the buttons
    }

    public boolean isSaved() {
        return saved;
    }

    public String getBatchId() {
        return idField.getText().trim();
    }

    public boolean isNewBatch() {
        return editing == null;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
            dispose();
            return;
        }

        // the Save button was clicked  check the input first
        String name = nameField.getText().trim();
        String id = idField.getText().trim();
        String dosage = dosageField.getText().trim();

        if (name.equals("") || id.equals("") || dosage.equals("")) {
            showError("Please fill in the medicine name, batch ID and dosage.");
            return;
        }
        if (categoryBox.getSelectedItem() == null) {
            showError("Please add a category first.");
            return;
        }
        if (editing == null && data.getBatches().find(id) != null) {
            showError("Batch ID " + id + " already exists.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException ex) {
            showError("Quantity must be a whole number.");
            return;
        }
        if (quantity < 0) {
            showError("Quantity cannot be negative.");
            return;
        }

        LocalDate expiration;
        try {
            expiration = LocalDate.parse(dateField.getText().trim(), Batch.DATE_FORMAT);
        } catch (Exception ex) {
            showError("Expiration date must be a real date like 12/31/2026.");
            return;
        }

        String category = categoryBox.getSelectedItem().toString();

        if (editing == null) {








            // new batch: add the node at the front of the linked list
            Batch newBatch = new Batch(id, name, quantity, expiration, category, dosage);
            data.getBatches().addFirst(newBatch);
        } else {









            // old batch
            editing.setName(name);
            editing.setQuantity(quantity);
            editing.setExpiration(expiration);
            editing.setCategory(category);
            editing.setDosage(dosage);
            editing.setChecked(false);       // the details changed click again
        }

        saved = true;
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Check your input", JOptionPane.ERROR_MESSAGE);
    }
}