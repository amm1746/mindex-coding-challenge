package com.mindex.challenge.service;

import java.util.List;

import com.mindex.challenge.data.Compensation;

/**
 * Logic for creating and reading compensation records.
 */
public interface CompensationService {
    // Validates and saves a new compensation record for an existing employee
    Compensation create(Compensation compensation);
    
    // Returns all of an employee's compensation records, oldest first
    List<Compensation> read(String employeeId);
}