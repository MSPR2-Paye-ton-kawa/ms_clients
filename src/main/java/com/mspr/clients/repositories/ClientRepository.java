package com.mspr.clients.repositories;

import com.mspr.clients.dto.ClientDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;


import com.mspr.clients.models.entities.Client;

public interface ClientRepository extends CrudRepository<Client, Long> {
    Page<ClientDTO> findAll(Pageable pageable);

}
