package database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;

import model.Account;
import model.Batch;
import model.BatchList;

public class DataStore {

    private BatchList batches = new BatchList();
    private ArrayList<Account> accounts = new ArrayList<Account>();
    private ArrayList<String> categories = new ArrayList<String>();
    private ArrayList<String> requests = new ArrayList<String>();   // Batch IDs waiting for the admin (first in, first out)

    private String fileName = FileFinder.find("rxshelfsafe_data.txt");

    public DataStore() {
        load();
    }

    public BatchList getBatches() {
        return batches;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public ArrayList<String> getRequests() {
        return requests;
    }

    // how many accounts have this role 
    public int countRole(String role) {
        int count = 0;
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getRole().equals(role)) {
                count++;
            }
        }
        return count;
    }

    // delete a batch from the list and from the removal requests
    public void deleteBatch(String batchId) {
        batches.remove(batchId);
        requests.remove(batchId);
    }

    // 
    // LOAD    read the text file and fill the list, accounts, etc.
    //
    // 
    public void load() {
        File file = new File(fileName);
        if (!file.exists()) {
            addSampleData();
            save();
            return;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                String[] p = line.split("\t", -1);    // the parts are separated by a tab
                if (p[0].equals("USER")) {
                    accounts.add(new Account(p[1], p[2], p[3]));
                } else if (p[0].equals("CAT")) {
                    categories.add(p[1]);
                } else if (p[0].equals("BATCH")) {
                    Batch b = new Batch(p[1], p[2], Integer.parseInt(p[3]),
                            LocalDate.parse(p[4]), p[5], p[6]);
                    b.setChecked(p[7].equals("1"));
                    batches.addLast(b);
                } else if (p[0].equals("REQ")) {
                    requests.add(p[1]);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Could not read the data file: " + e.getMessage());
        }

        // make sure there is always at least one admin and one staff account
        if (accounts.size() == 0) {
            addDefaultAccounts();
        }
        if (categories.size() == 0) {
            addDefaultCategories();
        }
    }

    //
    // SAVE: write everything to the text file
    //
    // 
    public void save() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(fileName));
            for (int i = 0; i < accounts.size(); i++) {
                Account a = accounts.get(i);
                writer.println("USER\t" + clean(a.getUsername()) + "\t" + clean(a.getPassword()) + "\t" + a.getRole());
            }
            for (int i = 0; i < categories.size(); i++) {
                writer.println("CAT\t" + clean(categories.get(i)));
            }
            Batch current = batches.getHead();
            while (current != null) {
                String checked = "0";
                if (current.isChecked()) {
                    checked = "1";
                }
                writer.println("BATCH\t" + clean(current.getBatchId()) + "\t" + clean(current.getName())
                        + "\t" + current.getQuantity() + "\t" + current.getExpiration()
                        + "\t" + clean(current.getCategory()) + "\t" + clean(current.getDosage())
                        + "\t" + checked);
                current = current.getNext();
            }
            for (int i = 0; i < requests.size(); i++) {
                writer.println("REQ\t" + requests.get(i));
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("Could not save the data file: " + e.getMessage());
        }
    }

    // remove tabs and new lines so they do not break the file format
    private String clean(String text) {
        return text.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ');
    }

    //
    // sample data for the first time the program runs
    // 
    private void addDefaultAccounts() {
        accounts.add(new Account("admin", "admin123", "ADMIN"));
        accounts.add(new Account("staff", "staff123", "STAFF"));
    }

    private void addDefaultCategories() {
        categories.add("Pain Relievers");
        categories.add("Antibiotics");
        categories.add("Antihistamines");
        categories.add("Vitamins");
        categories.add("Antacids");
    }

    private void addSampleData() {
        addDefaultAccounts();
        addDefaultCategories();
        LocalDate today = LocalDate.now();     // the dates are based on today so all 3 colors show
        batches.addLast(new Batch("B10001", "Paracetamol", 120, today.minusDays(19), "Pain Relievers", "500mg"));
        batches.addLast(new Batch("B10002", "Amoxicillin", 60, today.plusDays(12), "Antibiotics", "250mg"));
        batches.addLast(new Batch("B10003", "Cetirizine", 200, today.plusDays(76), "Antihistamines", "10mg"));
        batches.addLast(new Batch("B10004", "Vitamin C", 150, today.plusDays(21), "Vitamins", "500mg"));
        batches.addLast(new Batch("B10005", "Omeprazole", 80, today.plusDays(240), "Antacids", "20mg"));
    }
}
