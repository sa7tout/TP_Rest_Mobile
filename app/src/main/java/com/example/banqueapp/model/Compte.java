package com.example.banqueapp.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "compte", strict = false)
public class Compte {
    @Element(name = "id", required = false)
    private Long id;

    @Element(name = "solde", required = false)
    private double solde;

    @Element(name = "dateCreation", required = false)
    private String dateCreation;

    @Element(name = "type", required = false)
    private TypeCompte type;

    // Default constructor is required for SimpleXML
    public Compte() {}

    // Your existing constructor
    public Compte(double solde, String dateCreation, TypeCompte type) {
        this.solde = solde;
        this.dateCreation = dateCreation;
        this.type = type;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getSolde() {
        return solde;
    }

    public void setSolde(double solde) {
        this.solde = solde;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public TypeCompte getType() {
        return type;
    }

    public void setType(TypeCompte type) {
        this.type = type;
    }
}