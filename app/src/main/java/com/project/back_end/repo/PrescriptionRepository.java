package com.yourpackage.repository; // change package as needed

import com.yourpackage.model.Prescription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends MongoRepository<Prescription, String> {

    // Find prescriptions by appointmentId
    List<Prescription> findByAppointmentId(Long appointmentId);
}