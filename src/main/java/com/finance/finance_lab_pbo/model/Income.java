package com.finance.finance_lab_pbo.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Income extends Transaction {
    private String source; // monthly allowance, design fee, etc.
    
    public Income(String source, BigDecimal amount, LocalDate date, String description) {
        super(amount, date, description);
        this.source = source;
    }
    
    @Override
    public String getType() {
        return "INCOME";
    }
    
    // Getter/Setter untuk source
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}