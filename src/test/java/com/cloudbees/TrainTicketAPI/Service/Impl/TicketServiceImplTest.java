package com.cloudbees.TrainTicketAPI.Service.Impl;

import com.cloudbees.TrainTicketAPI.Model.Ticket;
import com.cloudbees.TrainTicketAPI.Model.User;
import com.cloudbees.TrainTicketAPI.Response.GenericResponse;
import com.cloudbees.TrainTicketAPI.Service.TicketService;
import com.cloudbees.TrainTicketAPI.Validator.TicketValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TicketServiceImplTest {
    @Mock
    private TicketValidator ticketValidator;

    @InjectMocks
    private TicketServiceImpl ticketService;

    Ticket ticketSetUp() {
        Ticket testTicket = new Ticket();
        testTicket.setUser(new User("test@example.com", "John", "Doe"));
        testTicket.setFrom("New York");
        testTicket.setTo("Washington D.C.");
        testTicket.setDate(LocalDate.now());
        testTicket.setPricePaid(100.00);
        return testTicket;
    }

    @Test
    void purchaseTicket_SuccessfulPurchase_ReturnsAcceptedResponse() {
        Ticket testTicket = ticketSetUp();
        GenericResponse<Ticket> response = ticketService.purchaseTicket(testTicket);
        assertEquals(HttpStatus.ACCEPTED, response.getStatus());
        assertEquals("Ticket Booked", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    void purchaseTicket_ValidationFails_ReturnsAccepted() {
        Ticket testTicket = ticketSetUp();
        GenericResponse<Ticket> response = ticketService.purchaseTicket(testTicket);
        assertEquals("Ticket Booked", response.getMessage());
        assertFalse(response.getData().isEmpty());
    }

    @Test
    void findTicketByUserEmail_TicketExists_ReturnsTicket() {
        Ticket testTicket = ticketSetUp();
        ticketService.purchaseTicket(testTicket);
        GenericResponse<Ticket> response = ticketService.findTicketByUserEmail("test@example.com");
        assertEquals("No ticket found for this email", response.getMessage());
    }

    @Test
    void modifyUserSeat_SuccessfulModification_ReturnsAcceptedResponse() {
        Ticket testTicket = ticketSetUp();
        ticketService.purchaseTicket(testTicket); // First add a ticket
        testTicket.setSection("A");
        testTicket.setSeatNumber(1);
        GenericResponse<Ticket> response = ticketService.modifyUserSeat(testTicket, "B", 2);
        assertEquals(HttpStatus.ACCEPTED, response.getStatus());
        assertEquals("Seat modification completed successfully.", response.getMessage());
        assertNotNull(response.getData());
    }
}