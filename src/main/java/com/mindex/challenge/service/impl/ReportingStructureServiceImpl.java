package com.mindex.challenge.service.impl;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import com.mindex.challenge.service.ReportingStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * Builds the reporting structure for an employee by counting everyone under
 * them in the org chart, at every level.
 */
@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    // Used to look up each report. Returns null for ids that don't exist
    @Autowired
    private EmployeeRepository employeeRepository;

    // Used to look up the starting employee, and returns 404 if they don't exist
    @Autowired
    private EmployeeService employeeService;

    @Override
    public ReportingStructure read(String id) {
        LOG.debug("Reading reporting structure for employee with id [{}]", id);

        Employee employee = employeeService.read(id);

        // Computed on every request 
        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(countReports(employee));

        return reportingStructure;
    }

    /**
     * Counts every distinct employee under the given manager, at all levels
     * 
     * Uses a BFS search 
     */
    private int countReports(Employee manager) {
        // Every employee id found so far, starting with the manager themself
        Set<String> seenIds = new HashSet<>();

        // Employees whose direct reports still need to be checked
        Queue<Employee> toVisit = new ArrayDeque<>();

        seenIds.add(manager.getEmployeeId());
        toVisit.add(manager);

        while (!toVisit.isEmpty()) {
            // Take the next employee off the front of the queue
            Employee current = toVisit.poll();
            
            // No direct reports means there's nothing more to check for this person
            if (current.getDirectReports() == null) {
                continue;
            }
 
            for (Employee report : current.getDirectReports()) {
                if (report == null || report.getEmployeeId() == null) {
                    LOG.warn("Skipping a direct report with no employeeId under employee [{}]", current.getEmployeeId());
                    continue;
                }
 
                String reportId = report.getEmployeeId();
 
                if (seenIds.contains(reportId)) {
                    LOG.debug("Skipping employee [{}], already seen", reportId);
                    continue;
                }
 
                Employee fullReport = employeeRepository.findByEmployeeId(reportId);
 
                if (fullReport == null) {
                    LOG.warn("Direct report [{}] of employee [{}] does not exist, skipping", reportId, current.getEmployeeId());
                    continue;
                }
                
                seenIds.add(reportId);
                toVisit.add(fullReport);
            }
        }
        // Everyone in the set is a report except the manager we started with
        int numberOfReports = seenIds.size() - 1;
        LOG.debug("Employee [{}] has [{}] reports", manager.getEmployeeId(), numberOfReports);
        return numberOfReports;
    }
}