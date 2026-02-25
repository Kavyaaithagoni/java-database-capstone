package com.yourpackage.controller; // change package as needed

import com.yourpackage.model.Prescription;
import com.yourpackage.service.PrescriptionService;
import com.yourpackage.service.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private Service service;


    // 1️⃣ Save Prescription (Doctor Only)
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> savePrescription(
            @PathVariable String token,
            @RequestBody Prescription prescription) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "doctor");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        return prescriptionService.savePrescription(prescription);
    }


    // 2️⃣ Get Prescription by Appointment ID (Doctor Only)
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(
            @PathVariable Long appointmentId,
            @PathVariable String token) {

        ResponseEntity<Map<String, String>> validation =
                service.validateToken(token, "doctor");

        if (!validation.getStatusCode().is2xxSuccessful()) {
            return validation;
        }

        ResponseEntity<Map<String, Object>> response =
                prescriptionService.getPrescription(appointmentId);

        Map<String, Object> body = response.getBody();

        if (body != null && body.containsKey("prescriptions")) {
            return response;
        }

        return ResponseEntity.ok(
                Map.of("message",
                        "No prescription exists for this appointment"));
    }
}