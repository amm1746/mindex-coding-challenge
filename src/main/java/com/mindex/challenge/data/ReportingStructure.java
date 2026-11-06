package com.mindex.challenge.data;
/**
 * The response for GET /reportingStructure/{id}: an employee plus the total
 * number of people who report to them.
 */
public class ReportingStructure {
    private Employee employee;
    private int numberOfReports;
    
    // Empty constructor so Jackson can create this object
    public ReportingStructure() {

    }

    // Getters and setters 
    
    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee){
        this.employee = employee;
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    public void setNumberOfReports(int numberOfReports){
        this.numberOfReports = numberOfReports;
    }
}