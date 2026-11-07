package panels;
import database.DataStore;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import model.Batch;
import model.BatchList;

// Dashboard

public class DashboardPanel extends JPanel {

    private DataStore data;

    private JLabel totalLabel, goodLabel, nearLabel, expiredLabel;
    private JLabel pendingLabel, adminLabel, staffLabel, categoryLabel;
    private JPanel nextList;                    

    public DashboardPanel(DataStore data) {
        this.data = data;

        Color pageGray = new Color(230, 230, 230);    

        setLayout(new BorderLayout(10, 10));
        setBackground(pageGray);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("DASHBOARD");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        add(title, BorderLayout.NORTH);

        // the cards with the numbers
        totalLabel = new JLabel("0");
        goodLabel = new JLabel("0");
        nearLabel = new JLabel("0");
        expiredLabel = new JLabel("0");
        pendingLabel = new JLabel("0");
        adminLabel = new JLabel("0"); 
        staffLabel = new JLabel("0");
        categoryLabel = new JLabel("0");


totalLabel.setFont(new Font("roboto", Font.PLAIN, 38));
        goodLabel.setFont(new Font("roboto", Font.PLAIN, 38));
        nearLabel.setFont(new Font("roboto", Font.PLAIN, 38));
        expiredLabel.setFont(new Font("roboto", Font.PLAIN, 38));
        pendingLabel.setFont(new Font("roboto", Font.PLAIN, 38));
        adminLabel.setFont(new Font("roboto", Font.PLAIN, 38)); 
        staffLabel.setFont(new Font("roboto", Font.PLAIN, 38));
        categoryLabel.setFont(new Font("roboto", Font.PLAIN, 38));
















        Color total = new Color(73, 146, 255);
        Color green = new Color(50, 220, 65);
        Color orange = new Color(255, 143, 53);
        Color red = new Color(210, 61, 61);

        JPanel boxes = new JPanel(new GridLayout(2, 4, 15, 15));
        boxes.setBackground(pageGray);
        boxes.add(makeBox("Total items", totalLabel, total));
        boxes.add(makeBox("Good", goodLabel, green));
        boxes.add(makeBox("Near Expiry", nearLabel, orange));
        boxes.add(makeBox("Expired", expiredLabel, red));
        boxes.add(makeBox("Categories", categoryLabel, Color.WHITE));
        boxes.add(makeBox("Pending removal", pendingLabel, Color.WHITE));
        boxes.add(makeBox("Staff Accounts", staffLabel, Color.WHITE));
        boxes.add(makeBox("Admin Accounts", adminLabel, Color.WHITE));
        boxes.setPreferredSize(new Dimension(100, 210));
        

        



        // the list of the next batches to expire
        nextList = new JPanel();
        nextList.setLayout(new BoxLayout(nextList, BoxLayout.Y_AXIS));
        nextList.setBackground(new Color(245, 245, 245));
        nextList.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JScrollPane nextScroll = new JScrollPane(nextList);
        nextScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        nextScroll.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(0, 0, 0)));

        JLabel nextTitle = new JLabel("Batches by expiry date (FEFO)");
        nextTitle.setFont(new Font("open sans", Font.BOLD, 16));

        JPanel nextPanel = new JPanel(new BorderLayout(5, 5));
        nextPanel.setBackground(pageGray);
        nextPanel.add(nextTitle, BorderLayout.NORTH);
        nextPanel.add(nextScroll, BorderLayout.CENTER);

        JPanel middle = new JPanel(new BorderLayout(10, 15));
        middle.setBackground(pageGray);
        middle.add(boxes, BorderLayout.NORTH);
        middle.add(nextPanel, BorderLayout.CENTER);
        add(middle, BorderLayout.CENTER);












    }

    // makes one card
    private JPanel makeBox(String text, JLabel numberLabel, Color background) {
        JLabel titleLabel = new JLabel(text);
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        numberLabel.setForeground(Color.BLACK);
        numberLabel.setFont(new Font("Arial", Font.PLAIN, 38));
        numberLabel.setHorizontalAlignment(JLabel.CENTER);

        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(background);
        box.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));   // space inside the card
        box.add(titleLabel, BorderLayout.NORTH);
        box.add(numberLabel, BorderLayout.CENTER);
        return box;
    }

    // makes one
    private JPanel makeRow(Batch b) {
        //  (red / orange / green)
        JPanel stripe = new JPanel();
        stripe.setPreferredSize(new Dimension(20, 0));
        stripe.setBackground(statusColor(b.getStatus()));

        JLabel nameLabel = new JLabel(b.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel detailsLabel = new JLabel("Batch " + b.getBatchId()
                + " ׀ Quantity: " + b.getQuantity()
                + " ׀ Expiry date: " + b.getExpiration().format(Batch.DATE_FORMAT)
                + " ׀ " + b.getCategory()
                + "   " + b.getDosage());
        detailsLabel.setFont(new Font("Arial", Font.PLAIN, 15));
         
        detailsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));   // space 
 


        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        row.setPreferredSize(new Dimension(100, 42));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));   // ke]
        row.add(stripe, BorderLayout.WEST);
        row.add(nameLabel, BorderLayout.CENTER);
        row.add(detailsLabel, BorderLayout.EAST);
        return row;
    }

   
    private Color statusColor(String status) {
        if (status.equals("Expired")) {
            return new Color(255, 80, 80);
        } else if (status.equals("Near expiry")) {
            return new Color(255, 193, 59);
        } else {
            return new Color(0, 230, 60);
        }
    }

    
    public void refresh() {
        BatchList list = data.getBatches();
        totalLabel.setText("" + list.getSize());
        goodLabel.setText("" + list.countStatus("Good"));
        nearLabel.setText("" + list.countStatus("Near expiry"));
        expiredLabel.setText("" + list.countStatus("Expired"));
        pendingLabel.setText("" + data.getRequests().size());
        adminLabel.setText("" + data.countRole("ADMIN"));
        staffLabel.setText("" + data.countRole("STAFF"));
        categoryLabel.setText("" + data.getCategories().size());

        nextList.removeAll();                       
        ArrayList<Batch> earliest = list.getEarliest(5);
        for (int i = 0; i < earliest.size(); i++) {
            nextList.add(makeRow(earliest.get(i)));
            nextList.add(Box.createVerticalStrut(10));      
        }
        nextList.revalidate();                         
        nextList.repaint();
    }
}