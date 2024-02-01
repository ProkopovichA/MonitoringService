package com.prokopovich.model;

public enum AuditAction {
    SIGN_IN("SignIn"),
    SIGN_UP("SignUp"),
    SIGN_OUT("SignOut"),
    MONTHLY_DISPLAY_VALUE("MonthlyDisplayValue"),
    DISPLAY_VALUE("DisplayValue"),
    ADD_NEW_INDICATOR_TYPE("AddNewIndicatorType"),
    INPUT_VALUE("InputValue");

    private final String action;

    AuditAction(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }
}
