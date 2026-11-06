package com.mindex.challenge.service;

import com.mindex.challenge.data.ReportingStructure;

/**
 * Logic for building an employee's reporting structure.
 */
public interface ReportingStructureService {
    // Returns the employee and their total number of distinct reports
    ReportingStructure read(String id);
}