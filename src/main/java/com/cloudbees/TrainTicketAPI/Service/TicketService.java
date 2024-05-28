package com.cloudbees.TrainTicketAPI.Service;

import com.cloudbees.TrainTicketAPI.Model.Ticket;
import com.cloudbees.TrainTicketAPI.Response.GenericResponse;

import java.util.List;

public interface TicketService {

    public GenericResponse<Ticket> purchaseTicket(Ticket ticket);

    public GenericResponse<Ticket> findTicketByUserEmail(String email);

    public GenericResponse<Ticket> getTicketsBySection(String section);

    public GenericResponse<Ticket> removeUser(Ticket ticket);

    public GenericResponse<Ticket> modifyUserSeat(Ticket ticket, String newSection, int seatNo);
}
