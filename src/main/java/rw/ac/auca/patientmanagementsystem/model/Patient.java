package rw.ac.auca.patientmanagementsystem.model;

import java.io.Serializable;
import java.time.LocalDate;

public class Patient implements Serializable {

    private String firstName;
    private String lastName;
    private LocalDate dob;
    private double amount;

    public Patient() {
    }

    public Patient(String firstName, String lastName, LocalDate dob, double amount) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.amount = amount;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}