package com.mindex.challenge.service.impl;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure read(String id) {
        Employee employee = employeeRepository.findByEmployeeId(id);

        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);
        reportingStructure.setNumberOfReports(countReports(employee));

        return reportingStructure;
    }

    private int countReports(Employee employee) {
        if(employee.getDirectReports() == null){
            return 0;
        }

        int count = 0;
        for (Employee report : employee.getDirectReports()) {
            Employee fullReport = employeeRepository.findByEmployeeId(report.getEmployeeId());
            count = count + 1 + countReports(fullReport);
        }
        return count;
    }
}