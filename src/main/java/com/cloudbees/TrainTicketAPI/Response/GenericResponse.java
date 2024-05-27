package com.cloudbees.TrainTicketAPI.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericResponse<T> {
    private List<T> data;
    private String message;
    private List<String> errors;
    private HttpStatus status;

    public static <T> GenericResponse<T> success(List<T> data, String message, HttpStatus status) {
        return new GenericResponse<>(data, message, null, status);
    }

    public static <T> GenericResponse<T> failure(List<String> errors, String message, HttpStatus status) {
        return new GenericResponse<>(null, message, errors, status);
    }
}
