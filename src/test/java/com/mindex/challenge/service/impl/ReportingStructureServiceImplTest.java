package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private static final String JOHN_LENNON_ID = "16a596ae-edd3-4847-99fe-c4518e82c86f";
    private static final String PAUL_MCCARTNEY_ID = "b7839309-3348-463b-a7e3-5de1c168beb3";
    private static final String RINGO_STARR_ID = "03aa1462-ffa9-4978-901b-7c001562cf6f";

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = "http://localhost:" + port + "/reportingStructure/{id}";
    }

    @Test
    public void testJohnLennonHasFourReports() {
        ReportingStructure structure = getReportingStructure(JOHN_LENNON_ID);

        assertEquals(JOHN_LENNON_ID, structure.getEmployee().getEmployeeId());
        assertEquals(4, structure.getNumberOfReports());
    }

    @Test
    public void testPaulMcCartneyHasNoReports() {
        ReportingStructure structure = getReportingStructure(PAUL_MCCARTNEY_ID);

        assertEquals(0, structure.getNumberOfReports());
    }

    @Test
    public void testUnknownIdReturnsNotFound() {
        ResponseEntity<String> response =
                restTemplate.getForEntity(reportingStructureUrl, String.class, "not-a-real-id");

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void testSharedReportIsCountedOnce() {
        List<Employee> directReports =
                Arrays.asList(reportWithId(JOHN_LENNON_ID), reportWithId(RINGO_STARR_ID));
        Employee manager = createEmployee("Brian", "Epstein", directReports);

        assertEquals(5, getReportingStructure(manager.getEmployeeId()).getNumberOfReports());
    }

    @Test
    public void testMissingReportIsSkipped() {
        List<Employee> directReports = Arrays.asList(reportWithId("does-not-exist"));
        Employee employee = createEmployee("Missing", "Report", directReports);

        assertEquals(0, getReportingStructure(employee.getEmployeeId()).getNumberOfReports());
    }

    @Test
    public void testCycleCountsEachEmployeeOnce() {
        Employee a = createEmployee("Employee", "A", new ArrayList<>());
        Employee b = createEmployee("Employee", "B", Arrays.asList(reportWithId(a.getEmployeeId())));

        a.setDirectReports(Arrays.asList(reportWithId(b.getEmployeeId())));
        updateEmployee(a);

        assertEquals(1, getReportingStructure(a.getEmployeeId()).getNumberOfReports());
        assertEquals(1, getReportingStructure(b.getEmployeeId()).getNumberOfReports());
    }

    private ReportingStructure getReportingStructure(String employeeId) {
        ResponseEntity<ReportingStructure> response =
                restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, employeeId);

        assertEquals(200, response.getStatusCode().value());
        return response.getBody();
    }

    private Employee createEmployee(String firstName, String lastName, List<Employee> directReports) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setDepartment("Engineering");
        employee.setPosition("Developer");
        employee.setDirectReports(directReports);

        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        return createdEmployee;
    }

    private void updateEmployee(Employee employee) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.exchange(employeeIdUrl,
                HttpMethod.PUT,
                new HttpEntity<Employee>(employee, headers),
                Employee.class,
                employee.getEmployeeId());
    }

    private static Employee reportWithId(String employeeId) {
        Employee report = new Employee();
        report.setEmployeeId(employeeId);
        return report;
    }
}