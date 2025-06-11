package com.finance.finance_lab_pbo.model;

public enum SpendingCategory {
    FOOD_DRINKS("Food & Drinks"),
    ENTERTAINMENT("Entertainment"), 
    HEALTH("Health"),
    TRANSPORTATION("Transportation"),
    SHOPPING("Shopping"),
    EDUCATION("Education"),
    UTILITIES("Utilities"),
    OTHER("Other");
    
    private final String displayName;
    
    SpendingCategory(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}