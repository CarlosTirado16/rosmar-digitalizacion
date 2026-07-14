package com.rosmar.digitalizacion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "items_ssop")
public class ItemSSOP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "registro_id", nullable = false)
    private RegistroSSOP registro;

    @Column
    private String mpsReferenceNumber;

    @Column(nullable = false)
    private String area;

    @Column
    private String status;

    @Column(length = 500)
    private String deviation;

    @Column
    private Boolean rewashed;

    @Column
    private Boolean rinsed;

    @Column
    private Boolean sanitized;

    @Column
    private Boolean notInUse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Seccion seccion;

    @Enumerated(EnumType.STRING)
    @Column
    private Frecuencia frecuencia;

    public enum Frecuencia {
        DAILY, ONCE_A_WEEK
    }

    public enum Seccion {
        PROCESSING_AREA, PACKAGING_AREA
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RegistroSSOP getRegistro() { return registro; }
    public void setRegistro(RegistroSSOP registro) { this.registro = registro; }

    public String getMpsReferenceNumber() { return mpsReferenceNumber; }
    public void setMpsReferenceNumber(String mpsReferenceNumber) { this.mpsReferenceNumber = mpsReferenceNumber; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeviation() { return deviation; }
    public void setDeviation(String deviation) { this.deviation = deviation; }

    public Boolean getRewashed() { return rewashed; }
    public void setRewashed(Boolean rewashed) { this.rewashed = rewashed; }

    public Boolean getRinsed() { return rinsed; }
    public void setRinsed(Boolean rinsed) { this.rinsed = rinsed; }

    public Boolean getSanitized() { return sanitized; }
    public void setSanitized(Boolean sanitized) { this.sanitized = sanitized; }

    public Boolean getNotInUse() { return notInUse; }
    public void setNotInUse(Boolean notInUse) { this.notInUse = notInUse; }

    public Seccion getSeccion() { return seccion; }
    public void setSeccion(Seccion seccion) { this.seccion = seccion; }

    public Frecuencia getFrecuencia() { return frecuencia; }
    public void setFrecuencia(Frecuencia frecuencia) { this.frecuencia = frecuencia; }
}