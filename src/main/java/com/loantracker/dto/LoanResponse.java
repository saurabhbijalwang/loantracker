package com.loantracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for loan response.
 * Contains all relevant loan information for API responses.
 */
public class LoanResponse {
    private int id;
    private String borrowerName;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private int loanTermMonths;
    private LocalDate startDate;
    private String status;
    private BigDecimal remainingBalance;
    private int paymentsRemaining;

    public LoanResponse() {
    }

    public LoanResponse(int id, String borrowerName, BigDecimal principalAmount,
                       BigDecimal interestRate, int loanTermMonths, LocalDate startDate,
                       String status) {
        this.id = id;
        this.borrowerName = borrowerName;
        this.principalAmount = principalAmount;
        this.interestRate = interestRate;
        this.loanTermMonths = loanTermMonths;
        this.startDate = startDate;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public int getPaymentsRemaining() {
        return paymentsRemaining;
    }

    public void setPaymentsRemaining(int paymentsRemaining) {
        this.paymentsRemaining = paymentsRemaining;
    }
}
