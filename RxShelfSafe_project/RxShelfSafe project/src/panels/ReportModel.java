package panels;
import javax.swing.table.DefaultTableModel;

// The table model of the Expiry Report.
// It makes the first column ("Checked") a real check box that the user can click.
public class ReportModel extends DefaultTableModel {

    public ReportModel(String[] columns) {
        super(columns, 0);
    }

    // column 0 holds true/false, so the table draws a check box
    public Class<?> getColumnClass(int column) {
        if (column == 0) {
            return Boolean.class;
        }
        return Object.class;
    }

    // only the check box column can be clicked
    public boolean isCellEditable(int row, int column) {
        return column == 0;
    }
}
