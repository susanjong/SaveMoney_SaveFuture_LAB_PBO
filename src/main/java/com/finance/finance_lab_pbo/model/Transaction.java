package com.finance.finance_lab_pbo.model;

import java.math.BigDecimal;
import java.time.LocalDate;

// Base class untuk Inheritance
public abstract class Transaction {
    protected int id;
    protected BigDecimal amount;
    protected LocalDate date;
    protected String description;
    
    // Encapsulation - protected fields + getters/setters
    public Transaction(BigDecimal amount, LocalDate date, String description) {
        this.amount = amount;
        this.date = date;
        this.description = description;
    }
    
    // Abstract method untuk Polymorphism
    public abstract String getType();
    
    // Getters/Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}