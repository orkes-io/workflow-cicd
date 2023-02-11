package io.orkes.conductor.cicd.workflows;


import java.math.BigDecimal;

public class LoanWorkflowInput {

    private String userEmail;

    private BigDecimal loanAmount;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }
}
