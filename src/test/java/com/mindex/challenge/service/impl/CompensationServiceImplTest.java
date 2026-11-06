package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    // An ID no real employee will ever have
    private static final String UNKNOWN_EMPLOYEE_ID = "does-not-exist";

    private String employeeUrl;
    private String compensationUrl;
    private String compensationIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        compensationUrl = "http://localhost:" + port + "/compensation";
        compensationIdUrl = "http://localhost:" + port + "/compensation/{employeeId}";
    }

    @Test
    public void testCreateRead() {
        String employeeId = createEmployee();
        Compensation testCompensation = buildCompensation(employeeId, "85000.00", LocalDate.of(2026, 1, 1));

        ResponseEntity<Compensation> createResponse =
                restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());

        Compensation createdCompensation = createResponse.getBody();
        assertNotNull(createdCompensation.getCompensationId());
        assertCompensationEquivalence(testCompensation, createdCompensation);

        ResponseEntity<Compensation[]> readResponse =
                restTemplate.getForEntity(compensationIdUrl, Compensation[].class, employeeId);
        assertEquals(HttpStatus.OK, readResponse.getStatusCode());

        Compensation[] readCompensations = readResponse.getBody();
        assertEquals(1, readCompensations.length);
        assertEquals(createdCompensation.getCompensationId(), readCompensations[0].getCompensationId());
        assertCompensationEquivalence(createdCompensation, readCompensations[0]);
    }

    @Test
    public void testReadReturnsOldestFirst() {
        String employeeId = createEmployee();
        Compensation newer = buildCompensation(employeeId, "90000.00", LocalDate.of(2026, 6, 1));
        Compensation older = buildCompensation(employeeId, "80000.00", LocalDate.of(2025, 1, 1));

    
        restTemplate.postForEntity(compensationUrl, newer, Compensation.class);
        restTemplate.postForEntity(compensationUrl, older, Compensation.class);

        Compensation[] readCompensations =
                restTemplate.getForEntity(compensationIdUrl, Compensation[].class, employeeId).getBody();

        assertEquals(2, readCompensations.length);
        assertCompensationEquivalence(older, readCompensations[0]);
        assertCompensationEquivalence(newer, readCompensations[1]);
    }

    @Test
    public void testReadNoRecordsReturnsEmptyList() {
        String employeeId = createEmployee();

        ResponseEntity<Compensation[]> response =
                restTemplate.getForEntity(compensationIdUrl, Compensation[].class, employeeId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().length);
    }

    @Test
    public void testReadUnknownEmployeeReturns404() {
       
        ResponseEntity<String> response =
                restTemplate.getForEntity(compensationIdUrl, String.class, UNKNOWN_EMPLOYEE_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateUnknownEmployeeReturns404() {
        Compensation testCompensation = buildCompensation(UNKNOWN_EMPLOYEE_ID, "85000.00", LocalDate.of(2026, 1, 1));

        ResponseEntity<String> response =
                restTemplate.postForEntity(compensationUrl, testCompensation, String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreateNegativeSalaryReturns400() {
        String employeeId = createEmployee();
        Compensation testCompensation = buildCompensation(employeeId, "-1.00", LocalDate.of(2026, 1, 1));

        ResponseEntity<String> response =
                restTemplate.postForEntity(compensationUrl, testCompensation, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCreateMissingEffectiveDateReturns400() {
        String employeeId = createEmployee();
        Compensation testCompensation = buildCompensation(employeeId, "85000.00", null);

        ResponseEntity<String> response =
                restTemplate.postForEntity(compensationUrl, testCompensation, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    
    private String createEmployee() {
        Employee employee = new Employee();
        employee.setFirstName("Test");
        employee.setLastName("Employee");
        employee.setDepartment("Engineering");
        employee.setPosition("Developer");

        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();
        return createdEmployee.getEmployeeId();
    }

    
    private static Compensation buildCompensation(String employeeId, String salary, LocalDate effectiveDate) {
        Compensation compensation = new Compensation();
        compensation.setEmployeeId(employeeId);
        compensation.setSalary(new BigDecimal(salary));
        compensation.setEffectiveDate(effectiveDate);
        return compensation;
    }

    private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
        assertEquals(expected.getEmployeeId(), actual.getEmployeeId());
        
        assertEquals("salary should match", 0, expected.getSalary().compareTo(actual.getSalary()));
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    }
}