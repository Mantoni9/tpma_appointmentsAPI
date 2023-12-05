package com.m2olie.appointmentService.controller;

import com.m2olie.appointmentService.model.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.m2olie.appointmentService.service.AppointmentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Controller for managing appointments.
 * Handles HTTP requests related to appointments.
 */
@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * Creates and saves a new appointment.
     *
     * @param appointment The appointment data to be saved.
     * @return The ID of the saved appointment.
     */
    @PostMapping
    public Long saveAppointment(@RequestBody Appointment appointment) {
        Appointment savedAppointment = appointmentService.saveAppointment(appointment);
        return savedAppointment.getId();
    }

    /**
     * Retrieves all appointments.
     *
     * @return A list of all appointments.
     */
    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    /**
     * Retrieves an appointment by its ID.
     *
     * @param id The ID of the appointment.
     * @return A ResponseEntity containing the appointment if found, or a not found status.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        Optional<Appointment> appointment = appointmentService.getAppointmentById(id);
        return appointment
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Finds appointments based on the doctor's name.
     *
     * @param doctorName The name of the doctor.
     * @return A list of appointments associated with the doctor.
     */
    @GetMapping("/doctor/{doctorName}")
    public List<Appointment> getByDoctorName(@PathVariable String doctorName) {
        return appointmentService.getAppointmentsByDoctorName(doctorName);
    }

    /**
     * Finds appointments based on the patient's name.
     *
     * @param patientName The name of the patient.
     * @return A list of appointments associated with the patient.
     */
    @GetMapping("/patient/{patientName}")
    public List<Appointment> getByPatientName(@PathVariable String patientName) {
        return appointmentService.getAppointmentsByPatientName(patientName);
    }

    /**
     * Retrieves available appointment dates based on the appointment type and duration.
     *
     * @param appointmentType The type of appointment.
     * @param appointmentDuration The duration of the appointment.
     * @return A list of available dates.
     */
    @GetMapping("/availableDates")
    public List<LocalDate> getAvailableDates(@RequestParam String appointmentType, @RequestParam int appointmentDuration) {
        return appointmentService.getAvailableDates(appointmentType, appointmentDuration);
    }

    /**
     * Retrieves available time slots for a given date, appointment type, and duration.
     *
     * @param appointmentType The type of appointment.
     * @param date The date for which to find available time slots.
     * @param appointmentDuration The duration of each time slot.
     * @return A list of available time slots.
     */
    @GetMapping("/availableTimeSlots")
    public List<LocalDateTime> getAvailableTimeSlots(@RequestParam String appointmentType, @RequestParam LocalDate date, @RequestParam int appointmentDuration) {
        return appointmentService.getAvailableTimeSlots(appointmentType, date, appointmentDuration);
    }
}
