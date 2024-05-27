package com.cloudbees.TrainTicketAPI.Controller;

import com.cloudbees.TrainTicketAPI.Model.Ticket;
import com.cloudbees.TrainTicketAPI.Response.GenericResponse;
import com.cloudbees.TrainTicketAPI.Service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping("/purchase")
    public GenericResponse<Ticket> purchaseTicket(@RequestBody Ticket ticket) {
        return ticketService.purchaseTicket(ticket);
    }

    @GetMapping("/receipt")
    public GenericResponse<Ticket> getReceipt(@RequestParam String email) {
        return ticketService.findTicketByUserEmail(email);
    }

    @GetMapping("/section/{section}")
    public GenericResponse<Ticket> getTicketsBySection(@PathVariable String section) {
        return ticketService.getTicketsBySection(section);
    }

    @DeleteMapping("/remove")
    public boolean removeUser(@RequestParam String email) {
        return ticketService.removeUser(email);
    }

    @PutMapping("/modify")
    public boolean modifyUserSeat(@RequestParam String email, @RequestParam String section) {
        return ticketService.modifyUserSeat(email, section);
    }
}
