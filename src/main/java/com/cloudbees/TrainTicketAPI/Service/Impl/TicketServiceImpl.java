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
    public GenericResponse<Ticket> removeUser(Ticket ticket) {
        Ticket removedTicket = findByTicket(ticket);
        if (!tickets.isEmpty()) {
            removedTicket = tickets.get(0);
            boolean[] seats = removedTicket.getSection().equals("A") ? sectionASeats : sectionBSeats;
            seats[removedTicket.getSeatNumber() - 1] = false;
            tickets.remove(removedTicket);
            return new GenericResponse<Ticket>(Collections.singletonList(removedTicket),"Ticket successfully cancelled",null,HttpStatus.ACCEPTED);
        }
        return new GenericResponse<Ticket>(null,"No Ticket found for specified user",null,HttpStatus.ACCEPTED);
    }

    @Override
    public GenericResponse<Ticket> modifyUserSeat(Ticket ticket, String newSection, int newSeatNumber) {
        Ticket ticketFound = findByTicket(ticket);
        List<String> errors = new ArrayList<>();
        if(ticketFound == null){
            return new GenericResponse<Ticket>(Collections.singletonList(ticket),"Ticket not found.",errors,HttpStatus.ACCEPTED);
        }
        ticketValidator.validateSeat(newSection,newSeatNumber,errors);
        if(!errors.isEmpty()){
            return new GenericResponse<Ticket>(Collections.singletonList(ticket),"Seat modification encountered a error.",errors,HttpStatus.BAD_REQUEST);
        }
        boolean[] oldSeats = ticketFound.getSection().equals("A") ? sectionASeats : sectionBSeats;
        oldSeats[ticketFound.getSeatNumber() - 1] = false;

        ticketFound.setSection(newSection);
        ticketFound.setSeatNumber(newSeatNumber);
        return new GenericResponse<Ticket>(Collections.singletonList(ticketFound),"Seat modification completed successfully.",errors,HttpStatus.ACCEPTED);
    }

    private Ticket findByTicket(Ticket ticket){
        List<Ticket> ticketsOfUser = findTicketByUserEmail(ticket.getUser().getEmail()).getData();
        if(ticketsOfUser == null){
            return null;
        }
        for(Ticket checkTicket : ticketsOfUser){
            if(checkTicket.getDate().equals(ticket.getDate()) && checkTicket.getFrom().equalsIgnoreCase(ticket.getFrom()) && checkTicket.getTo().equalsIgnoreCase(ticket.getTo())){
                return checkTicket;
            }
        }
        return null;
    }
}
