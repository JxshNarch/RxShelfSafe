
package panels;

import database.DataStore;
import database.FileFinder;
import model.Batch;
import popups.BatchPopup;
import frame.MainFrame;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;






  
      
        
   
     
       
        
    

















public class BatchPanel extends JPanel implements ActionListener {

    private static final int PAGE_SIZE = 20;    
    
     private String statusFilter = "All";          // the status that is chosen now
    









    // cards

    private DataStore data;
    private MainFrame main;

    private JTextField searchField;
    private JButton searchButton, clearButton, sortButton;
    private JComboBox<String> sortBox;
    private JLabel infoLabel;
    private JButton addButton, editButton, deleteButton;        // admin
    // admin and staff
    private JButton prevButton, nextButton;
    private JLabel pageLabel;

    private JPanel cardList;                                           // the cards are added in here
    private JScrollPane scroll;
    private ArrayList<BatchStyle> cards = new ArrayList<BatchStyle>();   // the cards on the screen now

    private ArrayList<Batch> shown = new ArrayList<Batch>();    // all the batches that match the search
    private String filter = "";                                                      // the text that was searched
    private Batch selectedBatch = null;                                    // the card that was clicked
    private int page = 0;                                                          // the page that is open 

    public BatchPanel(DataStore data, MainFrame main) {
        this.data = data;
        this.main = main;
        setLayout(new BorderLayout(10, 10));

        setBackground(new Color(230, 230, 230));

        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        //top: title, search and sort
        JLabel title = new JLabel("MANAGE BATCHES");
        title.setFont(new Font("Arial", Font.BOLD, 28));
title.setOpaque(true);  
         title.setBackground(new Color(230, 230, 230));

        JLabel subtitle = new JLabel("linked list from HEAD to the last node");
        subtitle.setFont(new Font("Arial", Font.BOLD, 16));
subtitle.setOpaque(true);  
          subtitle.setBackground(new Color(230, 230, 230));





        searchField = new JTextField();
        
        searchField.setPreferredSize(new Dimension(600, 35));
       
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black, 2),
                BorderFactory.createEmptyBorder(0, 10, 0, 8)));
        searchField.addActionListener(this);               

        searchButton = new JButton("Search");
        clearButton = new JButton("Clear");






        sortBox = new JComboBox<String>(new String[]{"All", "Good", "Near expiry", "Expired",
        "Expiration Date", "Batch ID", "Medicine Name", "Category", "Quantity"});      //













        sortButton = new JButton("Sort");

        searchButton.addActionListener(this);
        clearButton.addActionListener(this);
        sortButton.addActionListener(this);

        searchButton.setFont(new Font("Arial", Font.PLAIN, 16));
        clearButton.setFont(new Font("Arial", Font.PLAIN, 16));
        sortButton.setFont(new Font("Arial", Font.PLAIN, 16));
        sortBox.setFont(new Font("Arial", Font.PLAIN, 16));

        // the ?help button
        ImageIcon big = new ImageIcon(FileFinder.find("question.png"));



        
        Image small = big.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);


        JButton helpButton = new JButton(new ImageIcon(small));
        helpButton.setBorder(BorderFactory.createEmptyBorder());      //   no padding around the icon
        helpButton.setMargin(new Insets(0, 0, 0, 0));                 // no inner margin 12
        helpButton.setPreferredSize(new Dimension(30, 30));
        helpButton.setToolTipText("Search by Name, ID or category");
        UIManager.put("ToolTip.font", new Font("Arial", Font.PLAIN, 14));
        UIManager.put("ToolTip.background", new Color(230, 230, 230));
        helpButton.setContentAreaFilled(false);
        helpButton.setBorderPainted(false);
        helpButton.setFocusPainted(false);

        searchButton.setPreferredSize(new Dimension(100, 30));
        searchButton.setBackground(new Color(0, 0, 0));
        searchButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        searchButton.setForeground(new Color(230, 230, 230));

        sortButton.setPreferredSize(new Dimension(90, 30));
        sortButton.setBackground(new Color(0, 0, 0));
        sortButton.setForeground(new Color(255, 255, 255));
        sortButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        clearButton.setPreferredSize(new Dimension(90, 30));
        clearButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        sortBox.setPreferredSize(new Dimension(145, 30));
        sortBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        sortBox.setBackground(new Color(255, 255, 255));
        UIManager.put("ComboBox.selectionBackground", new Color(222, 222, 222));   // highlighted
        UIManager.put("ComboBox.selectionForeground", Color.BLACK);                // text of highlighted item
        UIManager.put("ComboBox.background", Color.WHITE);                         //list background

        JPanel tools = new JPanel(new FlowLayout(FlowLayout.LEFT));

         tools.setBackground(new Color(230, 230, 230));
        tools.add(searchField);
        tools.add(helpButton);
        tools.add(searchButton);
        tools.add(clearButton);
        



        JPanel toolr = new JPanel(new FlowLayout(FlowLayout.RIGHT));
         toolr.setBackground(new Color(230, 230, 230));
        
        toolr.add(sortBox);
        toolr.add(sortButton);


        
      
     

        JPanel reviewHolder = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      

        
        JPanel toolsRow = new JPanel(new BorderLayout());
     toolsRow.add(tools, BorderLayout.CENTER);
      toolsRow.add(toolr, BorderLayout.EAST);       // the new line



        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.NORTH);
        top.add(subtitle, BorderLayout.CENTER);
        top.add(toolsRow, BorderLayout.SOUTH);
        add(top, BorderLayout.NORTH);

        //middle: the list of cards (one card for each node)
        cardList = new JPanel();
        cardList.setLayout(new BoxLayout(cardList, BoxLayout.Y_AXIS));
        cardList.setBackground(new Color(245, 245, 245));
        cardList.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        scroll = new JScrollPane(cardList);
        scroll.getViewport().setBackground(new Color(245, 245, 245));
        scroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        scroll.getVerticalScrollBar().setUnitIncrement(20);        // faster mouse wheel
        add(scroll, BorderLayout.CENTER);

        //bottom  button
        infoLabel = new JLabel(" ");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));

        bottom.setBackground(new Color(230, 230, 230)); //botton area only

        if (main.isAdmin()) {
            addButton = new JButton("+ Add");
            editButton = new JButton("Edit");
            deleteButton = new JButton("Delete");
            addButton.addActionListener(this);
            editButton.addActionListener(this);
            deleteButton.addActionListener(this);
            bottom.add(addButton);
            bottom.add(editButton);
            bottom.add(deleteButton);

      addButton.setPreferredSize(new Dimension(100, 30));
        addButton.setBackground(new Color(0, 0, 0));
        addButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        addButton.setForeground(new Color(255, 255, 255));

deleteButton.setPreferredSize(new Dimension(100, 30));
        deleteButton.setBackground(new Color(0, 0, 0));
        deleteButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        deleteButton.setForeground(new Color(255, 255, 255));

editButton.setPreferredSize(new Dimension(100, 30));
        editButton.setBackground(new Color(0, 0, 0));
        editButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        editButton.setForeground(new Color(255, 255, 255));

 addButton.setFont(new Font("Arial", Font.PLAIN, 16));
  deleteButton.setFont(new Font("Arial", Font.PLAIN, 16));
   editButton.setFont(new Font("Arial", Font.PLAIN, 16));

            
        }

        JPanel prev = new JPanel(new FlowLayout(FlowLayout.RIGHT));
 prev.setBackground(new Color(230, 230, 230));
 prev.setOpaque(true); 

        prevButton = new JButton("<");
        nextButton = new JButton(">");
        pageLabel = new JLabel("Page 1 of 1");
        prevButton.addActionListener(this);
        nextButton.addActionListener(this);
        

                JPanel bottomRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));

                bottomRight.setBackground(new Color(230, 230, 230));
 bottomRight.setOpaque(true); 
        bottomRight.add(infoLabel);
        bottomRight.add(prevButton);
        bottomRight.add(pageLabel);
        bottomRight.add(nextButton);

        JPanel bottomRow = new JPanel(new BorderLayout());

                bottomRow.setBackground(new Color(230, 230, 230));
 bottomRow.setOpaque(true); 

        bottomRow.add(bottom, BorderLayout.WEST);
        bottomRow.add(bottomRight, BorderLayout.EAST);
        add(bottomRow, BorderLayout.SOUTH);


        prevButton.setPreferredSize(new Dimension(50, 30));
        nextButton.setPreferredSize(new Dimension(50, 30));
        pageLabel.setFont(new Font("Arial", Font.PLAIN, 16));

    }

    // fill the screen with the cards of the current page
    public void refresh() {
        int oldScroll = scroll.getVerticalScrollBar().getValue();    // remember where we were scrolled to

        shown = data.getBatches().search(filter);

        // keep only the batches with the chosen status
if (!statusFilter.equals("All")) {
    ArrayList<Batch> filtered = new ArrayList<Batch>();
    for (int i = 0; i < shown.size(); i++) {
        if (shown.get(i).getStatus().equals(statusFilter)) {
            filtered.add(shown.get(i));
        }
    }
    shown = filtered;
}

        // forget the selected card if its batch was deleted
        if (selectedBatch != null && data.getBatches().find(selectedBatch.getBatchId()) != selectedBatch) {
            selectedBatch = null;
        }

        // work out which cards are on this page
        int totalPages = (shown.size() + PAGE_SIZE - 1) / PAGE_SIZE;
        if (totalPages == 0) {
            totalPages = 1;
        }
        if (page >= totalPages) {
            page = totalPages - 1;
        }
        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, shown.size());

        // build the cards
        cardList.removeAll();
        cards.clear();
        Batch head = data.getBatches().getHead();
        for (int i = start; i < end; i++) {
            Batch b = shown.get(i);
            boolean requested = data.getRequests().contains(b.getBatchId());
            BatchStyle card = new BatchStyle(b, b == head, !main.isAdmin(), requested, this);
            card.setSelected(b == selectedBatch);
            cards.add(card);
            cardList.add(card);
            cardList.add(Box.createVerticalStrut(10));         // gap between the cards
        }
        cardList.revalidate();
        cardList.repaint();
        scroll.validate();
        scroll.getVerticalScrollBar().setValue(oldScroll);     // go back to the same place

        pageLabel.setText("Page " + (page + 1) + " of " + totalPages);
        prevButton.setEnabled(page > 0);
        nextButton.setEnabled(page < totalPages - 1);
        infoLabel.setText("   Showing " + shown.size() + " of " + data.getBatches().getSize() + " nodes");

        infoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
    }

    // a card was clicked: remember it and outline it
    public void selectBatch(Batch b) {
        selectedBatch = b;
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).setSelected(cards.get(i).getBatch() == b);
        }
    }

    // the "Request removal" button of a card was clicked staff
    public void requestRemoval(Batch b) {





        //experimental
int answer = JOptionPane.showConfirmDialog(this,
                    "u sure man?",
                    "r u for real man?", JOptionPane.YES_NO_OPTION);
            if (answer != JOptionPane.YES_OPTION) {
                return;
            }













        if (data.getRequests().contains(b.getBatchId())) {
            JOptionPane.showMessageDialog(this, "A removal request for this batch was already sent.");






        } else {
            data.getRequests().add(b.getBatchId());
            main.addLog("REQUEST: Staff requested removal of batch " + b.getBatchId() + ", waiting for Admin.");
            data.save();
            main.refreshAll();
        }
    }

    // get the batch of the selected card null
    private Batch getSelectedBatch() {
        if (selectedBatch == null) {
            JOptionPane.showMessageDialog(this, "Please click a batch card first.");
            return null;
        }
        return selectedBatch;
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == searchButton || source == searchField) {
            filter = searchField.getText().trim();
            page = 0;
            main.addLog("SEARCH: looked for \"" + filter + "\" in the list.");
            refresh();

        } else if (source == clearButton) {
            searchField.setText("");
            filter = "";
            statusFilter = "All";                 
            page = 0;
            refresh();

        } else if (source == sortButton) {
            String field = sortBox.getSelectedItem().toString();

          
            if (field.equals("All") || field.equals("Good") || field.equals("Near expiry") || field.equals("Expired")) {
                statusFilter = field;
                page = 0;
                main.addLog("FILTER: showing " + field + " batches.");
                refresh();

            } else {
                statusFilter = "All";              
                data.getBatches().sortBy(field);
                data.save();
                page = 0;
                main.addLog("SORT: list sorted by " + field + " (head is now "
                        + data.getBatches().getHead().getBatchId() + ").");
                refresh();
            }

        } else if (source == prevButton) {
            page--;
            refresh();

        } else if (source == nextButton) {
            page++;
            refresh();

        } else if (source == addButton) {
            BatchPopup dialog = new BatchPopup(main, data, null);
            dialog.setVisible(true);                 // waits here until the dialog is closed
            if (dialog.isSaved()) {
                main.addLog("ADDED: Batch " + dialog.getBatchId() + " inserted at HEAD of the linked list.");
                data.save();
                page = 0;
                main.refreshAll();
            }

        } else if (source == editButton) {
            Batch b = getSelectedBatch();
            if (b != null) {
                BatchPopup dialog = new BatchPopup(main, data, b);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    main.addLog("EDITED: Batch " + b.getBatchId() + " was updated.");
                    data.save();
                    main.refreshAll();
                }
            }

        } else if (source == deleteButton) {
            Batch b = getSelectedBatch();
            if (b != null) {
                int answer = JOptionPane.showConfirmDialog(this,
                        "Are u sure u want to delete batch " + b.getBatchId() + " (" + b.getName() + ")?",
                        "Confirm", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    String id = b.getBatchId();
                    data.deleteBatch(id);
                    main.addLog("DELETED: Batch " + id + " was removed from the linked list.");
                    data.save();
                    main.refreshAll();
                }
            }

      
            }
        }
    }
