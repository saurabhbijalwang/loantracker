package com.loantracker.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Loan entity model
 */
public class Loan {
    private int id;
    private String borrowerName;
    private BigDecimal principalAmount;
    private BigDecimal interestRate;
    private int loanTermMonths;
    private LocalDate startDate;
    private String status;

    public Loan() {
    }

    public Loan(String borrowerName, BigDecimal principalAmount, 
                BigDecimal interestRate, int loanTermMonths, LocalDate startDate) {
        this.borrowerName = borrowerName;
        this.principalAmount = principalAmount;
        this.interestRate = interestRate;
        this.loanTermMonths = loanTermMonths;
        this.startDate = startDate;
        this.status = "ACTIVE";
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

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", borrowerName='" + borrowerName + '\'' +
                ", principalAmount=" + principalAmount +
                ", interestRate=" + interestRate +
                ", loanTermMonths=" + loanTermMonths +
                ", startDate=" + startDate +
                ", status='" + status + '\'' +
                '}';
    }
}
