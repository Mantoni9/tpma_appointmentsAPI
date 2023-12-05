package com.m2olie.appointmentService;

import com.m2olie.appointmentService.model.Appointment;
import com.m2olie.appointmentService.repository.AppointmentRepository;
import com.m2olie.appointmentService.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Tests for AppointmentService.
 */
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test saving an appointment.
     */
    @Test
    void saveAppointment() {
        Appointment appointment = new Appointment();
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment saved = appointmentService.saveAppointment(appointment);
        assertNotNull(saved);
    }

    /**
     * Test getting all appointments.
     */
    @Test
    void getAllAppointments() {
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(new Appointment(), new Appointment()));
        List<Appointment> appointments = appointmentService.getAllAppointments();
        assertEquals(2, appointments.size());
    }

    /**
     * Test get an specific appointment.
     */
    @Test
    void getAppointmentById() {
        Long id = 1L;
        Appointment appointment = new Appointment();
        when(appointmentRepository.findById(id)).thenReturn(Optional.of(appointment));

        Optional<Appointment> found = appointmentService.getAppointmentById(id);
        assertTrue(found.isPresent());
    }

    @Test
    void getAppointmentsByDoctorName() {
        String doctorName = "Dr. Smith";
        when(appointmentRepository.findByDoctorName(doctorName))
                .thenReturn(Arrays.asList(new Appointment(), new Appointment()));

        List<Appointment> appointments = appointmentService.getAppointmentsByDoctorName(doctorName);
        assertEquals(2, appointments.size());
    }

    @Test
    void getAppointmentsByPatientName() {
        String patientName = "John Doe";
        when(appointmentRepository.findByPatientName(patientName))
                .thenReturn(Arrays.asList(new Appointment(), new Appointment()));

        List<Appointment> appointments = appointmentService.getAppointmentsByPatientName(patientName);
        assertEquals(2, appointments.size());
    }

    @Test
    void getAvailableDates() {
        String appointmentType = "CT";
        int appointmentDuration = 60; // 1 hour duration
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusMonths(6);
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = end.atTime(23, 59);

        // Mock existing appointments
        List<Appointment> mockAppointments = Arrays.asList(
                new Appointment(null, startTime.plusDays(1).withHour(9), startTime.plusDays(1).withHour(10), "Patient 1", "Doctor A", appointmentType, "Location 1"),
                new Appointment(null, startTime.plusDays(1).withHour(10), startTime.plusDays(1).withHour(11), "Patient 2", "Doctor B", appointmentType, "Location 1")
                // Add more mock appointments as necessary to simulate a busy schedule
        );

        when(appointmentRepository.findByAppointmentTypeAndStartTimeBetween(eq(appointmentType), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockAppointments);

        List<LocalDate> availableDates = appointmentService.getAvailableDates(appointmentType, appointmentDuration);

        // Assert that the dates where maximum slots are filled are not in the available dates
        assertFalse(availableDates.contains(startTime.plusDays(1).toLocalDate()));

        // More assertions can be added to validate the logic comprehensively
    }

    @Test
    void getAvailableTimeSlots() {
        String appointmentType = "CT";
        int appointmentDuration = 60; // 1 hour duration
        LocalDate date = LocalDate.now();
        LocalDateTime startTime = date.atTime(7, 0);
        LocalDateTime endTime = date.atTime(19, 0);

        // Mock existing appointments
        List<Appointment> mockAppointments = Arrays.asList(
                new Appointment(null, startTime.withHour(9), startTime.withHour(10), "Patient 1", "Doctor A", appointmentType, "Location 1"),
                new Appointment(null, startTime.withHour(10), startTime.withHour(11), "Patient 2", "Doctor B", appointmentType, "Location 1")
                // Add more mock appointments as necessary to simulate a busy schedule
        );

        when(appointmentRepository.findByAppointmentTypeAndStartTimeBetween(eq(appointmentType), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(mockAppointments);

        List<LocalDateTime> availableTimeSlots = appointmentService.getAvailableTimeSlots(appointmentType, date, appointmentDuration);

        // Assert that the time slots overlapping with existing appointments are not available
        assertFalse(availableTimeSlots.contains(startTime.withHour(9)));
        assertFalse(availableTimeSlots.contains(startTime.withHour(10)));

        // More assertions can be added to validate the logic comprehensively
    }
}
