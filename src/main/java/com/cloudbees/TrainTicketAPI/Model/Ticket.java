package com.cloudbees.TrainTicketAPI.Model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Ticket {
    private String from;
    private String to;
    private User user;
    private double pricePaid;
    private String section;
    private int seatNumber;
    private LocalDate date;
}
