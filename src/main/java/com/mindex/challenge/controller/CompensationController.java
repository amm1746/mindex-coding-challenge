package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * REST endpoints for creating and reading compensation records.
 */
@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    private CompensationService compensationService;

    // POST /compensation with a Compensation in the request body.
    // Returns 201 Created, the standard status for creating a new record.
    @PostMapping("/compensation")
    @ResponseStatus(HttpStatus.CREATED)
    public Compensation create(@RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for [{}]", compensation);
        return compensationService.create(compensation);
    }

    // GET /compensation/{employeeId} returns all of that employee's
    // compensation records, oldest first
    @GetMapping("/compensation/{employeeId}")
    public List<Compensation> read(@PathVariable String employeeId) {
        LOG.debug("Received compensation read request for employee id [{}]", employeeId);
        return compensationService.read(employeeId);
    }
}