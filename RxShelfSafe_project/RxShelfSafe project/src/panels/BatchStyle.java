package panels;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.Batch;

// One card of the Manage cared style

public class BatchStyle extends JPanel implements MouseListener, ActionListener {

    private Batch batch;
    private BatchPanel panel;          //cards 
    private JButton requestButton;

    public BatchStyle(Batch b, boolean isHead, boolean showRequest, boolean alreadyRequested, BatchPanel panel) {
        this.batch = b;
        this.panel = panel;

        setLayout(new BorderLayout(15, 0));
        setBackground(new Color(230, 230, 230));
        setPreferredSize(new Dimension(100, 100));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); 
        setSelected(false);
        addMouseListener(this);

        //color (red / orange / green)
        JPanel stripe = new JPanel();
        stripe.setPreferredSize(new Dimension(20, 0));
        stripe.setBackground(statusColor(b.getStatus()));

        //middle text
        String firstLine = b.getName().toUpperCase() + "  -  Batch ID: " + b.getBatchId();
        if (isHead) {
            firstLine = firstLine + "   [HEAD]";
        }
        JLabel nameLabel = new JLabel(firstLine);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel line2 = new JLabel("Quantity: " + b.getQuantity() + "     Category: " + b.getCategory());
        line2.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel line3 = new JLabel("Dosage: " + b.getDosage() + "     Expiry date: "
                + b.getExpiration().format(Batch.DATE_FORMAT));
        line3.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel statusLabel = new JLabel(b.getStatus().toUpperCase());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);                       //show colo
        textPanel.add(Box.createVerticalGlue());
        textPanel.add(nameLabel);
        textPanel.add(line2);
        textPanel.add(line3);
        textPanel.add(statusLabel);
        textPanel.add(Box.createVerticalGlue());

        //reviewed
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.X_AXIS));
        rightPanel.setOpaque(false);
        if (b.isChecked()) {
            JLabel reviewedLabel = new JLabel("Reviewed");
            reviewedLabel.setFont(new Font("Arial", Font.BOLD, 14));
            reviewedLabel.setForeground(new Color(20, 130, 50));
            rightPanel.add(reviewedLabel);
            rightPanel.add(Box.createHorizontalStrut(20));
        }
        if (showRequest) {
            requestButton = new JButton("Request removal");


            
            requestButton.setFont(new Font("Arial", Font.PLAIN, 14));
            requestButton.setPreferredSize(new Dimension(150, 36));
            requestButton.setMaximumSize(new Dimension(150, 36));
            requestButton.setBackground(Color.BLACK);
            requestButton.setForeground(Color.WHITE);
            requestButton.setFocusPainted(false);
            if (alreadyRequested) {
                requestButton.setText("Requested");
                requestButton.setEnabled(false);


 

               
            }
            requestButton.addActionListener(this);
            rightPanel.add(requestButton);
        }
        rightPanel.add(Box.createHorizontalStrut(20));

        add(stripe, BorderLayout.WEST);
        add(textPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    public Batch getBatch() {
        return batch;
    }

    //thicc
    public void setSelected(boolean selected) {
        if (selected) {
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            setBackground(new Color(214, 214, 214));
        } else {
            setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
            setBackground(Color.WHITE);
        }
    }

    //color stripe
    private Color statusColor(String status) {
        if (status.equals("Expired")) {
            return new Color(255, 80, 80);
        } else if (status.equals("Near expiry")) {
            return new Color(255, 193, 59);
        } else {
            return new Color(0, 230, 60);
        }
    }

    // the request clicked
    public void actionPerformed(ActionEvent e) {
        panel.requestRemoval(batch);






    }

    // the card was clicked: select it
    public void mouseClicked(MouseEvent e) {
        panel.selectBatch(batch);
    }

    // the mouse methods below are required by MouseListener but we do not use them
    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}