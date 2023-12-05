package com.m2olie.appointmentService.repository;

import com.m2olie.appointmentService.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing Appointment entities.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorName(String doctorName);

    List<Appointment> findByPatientName(String patientName);

    List<Appointment> findByAppointmentTypeAndStartTimeBetween(
            String appointmentType, LocalDateTime start, LocalDateTime end);

    int countByStartTime(LocalDateTime startTime);
}
