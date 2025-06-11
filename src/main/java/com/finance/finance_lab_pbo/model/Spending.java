package com.finance.finance_lab_pbo.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Spending extends Transaction {
    private String category; // food & drinks, entertainment, health, etc.
    
    public Spending(String category, BigDecimal amount, LocalDate date, String description) {
        super(amount, date, description);
        this.category = category;
    }
    
    @Override
    public String getType() {
        return "SPENDING";
    }
    
    // Getter/Setter untuk category
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}