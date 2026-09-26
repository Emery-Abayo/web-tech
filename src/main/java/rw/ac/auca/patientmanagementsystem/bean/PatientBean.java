package rw.ac.auca.patientmanagementsystem.bean;

import rw.ac.auca.patientmanagementsystem.model.Patient;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "patientBean")
@ApplicationScoped
public class PatientBean implements Serializable {

    private String firstName;
    private String lastName;
    private LocalDate dob;
    private double amount;

    private final List<Patient> patients = new ArrayList<>();

    public String register() {
        patients.add(new Patient(firstName, lastName, dob, amount));
        firstName = null;
        lastName = null;
        dob = null;
        amount = 0;
        return "patients?faces-redirect=true";
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public List<Patient> getPatients() { return patients; }
}