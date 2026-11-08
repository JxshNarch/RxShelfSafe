package panels;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import database.DataStore;
import frame.MainFrame;
import model.Batch;


public class RemovalPanel extends JPanel implements ActionListener, MouseListener {

    private DataStore data;
    private MainFrame main;
    private JPanel rowList;                                           // the rows are added in here
    private JScrollPane scroll;
    private JButton approveButton, rejectButton;

    private ArrayList<JPanel> rows = new ArrayList<JPanel>();    
    private ArrayList<JPanel> bars = new ArrayList<JPanel>();    
    private ArrayList<String> ids = new ArrayList<String>();     
    private String selectedId = null;                           
    public RemovalPanel(DataStore data, MainFrame main) {
        this.data = data;
        this.main = main;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // top
        JLabel title = new JLabel("REMOVAL QUEUE");
        title.setFont(new Font("Arial", Font.BOLD, 28));

        JLabel subtitle = new JLabel("First In First Out");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.NORTH);
        top.add(subtitle, BorderLayout.CENTER);
        top.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(top, BorderLayout.NORTH);

        top.setBackground( new Color(230, 230,230));

        






        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(230, 230,230));
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 4, 15));

        JLabel spacer = new JLabel(" ");
        spacer.setPreferredSize(new Dimension(50, 20));
        header.add(spacer, BorderLayout.WEST);

        JPanel names = new JPanel(new GridLayout(1, 4));
        names.setOpaque(false);
        names.add(makeHeaderLabel("Name"));
        names.add(makeHeaderLabel("ID"));
        names.add(makeHeaderLabel("Status"));
        names.add(makeHeaderLabel("Requested By"));
        header.add(names, BorderLayout.CENTER);

        rowList = new JPanel();
        rowList.setLayout(new BoxLayout(rowList, BoxLayout.Y_AXIS));
        rowList.setBackground(new Color(230, 230,230));
        rowList.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        scroll = new JScrollPane(rowList);
        scroll.setColumnHeaderView(header);                      // the header stays on top
        scroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        scroll.getViewport().setBackground(new Color(245, 245, 245));
        scroll.getVerticalScrollBar().setUnitIncrement(20);
        add(scroll, BorderLayout.CENTER);

        //













        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBackground(new Color(230, 230,230));
        if (main.isAdmin()) {
            approveButton = new JButton("Approve");
            rejectButton = new JButton("Reject");
            styleButton(approveButton);
            styleButton(rejectButton);
            approveButton.addActionListener(this);
            rejectButton.addActionListener(this);
            bottom.add(approveButton);
            bottom.add(rejectButton);
        } else {
            JLabel waiting = new JLabel("These requests are waiting for the Admin.");
            waiting.setFont(new Font("Arial", Font.PLAIN, 16));
            bottom.add(waiting);
        }
        add(bottom, BorderLayout.SOUTH);
    }

    // a column name 
    private JLabel makeHeaderLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        return label;
    }

    //  black button style
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setPreferredSize(new Dimension(100, 30));
        button.setBackground(Color.BLACK);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setFocusPainted(false);
    }

    // 
    private Color statusColor(String status) {
        if (status.equals("Expired")) {
            return new Color(255, 0, 0);
        } else if (status.equals("Near expiry")) {
            return new Color(255, 193, 59);
        } else {
            return new Color(0, 230, 60);
        }
    }

    // 
    private JPanel makeRow(int number, String id) {
        
        Batch b = data.getBatches().find(id);
        String medicine = "(deleted)";
        String status = "";
        if (b != null) {
            medicine = b.getName();
            status = b.getStatus();
        }

        JLabel numberLabel = new JLabel("" + number, JLabel.CENTER);
        numberLabel.setFont(new Font("Arial", Font.BOLD, 18));
        numberLabel.setPreferredSize(new Dimension(50, 38));

                       JLabel nameLabel = new JLabel(medicine, JLabel.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel idLabel = new JLabel(id, JLabel.CENTER);
        idLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        // the
        JPanel statusCell = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        statusCell.setOpaque(false);
        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusCell.add(statusLabel);
        if (b != null) {
            JPanel square = new JPanel();
            square.setPreferredSize(new Dimension(20, 20));
            square.setBackground(statusColor(status));
            statusCell.add(square);
        }

                       JLabel byLabel = new JLabel("Staff", JLabel.CENTER);
        byLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel bar = new JPanel(new GridLayout(1, 4));
        bar.add(nameLabel);
        bar.add(idLabel);
                 bar.add(statusCell);
        bar.add(byLabel);
        bars.add(bar);
        bar.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(100, 38));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));   // keeps the row from stretching tall
     row.add(numberLabel, BorderLayout.WEST);
        row.add(bar, BorderLayout.CENTER);
        row.addMouseListener(this);
        
        return row;
    }

    // the selected row gets a black outline and a light blue color
private void showSelection() {
        for (int i = 0; i < bars.size(); i++) {
            if (ids.get(i).equals(selectedId)) {
                bars.get(i).setBackground(new Color(214, 214, 214));
                bars.get(i).setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            } else {
                bars.get(i).setBackground(new Color(255, 255, 255));
                bars.get(i).setBorder(BorderFactory.createEmptyBorder());
            }
        }
    }

    public void refresh() {
        rowList.removeAll();
        rows.clear();
        bars.clear();
        ids.clear();

        if (selectedId != null && !data.getRequests().contains(selectedId)) {
            selectedId = null;                                   // request is gone
        }

        for (int i = 0; i < data.getRequests().size(); i++) {
            String id = data.getRequests().get(i);
            JPanel row = makeRow(i + 1, id);
                   rows.add(row);
            ids.add(id);
            rowList.add(row);
            rowList.add(Box.createVerticalStrut(10));            
        }
        showSelection();
        rowList.revalidate();
        rowList.repaint();
    }

    public void actionPerformed(ActionEvent e) {
        if (selectedId == null) {
            JOptionPane.showMessageDialog(this, "Please click a request in the list first.");
            return;
        }
        String id = selectedId;
        int index = data.getRequests().indexOf(id);



        if (e.getSource() == approveButton) {



 int answer = JOptionPane.showConfirmDialog(this,
                    "This item will be permanently deleted from the system",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (answer != JOptionPane.YES_OPTION) {
                return;
            }


            data.deleteBatch(id);        // removes the node and the request
            main.addLog("APPROVED: Admin approved removal of batch " + id + ". The node was deleted.");



            
        } else if (e.getSource() == rejectButton) {


int answer = JOptionPane.showConfirmDialog(this,
                    "Confirm rejection of the removal request?",
                    "Confirm Rejection", JOptionPane.YES_NO_OPTION);
            if (answer != JOptionPane.YES_OPTION) {
                return;
            }







            data.getRequests().remove(index);
            main.addLog("REJECTED: Admin rejected the removal request for batch " + id + ".");
        }
        selectedId = null;
        data.save();
        main.refreshAll();
    }

    // a row was clicked: select it
    public void mouseClicked(MouseEvent e) {
        int index = rows.indexOf(e.getSource());
        if (index != -1) {
            selectedId = ids.get(index);
            showSelection();
        }
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