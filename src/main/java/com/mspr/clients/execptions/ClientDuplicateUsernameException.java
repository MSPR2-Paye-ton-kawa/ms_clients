package com.mspr.clients.execptions;

import io.github.wimdeblauwe.errorhandlingspringbootstarter.ResponseErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@ResponseErrorCode("CLIENT_USERNAME_EXISTS")
public class ClientDuplicateUsernameException extends RuntimeException {
    public ClientDuplicateUsernameException(String username) {
        super(String.format("Client with username=%s already exists", username));
    }

    public static ClientDuplicateUsernameException of(String username) {
        return new ClientDuplicateUsernameException(username);
    }
}
