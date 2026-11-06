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

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public ReportingStructure read(String id) {
        LOG.debug("Reading reporting structure for employee with id [{}]", id);

        Employee employee = employeeService.read(id);

        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(countReports(employee));

        return reportingStructure;
    }

    private int countReports(Employee manager) {
        Set<String> seenIds = new HashSet<>();
        Queue<Employee> toVisit = new ArrayDeque<>();

        seenIds.add(manager.getEmployeeId());
        toVisit.add(manager);

        while (!toVisit.isEmpty()) {
            Employee current = toVisit.poll();
 
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
 
                toVisit.add(fullReport);
            }
        }
        int numberOfReports = seenIds.size() - 1;
        LOG.debug("Employee [{}] has [{}] reports", manager.getEmployeeId(), numberOfReports);
        return numberOfReports;
    }
}