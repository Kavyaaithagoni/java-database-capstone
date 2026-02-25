package com.yourpackage.service; // change package as needed

import com.yourpackage.model.Appointment;
import com.yourpackage.model.Patient;
import com.yourpackage.repository.AppointmentRepository;
import com.yourpackage.repository.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    // 1️⃣ Create Patient
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 2️⃣ Get Patient Appointments
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {

        Map<String, Object> response = new HashMap<>();

        String email = tokenService.extractEmail(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null || !patient.getId().equals(id)) {
            response.put("message", "Unauthorized access");
            return ResponseEntity.status(401).body(response);
        }

        List<Appointment> appointments =
                appointmentRepository.findByPatientId(id);

        List<AppointmentDTO> dtoList = convertToDTO(appointments);

        response.put("appointments", dtoList);
        return ResponseEntity.ok(response);
    }

    // 3️⃣ Filter by Condition (past/future)
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {

        Map<String, Object> response = new HashMap<>();

        int status;
        if ("past".equalsIgnoreCase(condition)) {
            status = 1;
        } else if ("future".equalsIgnoreCase(condition)) {
            status = 0;
        } else {
            response.put("message", "Invalid condition");
            return ResponseEntity.badRequest().body(response);
        }

        List<Appointment> appointments =
                appointmentRepository
                        .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(id, status);

        response.put("appointments", convertToDTO(appointments));
        return ResponseEntity.ok(response);
    }

    // 4️⃣ Filter by Doctor Name
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {

        Map<String, Object> response = new HashMap<>();

        List<Appointment> appointments =
                appointmentRepository
                        .filterByDoctorNameAndPatientId(name, patientId);

        response.put("appointments", convertToDTO(appointments));
        return ResponseEntity.ok(response);
    }

    // 5️⃣ Filter by Doctor and Condition
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(
            String condition,
            String name,
            long patientId) {

        Map<String, Object> response = new HashMap<>();

        int status;
        if ("past".equalsIgnoreCase(condition)) {
            status = 1;
        } else if ("future".equalsIgnoreCase(condition)) {
            status = 0;
        } else {
            response.put("message", "Invalid condition");
            return ResponseEntity.badRequest().body(response);
        }

        List<Appointment> appointments =
                appointmentRepository
                        .filterByDoctorNameAndPatientIdAndStatus(name, patientId, status);

        response.put("appointments", convertToDTO(appointments));
        return ResponseEntity.ok(response);
    }

    // 6️⃣ Get Patient Details
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {

        Map<String, Object> response = new HashMap<>();

        String email = tokenService.extractEmail(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null) {
            response.put("message", "Patient not found");
            return ResponseEntity.badRequest().body(response);
        }

        response.put("patient", patient);
        return ResponseEntity.ok(response);
    }

    // 🔹 Helper Method – Convert to DTO
    private List<AppointmentDTO> convertToDTO(List<Appointment> appointments) {

        List<AppointmentDTO> dtoList = new ArrayList<>();

        for (Appointment a : appointments) {
            AppointmentDTO dto = new AppointmentDTO();
            dto.setId(a.getId());
            dto.setDoctorName(a.getDoctor().getName());
            dto.setAppointmentTime(a.getAppointmentTime());
            dto.setStatus(a.getStatus());
            dtoList.add(dto);
        }

        return dtoList;
    }
}