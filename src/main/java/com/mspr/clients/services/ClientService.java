package com.mspr.clients.services;

import com.mspr.clients.dto.ClientDTO;
import com.mspr.clients.execptions.ClientDuplicateUsernameException;
import com.mspr.clients.execptions.ClientNotFoundException;
import com.mspr.clients.models.entities.Address;
import com.mspr.clients.models.entities.Client;
import com.mspr.clients.models.entities.Company;
import com.mspr.clients.models.enums.RabbitMessageType;
import com.mspr.clients.models.queries.PaginationQuery;
import com.mspr.clients.models.requests.ClientCreateRequest;
import com.mspr.clients.models.requests.ClientUpdateRequest;
import com.mspr.clients.models.responses.PagedResult;
import com.mspr.clients.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientRabbitMessageSender clientRabbitMessageSender;

    public PagedResult<ClientDTO> findAll(PaginationQuery query) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        int pageNumber = Math.max(query.pageNumber() - 1, 0);
        Pageable pageable = PageRequest.of(pageNumber, query.itemsPerPage(), sort);
        Page<ClientDTO> page = clientRepository.findAll(pageable);
        return new PagedResult<>(
                page.getContent(),
                page.getTotalElements(),
                page.getNumber() + 1,
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    public Optional<ClientDTO> getClientById(Long id) {
        return Optional.of(clientRepository.findById(id).map(ClientDTO::fromModel).orElseThrow(() -> ClientNotFoundException.of(id)));
    }
    @Transactional
    public ClientDTO createClient(ClientCreateRequest clientCreateRequest) {
        Client client = new Client(
                clientCreateRequest.username(),
                clientCreateRequest.firstname(),
                clientCreateRequest.lastname(),
                new Address(clientCreateRequest.street(), clientCreateRequest.postalCode(), clientCreateRequest.city()),
                new Company(clientCreateRequest.companyName(), clientCreateRequest.email(), clientCreateRequest.phoneNumber())
        );
        return saveClient(client, clientCreateRequest.username(), RabbitMessageType.CREATED);
    }

    @Transactional
    public ClientDTO updateClient(Long id, ClientUpdateRequest clientUpdateRequest) {

            Client client = clientRepository.findById(id).orElseThrow(() -> ClientNotFoundException.of(id));

            client.setUsername(clientUpdateRequest.username());
            client.setFirstname(clientUpdateRequest.firstname());
            client.setLastname(clientUpdateRequest.lastname());

            client.getAddress().setStreet(clientUpdateRequest.street());
            client.getAddress().setPostalCode(clientUpdateRequest.postalCode());
            client.getAddress().setCity(clientUpdateRequest.city());

            client.getCompany().setCompanyName(clientUpdateRequest.companyName());
            client.getCompany().setEmail(clientUpdateRequest.email());
            client.getCompany().setPhoneNumber(clientUpdateRequest.phoneNumber());

            return saveClient(client, clientUpdateRequest.username(), RabbitMessageType.UPDATED);
    }

    @Transactional
    public void deleteClient(Long id) {
       Client client = clientRepository.findById(id).orElseThrow(() -> ClientNotFoundException.of(id));
       clientRepository.delete(client);
       clientRabbitMessageSender.sendMessageInClientQueue(RabbitMessageType.DELETED, client);
    }
    private ClientDTO saveClient(Client client, String username, RabbitMessageType messageType) {
        try {
            Client savedClient = clientRepository.save(client);
            clientRabbitMessageSender.sendMessageInClientQueue(messageType, savedClient);
            return ClientDTO.fromModel(savedClient);
        } catch (DataIntegrityViolationException ex) {
            if (ex.getMessage().contains("username")) {
                throw ClientDuplicateUsernameException.of(username);
            }
            throw ex;
        }
    }

}
