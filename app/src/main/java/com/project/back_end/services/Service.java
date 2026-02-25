package com.yourpackage.service; // change package as needed

import com.yourpackage.model.*;
import com.yourpackage.dto.Login;
import com.yourpackage.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // 1️⃣ Validate Token
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {

        Map<String, String> response = new HashMap<>();

        if (!tokenService.validateToken(token, user)) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        response.put("message", "Token valid");
        return ResponseEntity.ok(response);
    }

    // 2️⃣ Validate Admin Login
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {

        Map<String, String> response = new HashMap<>();

        Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

        if (admin == null || !admin.getPassword().equals(receivedAdmin.getPassword())) {
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = tokenService.generateToken(admin.getUsername());
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    // 3️⃣ Filter Doctor
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {

        Map<String, Object> result = new HashMap<>();
        List<Doctor> doctors;

        if (name != null && specialty != null && time != null) {
            doctors = doctorService
                    .filterDoctorsByNameSpecilityandTime(name, specialty, time);
        } else if (name != null && specialty != null) {
            doctors = doctorRepository
                    .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        } else if (specialty != null) {
            doctors = doctorRepository
                    .findBySpecialtyIgnoreCase(specialty);
        } else {
            doctors = doctorRepository.findAll();
        }

        result.put("doctors", doctors);
        return result;
    }

    // 4️⃣ Validate Appointment
    public int validateAppointment(Appointment appointment) {

        Optional<Doctor> doctorOpt =
                doctorRepository.findById(appointment.getDoctor().getId());

        if (doctorOpt.isEmpty()) {
            return -1; // Doctor doesn't exist
        }

        List<String> availableSlots =
                doctorService.getDoctorAvailability(
                        appointment.getDoctor().getId(),
                        appointment.getAppointmentTime().toLocalDate()
                );

        String requestedTime = appointment.getAppointmentTime().toLocalTime().toString();

        if (availableSlots.contains(requestedTime)) {
            return 1; // Valid
        }

        return 0; // Time unavailable
    }

    // 5️⃣ Validate Patient (Registration)
    public boolean validatePatient(Patient patient) {

        Patient existing =
                patientRepository.findByEmailOrPhone(
                        patient.getEmail(),
                        patient.getPhone()
                );

        return existing == null; // true if not exists
    }

    // 6️⃣ Validate Patient Login
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {

        Map<String, String> response = new HashMap<>();

        Patient patient =
                patientRepository.findByEmail(login.getIdentifier());

        if (patient == null ||
            !patient.getPassword().equals(login.getPassword())) {

            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = tokenService.generateToken(patient.getEmail());
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    // 7️⃣ Filter Patient Appointments
    public ResponseEntity<Map<String, Object>> filterPatient(
            String condition,
            String name,
            String token) {

        String email = tokenService.extractEmail(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        if (condition != null && name != null) {
            return patientService
                    .filterByDoctorAndCondition(condition, name, patient.getId());
        } else if (condition != null) {
            return patientService
                    .filterByCondition(condition, patient.getId());
        } else if (name != null) {
            return patientService
                    .filterByDoctor(name, patient.getId());
        }

        return patientService
                .getPatientAppointment(patient.getId(), token);
    }
}