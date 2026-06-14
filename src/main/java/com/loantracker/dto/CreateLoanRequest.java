package com.loantracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for creating a new loan.
 * Separates API contracts from internal domain models.
 * Enables validation at the boundary layer.
 */
public class CreateLoanRequest {
    private String borrowerName;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private int loanTermMonths;
    private LocalDate startDate;

    public CreateLoanRequest() {
    }

    public CreateLoanRequest(String borrowerName, BigDecimal principalAmount,
                           BigDecimal interestRate, int loanTermMonths, LocalDate startDate) {
        this.borrowerName = borrowerName;
        this.principalAmount = principalAmount;
        this.interestRate = interestRate;
        this.loanTermMonths = loanTermMonths;
        this.startDate = startDate;
    }

    // Getters and Setters
    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public BigDecimal getPrincipalAmount() {
        return principalAmount;
    }

    public void setPrincipalAmount(BigDecimal principalAmount) {
        this.principalAmount = principalAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public int getLoanTermMonths() {
        return loanTermMonths;
    }

    public void setLoanTermMonths(int loanTermMonths) {
        this.loanTermMonths = loanTermMonths;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
}
