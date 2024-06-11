package com.mspr.clients.models.entities;

import com.mspr.clients.models.enums.RabbitMessageType;

public record ClientMessage(RabbitMessageType type, Client payload){
}
