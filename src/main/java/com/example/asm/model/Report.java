package com.example.asm.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "Reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ReportID")
    private Integer reportID;

    @Column(name = "ReportName")
    private String reportName;

    @Column(name = "Description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "CreatedBy", referencedColumnName = "UserID")
    private User createdBy;

    @Column(name = "CreatedAt")
    private Date createdAt;

    // Getters and Setters
}
