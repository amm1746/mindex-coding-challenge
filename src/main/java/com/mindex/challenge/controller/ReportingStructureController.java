package com.mindex.challenge.controller;

import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST endpoint for reading an employee's reporting structure.
 */
@RestController
public class ReportingStructureController{
    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureController.class);
    
    @Autowired
    private ReportingStructureService reportingStructureService;

    // GET /reportingStructure/{id} returns the employee and their total number of
    // reports. Returns 404 if no employee has this id.
    @GetMapping("/reportingStructure/{id}")
    public ReportingStructure read(@PathVariable String id){
         LOG.debug("Received reporting structure read request for id [{}]", id);
        return reportingStructureService.read(id);
    }
}