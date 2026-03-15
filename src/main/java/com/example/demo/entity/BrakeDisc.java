package com.example.demo.entity;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "brake_disc")
public class BrakeDisc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "car_series")
    private String carSeries;

    @Column(name = "car_model")
    private String carModel;

    @Column(name = "position")
    private String position;

    @Column(name = "type")
    private String type;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "surface_treatment")
    private String surfaceTreatment;

    @Column(name = "process")
    private String process;

    @Column(name = "outer_diameter")
    private Double outerDiameter;

    @Column(name = "total_height")
    private Double totalHeight;

    @Column(name = "brake_thickness")
    private Double brakeThickness;

    @Column(name = "center_hole")
    private Double centerHole;

    @Column(name = "mounting_hole")
    private String mountingHole;

    @Column(name = "oe1")
    private String oe1;

    @Column(name = "oe2")
    private String oe2;

    @Column(name = "oe3")
    private String oe3;

    @Column(name = "oe4")
    private String oe4;

    @Column(name = "oe5")
    private String oe5;

    @Column(name = "oe6")
    private String oe6;

    @Column(name = "oe7")
    private String oe7;

    @Column(name = "oe8")
    private String oe8;

    @Column(name = "oe9")
    private String oe9;

    @Column(name = "oe10")
    private String oe10;

    @Column(name = "oe11")
    private String oe11;

    @Column(name = "ek25")
    private String ek25;

    @Column(name = "ek1")
    private String ek1;

    @Column(name = "shengdi_code")
    private String shengdiCode;
}
