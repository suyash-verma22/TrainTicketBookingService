package com.cloudbees.TrainTicketAPI.Validator;

import com.cloudbees.TrainTicketAPI.Model.Ticket;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class TicketValidator {
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    public void validateTicket(Ticket ticket, List<Ticket> tickets, List<String> errors){
        String email = ticket.getUser().getEmail();
        LocalDate date = ticket.getDate();
        String from = ticket.getFrom();
        String to = ticket.getTo();

        // validate all mandatory fields
        checkRequiredFields(ticket,errors);
        if(!errors.isEmpty()){
            return;
        }
        // check valid email
        if(!validateEmail(email)){
            errors.add("Please enter valid email to book ticket.");
        }
        // check valid date
        if(!validateTicket(email, from, to, date, tickets)){
            errors.add("There is already a ticket booked for the route on this date: " + date);
        }
    }

    public void checkRequiredFields(Ticket ticket, List<String> errors){
        if(ObjectUtils.isEmpty(ticket.getUser().getEmail())){
            errors.add("Email can not be null/empty.");
        }
        if(ObjectUtils.isEmpty(ticket.getUser().getFirstName())){
            errors.add("First name can not be null/empty.");
        }
        if(ObjectUtils.isEmpty(ticket.getFrom()) || ObjectUtils.isEmpty(ticket.getTo())){
            errors.add("Source/Destination can not be null/empty.");
        }
        if(ObjectUtils.isEmpty(ticket.getPricePaid())){
            errors.add("Ticket Price can not be null/empty.");
        }
    }

    private boolean validateEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean validateTicket(String email, String from, String to, LocalDate date, List<Ticket> tickets){
        Ticket ticket = tickets.stream()
                .filter(t -> t.getUser().getEmail().equals(email))
                .findFirst()
                .orElse(null);
        return ObjectUtils.isEmpty(ticket) || !date.equals(ticket.getDate())
                || !from.equalsIgnoreCase(ticket.getFrom()) || !to.equalsIgnoreCase(ticket.getTo());
    }

    public void validateSeat(String section, int seatNo, List<String> errors){
        if(!section.equalsIgnoreCase("A") && !section.equalsIgnoreCase("B")){
            errors.add("Section " + section + " doest not exist on train. Please select a available section.");
        }
        if(seatNo <= 0 || seatNo > 30){
            errors.add("Seat number " + seatNo + " doest not exist. Please select a valid seat.");
        }
    }
}
