package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import com.mindex.challenge.service.EmployeeService;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Creates and reads compensation records. Each record is linked to an
 * employee by employeeId, and an employee can have several records over time.
 */
@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    // Used to check that the employee exists 
    @Autowired
    private EmployeeService employeeService;

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation for employee [{}]", compensation.getEmployeeId());
        
        // Reject missing required fields with 400 bad request 
        if (compensation.getEmployeeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employeeId is required");
        }
        if (compensation.getSalary() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "salary is required");
        }
        if (compensation.getEffectiveDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "effectiveDate is required");
        }
        
        if (compensation.getSalary().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "salary cannot be negative");
        }
        

        employeeService.read(compensation.getEmployeeId());
 
        // Generate the id here so the client can't choose or overwrite one 
        compensation.setCompensationId(UUID.randomUUID().toString());
        compensationRepository.insert(compensation);
 
        LOG.debug("Created compensation [{}] for employee [{}]", compensation.getCompensationId(), compensation.getEmployeeId());
 
        return compensation;
    }

    @Override
    public List<Compensation> read(String employeeId) {
        LOG.debug("Reading compensation for employee [{}]", employeeId);
        
        // Returns 404 if the employee doesn't exist 
        employeeService.read(employeeId);
 
        List<Compensation> compensations = compensationRepository.findByEmployeeIdOrderByEffectiveDateAsc(employeeId);
 
        LOG.debug("Found [{}] compensation records for employee [{}]", compensations.size(), employeeId);
 
        return compensations;
    }
}