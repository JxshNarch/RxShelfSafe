package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;

public class Batch {

    public static final int NEAR_DAYS = 30;

    public static final DateTimeFormatter DATE_FORMAT
            = DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private String batchId;
    private String name;
    private int quantity;
    private LocalDate expiration;
    private String category;
    private String dosage;
    private boolean checked;     // true when staff/admin already checkedthis batch






    private Batch next;          // pointer to the next node in the list

    public Batch(String batchId, String name, int quantity, LocalDate expiration,
            String category, String dosage) {
        this.batchId = batchId;
        this.name = name;
        this.quantity = quantity;
        this.expiration = expiration;
        this.category = category;
        this.dosage = dosage;
        this.checked = false;
        this.next = null;
    }

    // getters
    public String getBatchId() {
        return batchId;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDate getExpiration() {
        return expiration;
    }

    public String getCategory() {
        return category;
    }

    public String getDosage() {
        return dosage;
    }

    public boolean isChecked() {
        return checked;
    }

    public Batch getNext() {
        return next;
    }

    // setters
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setExpiration(LocalDate expiration) {
        this.expiration = expiration;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setNext(Batch next) {
        this.next = next;
    }

    // how many days are left before it expires         negative = already expired
    public long getDaysLeft() {
        return ChronoUnit.DAYS.between(LocalDate.now(), expiration);
    }

    // expired, near expiry, or good
    public String getStatus() {
        long days = getDaysLeft();
        if (days < 0) {
            return "Expired";
        } else if (days <= NEAR_DAYS) {
            return "Near expiry";
        } else {
            return "Good";
        }
    }
}
