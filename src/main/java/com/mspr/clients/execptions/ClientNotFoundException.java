package com.mspr.clients.execptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(Long id) {
        super(String.format("Client with id=%d not found", id));
    }
    public static ClientNotFoundException of(Long id) {
        return new ClientNotFoundException(id);
    }

}