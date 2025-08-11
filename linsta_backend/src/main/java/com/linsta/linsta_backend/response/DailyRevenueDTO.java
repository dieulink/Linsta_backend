package com.linsta.linsta_backend.response;

import java.time.LocalDate;

public class DailyRevenueDTO {
    private LocalDate date;
    private Long revenue;

    public DailyRevenueDTO(LocalDate date, Long revenue) {
        this.date = date;
        this.revenue = revenue;
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getRevenue() {
        return revenue;
    }
}
