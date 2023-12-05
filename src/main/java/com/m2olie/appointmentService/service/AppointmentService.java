package com.m2olie.appointmentService.service;

import com.m2olie.appointmentService.model.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.m2olie.appointmentService.repository.AppointmentRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for handling appointments.
 */
@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    /**
     * Saves an appointment to the repository.
     *
     * @param appointment The appointment to be saved.
     * @return The saved appointment.
     */
    public Appointment saveAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    /**
     * Retrieves all appointments from the repository.
     *
     * @return A list of all appointments.
     */
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    /**
     * Retrieves an appointment by its ID.
     *
     * @param id The ID of the appointment.
     * @return An Optional containing the appointment if found, or empty otherwise.
     */
    public Optional<Appointment> getAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    /**
     * Finds appointments by doctor's name.
     *
     * @param doctorName The name of the doctor.
     * @return A list of appointments associated with the specified doctor.
     */
    public List<Appointment> getAppointmentsByDoctorName(String doctorName) {
        return appointmentRepository.findByDoctorName(doctorName);
    }

    /**
     * Finds appointments by patient's name.
     *
     * @param patientName The name of the patient.
     * @return A list of appointments associated with the specified patient.
     */
    public List<Appointment> getAppointmentsByPatientName(String patientName) {
        return appointmentRepository.findByPatientName(patientName);
    }

    /**
     * Calculates and retrieves available appointment dates based on the appointment type and duration.
     *
     * @param appointmentType The type of the appointment.
     * @param appointmentDuration The duration of the appointment.
     * @return A list of available dates for the specified appointment type and duration.
     */
    public List<LocalDate> getAvailableDates(String appointmentType, int appointmentDuration) {
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(6);

        List<Appointment> appointments = appointmentRepository.findByAppointmentTypeAndStartTimeBetween(
                appointmentType, start.atStartOfDay(), end.atTime(23, 59));

        List<LocalDate> availableDates = new ArrayList<>();

        long simultaneousSlots = calculateSimultaneousSlots(appointmentType);

        long dayStart = 7;
        long dayEnd = 19;
        long workingHours = dayEnd - dayStart;
        long workingMinutes = workingHours * 60;
        long maxSlotsPerDay = (workingMinutes / appointmentDuration) * simultaneousSlots;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            final LocalDate currentDate = date;

            long countAppointments = appointments.stream()
                    .filter(appointment -> appointment.getStartTime().toLocalDate().equals(currentDate))
                    .count();

            if (countAppointments < maxSlotsPerDay) {
                availableDates.add(date);
            }
        }

        return availableDates;
    }

    /**
     * Calculates and retrieves available time slots for a specific date and appointment type.
     *
     * @param appointmentType The type of the appointment.
     * @param date The date for which to find available time slots.
     * @param appointmentDuration The duration of each appointment slot.
     * @return A list of available time slots for the specified date and appointment type.
     */
    public List<LocalDateTime> getAvailableTimeSlots(String appointmentType, LocalDate date, int appointmentDuration) {
        int maxConcurrentAppointments = calculateSimultaneousSlots(appointmentType);

        LocalDateTime startTime = date.atTime(7, 0);
        LocalDateTime endTime = date.atTime(19, 0);

        List<Appointment> appointments = appointmentRepository.findByAppointmentTypeAndStartTimeBetween(
                appointmentType, startTime, endTime);

        List<LocalDateTime> availableTimeSlots = new ArrayList<>();

        for (LocalDateTime time = startTime; time.plusMinutes(appointmentDuration).isBefore(endTime) || time.plusMinutes(appointmentDuration).equals(endTime); time = time.plusMinutes(appointmentDuration)) {
            final LocalDateTime currentTime = time;

            long overlappingAppointments = appointments.stream()
                    .filter(appointment ->
                            currentTime.isBefore(appointment.getEndTime()) &&
                                    currentTime.plusMinutes(appointmentDuration).isAfter(appointment.getStartTime()))
                    .count();

            if (overlappingAppointments < maxConcurrentAppointments) {
                availableTimeSlots.add(time);
            }
        }

        return availableTimeSlots;
    }

    /**
     * Calculates the number of simultaneous appointment slots available based on the appointment type.
     *
     * @param appointmentType The type of the appointment.
     * @return The number of simultaneous slots.
     */
    private int calculateSimultaneousSlots(String appointmentType) {
        return switch (appointmentType) {
            case "CT", "Allgemeines Arztgespräch", "M2OLIE Zentrum" -> 1;
            case "MRT", "Biopsie" -> 2;
            default -> 0;
        };
    }

}
