package model;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

//color if the item

public class ItemStatus extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setHorizontalAlignment(CENTER);

        if (!isSelected) {
            String status = String.valueOf(value);
            if (status.equals("Expired")) {
                cell.setBackground(new Color(255, 40, 40));   //red
            } else if (status.equals("Near expiry")) {
                cell.setBackground(new Color(255, 165, 0));             //yellow
            } else {
                cell.setBackground(new Color(39, 213, 39));      //green
            }
            cell.setForeground(Color.BLACK);
        }
        return cell;
    }
}
