package com.cloudbees.TrainTicketAPI.Service.Impl;

import com.cloudbees.TrainTicketAPI.Model.Ticket;
import com.cloudbees.TrainTicketAPI.Response.GenericResponse;
import com.cloudbees.TrainTicketAPI.Service.TicketService;
import com.cloudbees.TrainTicketAPI.Validator.TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketValidator ticketValidator;

    private final List<Ticket> tickets = new ArrayList<>();
    private static final int SEATS_PER_SECTION = 30;
    private boolean[] sectionASeats = new boolean[SEATS_PER_SECTION];
    private boolean[] sectionBSeats = new boolean[SEATS_PER_SECTION];

    @Override
    public GenericResponse<Ticket> purchaseTicket(Ticket ticket) {
        List<String> errors = new ArrayList<>();

        ticketValidator.validateTicket(ticket,tickets,errors);

        if(!errors.isEmpty()){
            return new GenericResponse<>(Collections.singletonList(ticket),"Failed to book ticket",errors, HttpStatus.BAD_REQUEST);
        }

        String section = tickets.size() % 2 == 0 ? "A" : "B";
        int seatNumber = assignSeat(section);
        if (seatNumber == -1) {
            return null;
        }
        ticket.setSection(section);
        ticket.setSeatNumber(seatNumber);
        tickets.add(ticket);
        return new GenericResponse<>(Collections.singletonList(ticket),"Ticket Booked",null,HttpStatus.ACCEPTED);
    }

    private int assignSeat(String section) {
        for (int i = 0; i < SEATS_PER_SECTION; i++) {
            boolean[] seats = section.equals("A") ? sectionASeats : sectionBSeats;
            if (!seats[i]) {
                seats[i] = true;
                return i + 1;
            }
        }
        return -1;
    }

    @Override
    public GenericResponse<Ticket> findTicketByUserEmail(String email) {
        GenericResponse<Ticket> response;
        Ticket ticket = tickets.stream()
                .filter(t -> t.getUser().getEmail().equals(email))
                .findFirst()
                .orElse(null);
        if(ObjectUtils.isEmpty(ticket)){
            return new GenericResponse<>(null,"No ticket found for this email",null,HttpStatus.NOT_FOUND);
        }else{
            return new GenericResponse<>(Collections.singletonList(ticket),"Ticket found",null,HttpStatus.ACCEPTED);
        }
    }

    @Override
    public GenericResponse<Ticket> getTicketsBySection(String section) {
        GenericResponse<Ticket> response;
        List<Ticket> ticketsInSection = new ArrayList<>();
        ticketsInSection = tickets.stream()
                .filter(t -> t.getSection().equalsIgnoreCase(section))
                .collect(Collectors.toList());
        if(ObjectUtils.isEmpty(ticketsInSection)){
            return new GenericResponse<>(ticketsInSection,"No seats are Booked in Section " + section,null,HttpStatus.ACCEPTED);
        }else{
            return new GenericResponse<>(ticketsInSection,"Ticket found",null,HttpStatus.ACCEPTED);
        }
    }

    @Override
    public boolean removeUser(String email) {
        List<Ticket> tickets = findTicketByUserEmail(email).getData();
        if (!tickets.isEmpty()) {
            Ticket ticket = tickets.get(0);
            boolean[] seats = ticket.getSection().equals("A") ? sectionASeats : sectionBSeats;
            seats[ticket.getSeatNumber() - 1] = false;
            return tickets.remove(ticket);
        }
        return false;
    }

    @Override
    public boolean modifyUserSeat(String email, String newSection) {
        List<Ticket> tickets = findTicketByUserEmail(email).getData();
        if (!tickets.isEmpty()) {
            Ticket ticket = tickets.get(0);
            if (ticket != null && !ticket.getSection().equals(newSection)) {
                int newSeatNumber = assignSeat(newSection);
                if (newSeatNumber == -1) {
                    return false;
                }
                boolean[] oldSeats = ticket.getSection().equals("A") ? sectionASeats : sectionBSeats;
                oldSeats[ticket.getSeatNumber() - 1] = false;

                ticket.setSection(newSection);
                ticket.setSeatNumber(newSeatNumber);
                return true;
            }
        }
        return false;
    }
}
