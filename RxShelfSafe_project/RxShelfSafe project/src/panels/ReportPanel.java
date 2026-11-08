package panels;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import database.DataStore;
import database.ExcelExporter;
import frame.MainFrame;
import model.Batch;
import model.BatchList;

public class ReportPanel extends JPanel implements ActionListener, TableModelListener {

    private DataStore data;
    private MainFrame main;

    private String filter = "All";          // the filter that is chosen now
    private JComboBox<String> filterBox;
    private JButton sortButton;
    private JTextField searchField;
    private JButton searchButton;
    private JCheckBox hideBox;
    private JTable table;
    private DefaultTableModel model;
    private boolean loading = false;        // true while refresh() fills the table
    private JLabel infoLabel;
    private JButton checkSelectedButton, uncheckSelectedButton, checkAllButton, uncheckAllButton, exportButton;

    private ArrayList<Batch> shown = new ArrayList<Batch>();    // the batches in the table now

    private Color pageGray = new Color(255,255,255);          // background of the table area
    private Color selectColor = new Color(200, 225, 250);       // color of the selected rows

    public ReportPanel(DataStore data, MainFrame main) {
        this.data = data;
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // top: title, the gray text under it, then search and filter
        JLabel title = new JLabel("EXPIRY REPORT");
        title.setFont(new Font("Arial", Font.BOLD, 28));

        JLabel subtitle = new JLabel("Batches are sorted by expiry date, soonest first");   // change this text if you want
        subtitle.setFont(new Font("Arial", Font.BOLD, 16));
        subtitle.setForeground(new Color(130, 130, 130));

        infoLabel = new JLabel(" ");            // "Showing ... checked" (refresh() fills it, it is at the bottom)
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(0, 10, 0, 8)));
        searchField.addActionListener(this);                // Enter also searches

        searchButton = makeButton("Search", Color.BLACK, Color.WHITE, 100);

        hideBox = new JCheckBox("Hide Checked Items");
        hideBox.setFont(new Font("Arial", Font.PLAIN, 16));
        hideBox.setFocusPainted(false);
        hideBox.setIcon(new CheckIcon(false));
        hideBox.setSelectedIcon(new CheckIcon(true));
        hideBox.addActionListener(this);

        // the dropdown and the Sort button are on the right side
        filterBox = new JComboBox<String>(new String[]{"All", "Good", "Near expiry", "Expired"});
        filterBox.setFont(new Font("Arial", Font.PLAIN, 16));
        filterBox.setPreferredSize(new Dimension(145, 35));
        filterBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        filterBox.setBackground(new Color(217, 217, 217));
        sortButton = makeButton("Sort", Color.BLACK, Color.WHITE, 90);

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tools.add(searchField);
        tools.add(searchButton);
        tools.add(hideBox);

        JPanel rightTools = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightTools.add(sortButton);
        rightTools.add(filterBox);

        JPanel toolsRow = new JPanel(new BorderLayout());
        toolsRow.add(tools, BorderLayout.CENTER);
        toolsRow.add(rightTools, BorderLayout.EAST);

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.NORTH);
        top.add(subtitle, BorderLayout.CENTER);
        top.add(toolsRow, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        // middle: the table (the status column is in the model, but we hide it and draw a stripe instead)
        model = new ReportModel(new String[]{"Mark", "ID", "Name", "Category",
            "Quantity", "Expiry Date", "Days Left", "Status"});
        model.addTableModelListener(this);      // so we know when a check box is clicked
        table = new JTable(model);
        table.setRowHeight(38);
        table.setIntercellSpacing(new Dimension(0, 8));         // the space between the rows
        table.setShowGrid(false);
        table.setBackground(pageGray);
        table.setSelectionBackground(selectColor);
        table.setSelectionForeground(Color.BLACK);
        table.setFillsViewportHeight(true);
        // Ctrl+A, Ctrl+click and Shift+click still work because of this
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        table.removeColumn(table.getColumnModel().getColumn(7));    // hide the Status column
        table.moveColumn(2, 1);        
                                     // Name goes before ID
                table.getColumnModel().getColumn(0).setMinWidth(90);        // Mark: a narrow column that keeps this width
        table.getColumnModel().getColumn(0).setMaxWidth(90);
          // Mark



        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // ID
        table.getColumnModel().getColumn(3).setPreferredWidth(160); // Category
        table.getColumnModel().getColumn(4).setPreferredWidth(90);  // Quantity
        table.getColumnModel().getColumn(5).setPreferredWidth(130); // Expiry Date
        table.getColumnModel().getColumn(6).setPreferredWidth(90);  // Days Left

        // how the cells look
        table.setDefaultRenderer(Object.class, new RowCellRenderer());
        table.setDefaultRenderer(Boolean.class, new CheckRenderer());
        JCheckBox editBox = new JCheckBox();                // the check box while you click it
        editBox.setHorizontalAlignment(JCheckBox.CENTER);
        editBox.setBackground(selectColor);
        editBox.setIcon(new CheckIcon(false));
        editBox.setSelectedIcon(new CheckIcon(true));
        table.setDefaultEditor(Boolean.class, new DefaultCellEditor(editBox));

        // the header (Mark, Name, ID ...)
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer());
        header.setPreferredSize(new Dimension(100, 40));
                header.setBackground(pageGray);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        scroll.getViewport().setBackground(pageGray);

                scroll.setViewportBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));   // small space at the right of the rows

        scroll.getVerticalScrollBar().setUnitIncrement(20);
        add(scroll, BorderLayout.CENTER);

        // bottom: check buttons and the info text on the left, export on the right
        checkSelectedButton = makeButton("Check", Color.BLACK, Color.WHITE, 100);
        uncheckSelectedButton = makeButton("Uncheck", Color.BLACK, Color.WHITE, 100);
        checkAllButton = makeButton("Check all", new Color(190, 190, 190), Color.BLACK, 110);
        uncheckAllButton = makeButton("Uncheck all", new Color(190, 190, 190), Color.BLACK, 120);
        exportButton = makeButton("Export", new Color(102, 255, 0), Color.BLACK, 110);

        JPanel bottomLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomLeft.add(checkSelectedButton);
        bottomLeft.add(uncheckSelectedButton);
        bottomLeft.add(checkAllButton);
        bottomLeft.add(uncheckAllButton);
        bottomLeft.add(infoLabel);

        JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomRight.add(exportButton);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(bottomLeft, BorderLayout.CENTER);
        bottom.add(bottomRight, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
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

    // the color of the stripe at the left of a row
    private Color statusColor(String status) {
        if (status.equals("Expired")) {
            return new Color(255, 0, 0);
        } else if (status.equals("Near expiry")) {
            return new Color(255, 193, 59);
        }
        return new Color(0, 230, 60);
    }

    // draws the cells of a row: a black outline around the row and the colored stripe on the Name cell
    private class RowCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(t, value, isSelected, false, row, column);
            setHorizontalAlignment(CENTER);
            setForeground(Color.BLACK);
            if (isSelected) {
                setBackground(new Color(214,214,214));
            } else {
                setBackground(Color.WHITE);
            }

            if (column == 1) {                       // the Name cell: bold, with the stripe at the left
                setFont(new Font("Arial", Font.BOLD, 16));
                Color stripe = Color.GRAY;
                if (row < shown.size()) {
                    stripe = statusColor(shown.get(row).getStatus());
                }
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(1, 1, 1, 0, Color.BLACK),
                        BorderFactory.createMatteBorder(0, 10, 0, 0, stripe)));
            } else if (column == 6) {                // the last cell closes the outline
                setFont(new Font("Arial", Font.PLAIN, 16));
                setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, Color.BLACK));
            } else {
                setFont(new Font("Arial", Font.PLAIN, 16));
                setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
            }
            return this;
        }
    }

    // draws a check box: a square, and a check mark inside when it is checked
    private class CheckIcon implements Icon {
        private boolean checked;

        public CheckIcon(boolean checked) {
            this.checked = checked;
        }

        public int getIconWidth() {
            return 22;
        }

        public int getIconHeight() {
            return 22;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (checked) {
                g2.setColor(Color.BLACK);
                g2.fillRect(x, y, 22, 22);                  // filled black square
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(3));
                g2.drawLine(x + 5, y + 11, x + 9, y + 16);  // the check mark
                g2.drawLine(x + 9, y + 16, x + 17, y + 6);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRect(x, y, 22, 22);                  // empty square with a black border
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(x + 1, y + 1, 20, 20);
            }
            g2.dispose();
        }
    }

    // draws the check box of the Mark column
    private class CheckRenderer extends JCheckBox implements TableCellRenderer {
        public CheckRenderer() {
            setHorizontalAlignment(CENTER);
            setOpaque(true);
            setIcon(new CheckIcon(false));
            setSelectedIcon(new CheckIcon(true));
        }

        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setSelected(value != null && (Boolean) value);
            if (isSelected) {
                setBackground(Color.WHITE);
            } else {
                setBackground(pageGray);
            }
            return this;
        }
    }

    // draws the header names (Mark, Name, ID ...)
    private class HeaderRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(t, value, false, false, row, column);
            setHorizontalAlignment(CENTER);
            setFont(new Font("Arial", Font.BOLD, 16));
            setForeground(Color.BLACK);
            setBackground(pageGray);
            setBorder(BorderFactory.createEmptyBorder());
            return this;
        }
    }

    // fill the table using the filter and the search text
    public void refresh() {
        BatchList list = data.getBatches();

        // pick the batches that pass the filter, the search and "hide checked"
        ArrayList<Batch> found = list.search(searchField.getText());
        ArrayList<Batch> rows = new ArrayList<Batch>();
        for (int i = 0; i < found.size(); i++) {
            Batch b = found.get(i);
            boolean keep = true;
            if (!filter.equals("All") && !b.getStatus().equals(filter)) {
                keep = false;
            }
            if (hideBox.isSelected() && b.isChecked()) {
                keep = false;
            }
            if (keep) {
                rows.add(b);
            }
        }
        sortByExpiry(rows);
        shown = rows;

        // show them in the table
        loading = true;                 // do not react to our own changes
        model.setRowCount(0);
        int checkedCount = 0;
        for (int i = 0; i < shown.size(); i++) {
            Batch b = shown.get(i);
            if (b.isChecked()) {
                checkedCount++;
            }
            model.addRow(new Object[]{b.isChecked(), b.getBatchId(), b.getName(), b.getCategory(),
                b.getQuantity(), b.getExpiration().format(Batch.DATE_FORMAT),
                b.getDaysLeft(), b.getStatus()});
        }
        loading = false;
        infoLabel.setText("   Showing " + shown.size() + " of " + list.getSize()
                + " items, " + checkedCount + " checked");
    }

    // selection sort: the batch that expires first goes to the top
    private void sortByExpiry(ArrayList<Batch> rows) {
        for (int i = 0; i < rows.size() - 1; i++) {
            int smallest = i;
            for (int j = i + 1; j < rows.size(); j++) {
                if (rows.get(j).getExpiration().isBefore(rows.get(smallest).getExpiration())) {
                    smallest = j;
                }
            }
            Batch temp = rows.get(i);
            rows.set(i, rows.get(smallest));
            rows.set(smallest, temp);
        }
    }

    // check or uncheck the selected rows
    private void setCheckedForSelected(boolean value) {
        int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this, "Please select one or more rows first\n(hold Ctrl or Shift to select many).");
            return;
        }
        for (int i = 0; i < selectedRows.length; i++) {
            shown.get(selectedRows[i]).setChecked(value);
        }
        finishChecking(selectedRows.length, value);
    }

    // check or uncheck everything that is shown in the table
    private void setCheckedForAll(boolean value) {
        if (shown.size() == 0) {
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Mark all " + shown.size() + " shown items as " + (value ? "checked" : "not checked") + "?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        for (int i = 0; i < shown.size(); i++) {
            shown.get(i).setChecked(value);
        }
        finishChecking(shown.size(), value);
    }

    private void finishChecking(int count, boolean value) {
        String word = "unchecked";
        if (value) {
            word = "checked";
        }
        main.addLog("CHECK: " + count + " item(s) " + word + " (filter: " + filter + ").");
        data.save();
        main.refreshAll();
    }

    // save what is shown in the table as an Excel file
    private void exportToExcel() {
        if (shown.size() == 0) {
            JOptionPane.showMessageDialog(this, "There is nothing to export.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("ExpiryReport_" + LocalDate.now() + ".xlsx"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".xlsx")) {
            file = new File(file.getAbsolutePath() + ".xlsx");
        }
        if (file.exists()) {
            int answer = JOptionPane.showConfirmDialog(this, file.getName() + " already exists. Replace it?",
                    "Export to Excel", JOptionPane.YES_NO_OPTION);
            if (answer != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            ExcelExporter.export(file, shown);
            main.addLog("EXPORT: " + shown.size() + " row(s) saved to " + file.getName() + ".");
            JOptionPane.showMessageDialog(this, "Saved " + shown.size() + " rows to:\n" + file.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not save the file. If it is open in Excel, close it and try again.\n" + ex.getMessage(),
                    "Export to Excel", JOptionPane.ERROR_MESSAGE);
        }
    }

    // called when the user clicks a check box in the table
    public void tableChanged(TableModelEvent e) {
        if (loading || e.getType() != TableModelEvent.UPDATE || e.getColumn() != 0) {
            return;
        }
        int row = e.getFirstRow();
        if (row < 0 || row >= shown.size()) {
            return;
        }
        boolean value = (Boolean) model.getValueAt(row, 0);
        Batch b = shown.get(row);
        b.setChecked(value);
        main.addLog("CHECK: batch " + b.getBatchId() + (value ? " checked." : " unchecked."));
        data.save();
        updateInfo();
    }

    // update the "Showing ... checked" text without rebuilding the table
    private void updateInfo() {
        int checkedCount = 0;
        for (int i = 0; i < shown.size(); i++) {
            if (shown.get(i).isChecked()) {
                checkedCount++;
            }
        }
        infoLabel.setText("   Showing " + shown.size() + " of " + data.getBatches().getSize()
                + " items, " + checkedCount + " checked");
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == sortButton) {
            filter = filterBox.getSelectedItem().toString();     // All, Good, Near expiry or Expired
            main.addLog("FILTER: report shows " + filter + " items.");
            refresh();
        } else if (source == searchButton || source == searchField || source == hideBox) {
            refresh();
        } else if (source == checkSelectedButton) {
            setCheckedForSelected(true);
        } else if (source == uncheckSelectedButton) {
            setCheckedForSelected(false);
        } else if (source == checkAllButton) {
            setCheckedForAll(true);
        } else if (source == uncheckAllButton) {
            setCheckedForAll(false);
        } else if (source == exportButton) {
            exportToExcel();
        }
    }
}