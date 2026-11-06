package com.mindex.challenge.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
 
import java.math.BigDecimal;
import java.time.LocalDate;

public class Compensation {

    @Id
    private String compensationId;
    private String employeeId;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal salary;

    private LocalDate effectiveDate;

    public Compensation() {
        
    }

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
