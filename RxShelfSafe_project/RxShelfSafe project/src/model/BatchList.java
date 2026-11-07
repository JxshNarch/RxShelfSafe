package model;

import java.util.ArrayList;

public class BatchList {

    private Batch head;    // the first node
    private int size;      // how many nodes are in the listt

    public Batch getHead() {
        return head;
    }

    public int getSize() {
        return size;
    }

    // add a new node at the front
    public void addFirst(Batch newNode) {
        newNode.setNext(head);
        head = newNode;
        size++;
    }

    // add a new node at the end
    public void addLast(Batch newNode) {
        newNode.setNext(null);
        if (head == null) {
            head = newNode;
        } else {
            Batch current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(newNode);
        }
        size++;
    }

    // find a node by Batch ID returns null if not found
    public Batch find(String batchId) {
        Batch current = head;
        while (current != null) {
            if (current.getBatchId().equalsIgnoreCase(batchId)) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }

    // delete a node
    public boolean remove(String batchId) {
        Batch previous = null;
        Batch current = head;
        while (current != null) {
            if (current.getBatchId().equalsIgnoreCase(batchId)) {
                if (previous == null) {
                    head = current.getNext();           
                } else {
                    previous.setNext(current.getNext()); 
                }
                size--;
                return true;
            }
            previous = current;
            current = current.getNext();
        }
        return false;
    }








    // put all the nodes in an ArrayList 
    public ArrayList<Batch> getAll() {
        ArrayList<Batch> all = new ArrayList<Batch>();
        Batch current = head;
        while (current != null) {
            all.add(current);
            current = current.getNext();
        }
        return all;
    }







    // go through the list and keep the nodes that match the text
    // (matches Batch ID, medicine name or category)




    public ArrayList<Batch> search(String text) {
        ArrayList<Batch> result = new ArrayList<Batch>();
        text = text.trim().toLowerCase();
        Batch current = head;
        while (current != null) {
            if (text.equals("")
                    || current.getBatchId().toLowerCase().contains(text)
                    || current.getName().toLowerCase().contains(text)
                    || current.getCategory().toLowerCase().contains(text)) {
                result.add(current);
            }
            current = current.getNext();
        }
        return result;
    }

    // count the nodes with this status
    public int countStatus(String status) {
        int count = 0;
        Batch current = head;
        while (current != null) {
            if (current.getStatus().equals(status)) {
                count++;
            }
            current = current.getNext();
        }
        return count;
    }



    // count the nodes in a category
    public int countCategory(String category) {
        int count = 0;
        Batch current = head;
        while (current != null) {
            if (current.getCategory().equalsIgnoreCase(category)) {
                count++;
            }
            current = current.getNext();
        }
        return count;
    }









    // get the batches that expire first
    public ArrayList<Batch> getEarliest(int howMany) {
        ArrayList<Batch> result = new ArrayList<Batch>();
        for (int i = 0; i < howMany; i++) {
            Batch best = null;
            Batch current = head;
            while (current != null) {
                if (!result.contains(current)) {
                    if (best == null || current.getExpiration().isBefore(best.getExpiration())) {
                        best = current;
                    }
                }
                current = current.getNext();
            }
            if (best != null) {
                result.add(best);
            }
        }
        return result;
    }










    // sort the list with bubble sort 
    //
    public void sortBy(String field) {
        if (head == null) {
            return;
        }
        boolean swapped = true;
        while (swapped) {
            swapped = false;
            Batch current = head;
            while (current.getNext() != null) {
                Batch after = current.getNext();
                if (compare(current, after, field) > 0) {
                    swapData(current, after);
                    swapped = true;
                }
                current = current.getNext();
            }
        }
    }









    // returns a number > 0 if a should come after b
    private int compare(Batch a, Batch b, String field) {
        if (field.equals("Expiration Date")) {
            return a.getExpiration().compareTo(b.getExpiration());
        } else if (field.equals("Batch ID")) {
            return a.getBatchId().compareToIgnoreCase(b.getBatchId());
        } else if (field.equals("Medicine Name")) {
            return a.getName().compareToIgnoreCase(b.getName());
        } else if (field.equals("Category")) {
            return a.getCategory().compareToIgnoreCase(b.getCategory());
        } else if (field.equals("Quantity")) {
            return a.getQuantity() - b.getQuantity();
        }
        return 0;
    }

    // swap the data of two nodes








    private void swapData(Batch a, Batch b) {
        String tempId = a.getBatchId();
        String tempName = a.getName();
        int tempQuantity = a.getQuantity();
        java.time.LocalDate tempDate = a.getExpiration();
        String tempCategory = a.getCategory();
        String tempDosage = a.getDosage();
        boolean tempChecked = a.isChecked();

        a.setBatchId(b.getBatchId());
        a.setName(b.getName());
        a.setQuantity(b.getQuantity());
        a.setExpiration(b.getExpiration());
        a.setCategory(b.getCategory());
        a.setDosage(b.getDosage());
        a.setChecked(b.isChecked());

        b.setBatchId(tempId);
        b.setName(tempName);
        b.setQuantity(tempQuantity);
        b.setExpiration(tempDate);
        b.setCategory(tempCategory);
        b.setDosage(tempDosage);
        b.setChecked(tempChecked);
    }
}
