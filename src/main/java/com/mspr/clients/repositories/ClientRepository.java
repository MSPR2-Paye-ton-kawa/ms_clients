package com.mspr.clients.repositories;

import com.mspr.clients.models.Client;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, String> {
}
