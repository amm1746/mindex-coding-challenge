package com.mindex.challenge.dao;

import com.mindex.challenge.data.Compensation;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Database access for compensation records. 
 */
@Repository
public interface CompensationRepository extends MongoRepository<Compensation, String> {
    // Find every record with this employeeId, sorted by effectiveDate, oldest first
    List<Compensation> findByEmployeeIdOrderByEffectiveDateAsc(String employeeId);
}