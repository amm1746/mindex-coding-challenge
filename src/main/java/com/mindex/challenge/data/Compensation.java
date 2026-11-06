package com.mindex.challenge.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
 
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * One compensation record for an employee.
 */
public class Compensation {

    // Unique id for this record, generated when it's created
    @Id
    private String compensationId;
    
    // Links this record to an employee by id
    private String employeeId;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal salary;

    // A date with no time attached
    private LocalDate effectiveDate;

    // Empty constructor 
    public Compensation() {
        
    }

    // Getters and setters 
    
    public String getCompensationId() {
        return compensationId;
    }

    public void setCompensationId(String compensationId){
        this.compensationId = compensationId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
