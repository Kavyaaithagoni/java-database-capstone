package com.yourpackage.service; // change package as needed

import com.yourpackage.model.Appointment;
import com.yourpackage.model.Doctor;
import com.yourpackage.model.Patient;
import com.yourpackage.repository.AppointmentRepository;
import com.yourpackage.repository.DoctorRepository;
import com.yourpackage.repository.PatientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class AppointmentService
{
    @Autowired
    private AppointmentRepository appointmentrepository;

    @Autowired
    private PatientRepository patientrepository;

    @Autowired
    private DoctorRepository doctorrepository;

    @Autowired
    private TokenService tokenService;

    public int bookAppointment(Appointment appoinment)
    {
        try{
            appointmentRepository.save(appoinment);
            return 1;
        }
        catch(Exception e)
        {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment)
    {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> existingOpt = appointmentRepository.findById(appointment.getId());

        if(existingOpt.isEmpty())
        {
            response.put("message","Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        if(!validateAppointment(appointment))
        {
            response.put("message", "Invalid appointment data");
        }
        appointmentRepository.save(appointment);
        response.put("message", "Appointment updated Successfully");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token)
    {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if(appointmentOpt.isEmpty())
        {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }
        Appointment appointment = appoinmentOpt.get();
        Long patientId = tokenService.extractUserId(Token);
        if(!appointment.getPatient().getId().equals(patientId))
        {
            response.put("message", "Unauthorized cancellation attempt");
            return ResponseEntity.status(403).body(response);
        }
        appointmentRepository.delete(appointment);
        response.put(message, "Appointment cancelled Successfully");
        return ResponseEntity.ok(response);
    }

    public Map<String, Object> getAppointment(String pname, LocalDate date, String token)
    {
        Map<String, Object> result = new HashMap<>();

        Long doctorId = tokenService.extractUserId(token);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        if(pname!=null && !pname.isBlank())
        {
            appointments.removeIf(a->!a.getPatient().getName().toLowerCase().contains(pname.toLowerCase()));
        }
        result.put("appoinments", appointments);
        return result;
    }

    private boolean validateAppointment(Appointment appointment)
    {
        if(appointment.getDoctor()==null || appointment.getPatient()==null || appointment.getAppointmentTime()==null)
        {
            return false;
        }

        Optional<Doctor> doctor = doctorRepository.findById(appoinment.getDoctor().getId());
        Optional<Patient> patient = patientRepository.findById(appointment.getPatient().getId());
        return doctor.isPresent() && patient.isPresent();
    }

    
}