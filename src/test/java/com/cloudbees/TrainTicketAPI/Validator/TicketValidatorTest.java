package com.cloudbees.TrainTicketAPI.Validator;

import com.cloudbees.TrainTicketAPI.Model.Ticket;
import com.cloudbees.TrainTicketAPI.Model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TicketValidatorTest {

    TicketValidator validator = new TicketValidator();

    @Test
    void testCheckRequiredFields_MissingFields_AddsAppropriateErrors() {
        Ticket ticket = new Ticket();
        ticket.setUser(new User());
        List<String> errors = new ArrayList<>();

        validator.checkRequiredFields(ticket, errors);

        assertEquals(3, errors.size());
        assertTrue(errors.contains("Email can not be null/empty."));
        assertTrue(errors.contains("First name can not be null/empty."));
        assertTrue(errors.contains("Source/Destination can not be null/empty."));
    }

    @Test
    void testValidateTicket_TicketExists_ReturnsFalse() {
        Ticket existingTicket = new Ticket();
        User user = new User();
        user.setEmail("user@example.com");
        existingTicket.setUser(user);
        existingTicket.setDate(LocalDate.now());
        existingTicket.setFrom("London");
        existingTicket.setTo("Paris");

        assertFalse(validator.validateTicket("user@example.com", "London", "Paris", LocalDate.now(), Collections.singletonList(existingTicket)));
    }

    @Test
    void testValidateTicket_TicketDoesNotExist_ReturnsTrue() {
        List<Ticket> tickets = Arrays.asList();
        assertTrue(validator.validateTicket("user@example.com", "London", "Paris", LocalDate.now(), tickets));
    }

    @Test
    void testValidateSeat_ValidSectionAndSeatNo_NoErrorsAdded() {
        List<String> errors = new ArrayList<>();
        validator.validateSeat("A", 5, errors);

        assertTrue(errors.isEmpty());
    }

    @Test
    void testValidateSeat_InvalidSection_ErrorsAdded() {
        List<String> errors = new ArrayList<>();
        validator.validateSeat("C", 5, errors);

        assertEquals(1, errors.size());
        assertTrue(errors.contains("Section C doest not exist on train. Please select a available section."));
    }

    @Test
    void testValidateSeat_InvalidSeatNo_ErrorsAdded() {
        List<String> errors = new ArrayList<>();
        validator.validateSeat("A", 31, errors);

        assertEquals(1, errors.size());
        assertTrue(errors.contains("Seat number 31 doest not exist. Please select a valid seat."));
    }
}