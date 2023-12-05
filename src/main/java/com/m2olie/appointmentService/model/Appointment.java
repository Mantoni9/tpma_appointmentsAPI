package com.m2olie.appointmentService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents an appointment in the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime startTime;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime endTime;
    private String patientName;
    private String doctorName;
    private String appointmentType;
    private String location;
}
