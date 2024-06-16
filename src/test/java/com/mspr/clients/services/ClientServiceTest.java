package com.mspr.clients.services;

import com.mspr.clients.dto.ClientDTO;
import com.mspr.clients.execptions.ClientDuplicateUsernameException;
import com.mspr.clients.execptions.ClientNotFoundException;
import com.mspr.clients.models.entities.Address;
import com.mspr.clients.models.entities.Client;
import com.mspr.clients.models.entities.Company;
import com.mspr.clients.models.queries.PaginationQuery;
import com.mspr.clients.models.requests.ClientCreateRequest;
import com.mspr.clients.models.requests.ClientUpdateRequest;
import com.mspr.clients.models.responses.PagedResult;
import com.mspr.clients.repositories.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientRabbitMessageSender clientRabbitMessageSender;

    @InjectMocks
    private ClientService clientService;

    private Client client1, client2, client3;
    private ClientCreateRequest clientCreateRequest;
    private ClientUpdateRequest clientUpdateRequest;
    private PaginationQuery query;
    private Pageable pageable;
    private Page<ClientDTO> page;

    @BeforeEach
    void setUp() {
        client1 = new Client(1L, "username1", "firstname1", "lastname1", LocalDateTime.now(), LocalDateTime.now(),
                new Address("street1", "postalCode1", "city1"),
                new Company("company1", "email1", "phone1"));
        client2 = new Client(2L, "username2", "firstname2", "lastname2", LocalDateTime.now(), LocalDateTime.now(),
                new Address("street2", "postalCode2", "city2"),
                new Company("company2", "email2", "phone2"));
        client3 = new Client(3L, "username3", "firstname3", "lastname3", LocalDateTime.now(), LocalDateTime.now(),
                new Address("street3", "postalCode3", "city3"),
                new Company("company3", "email3", "phone3"));

        clientCreateRequest = new ClientCreateRequest("username1", "firstname1", "lastname1",
                "street1", "postalCode1", "city1",
                "company1", "email1", "phone1");
        clientUpdateRequest = new ClientUpdateRequest("username1", "firstname1", "lastname1",
                "street1", "postalCode1", "city1",
                "company1", "email1", "phone1");

        query = new PaginationQuery(1, 10);
        pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        page = new PageImpl<>(List.of(
                ClientDTO.fromModel(client1),
                ClientDTO.fromModel(client2),
                ClientDTO.fromModel(client3))
        );
    }

    @Test
    void findAllClient_Should_ReturnsPagedClientList() {
        // Given
        when(clientRepository.findAll(pageable)).thenReturn(page);

        // When
        PagedResult<ClientDTO> result = clientService.findAll(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.data()).hasSize(3);
        assertThat(result.data())
                .extracting(ClientDTO::username, ClientDTO::firstname, ClientDTO::lastname)
                .containsExactlyInAnyOrder(
                        tuple("username1", "firstname1", "lastname1"),
                        tuple("username2", "firstname2", "lastname2"),
                        tuple("username3", "firstname3", "lastname3")
                );
        verify(clientRepository, times(1)).findAll(pageable);
    }

    @Test
    void getClientById_WhenClientExists_Should_ReturnClientDTO() {
        // Given
        when(clientRepository.findById(client1.getId())).thenReturn(Optional.of(client1));

        // When
        Optional<ClientDTO> result = clientService.getClientById(client1.getId());

        // Then
        assertThat(result).isPresent().contains(ClientDTO.fromModel(client1));
        verify(clientRepository, times(1)).findById(client1.getId());
    }

    @Test
    void getClientById_WhenClientNotExist_Should_ThrowClientNotFoundException() {
        // Given
        Long nonExistentId = 10L;
        when(clientRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> clientService.getClientById(nonExistentId))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessageContaining(String.format("Client with id=%d not found", nonExistentId));
        verify(clientRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void createClient_Should_ReturnClientDTO() {
        // Given
        when(clientRepository.save(any(Client.class))).thenReturn(client1);

        // When
        ClientDTO result = clientService.createClient(clientCreateRequest);

        // Then
        assertThat(result).isNotNull().isEqualTo(ClientDTO.fromModel(client1));
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void createClient_WithDuplicateUsername_Should_ThrowClientDuplicateUsernameException() {
        // Given
        when(clientRepository.save(any(Client.class))).thenThrow(new DataIntegrityViolationException("Duplicate username"));

        // When & Then
        assertThatThrownBy(() -> clientService.createClient(clientCreateRequest))
                .isInstanceOf(ClientDuplicateUsernameException.class)
                .hasMessageContaining(String.format("Client with username=%s already exists", clientCreateRequest.username()));
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void updateClient_Should_ReturnClientDTO() {
        when(clientRepository.findById(client1.getId())).thenReturn(Optional.of(client1));
        when(clientRepository.save(any(Client.class))).thenReturn(client1);

        ClientDTO result = clientService.updateClient(client1.getId(), clientUpdateRequest);

        assertThat(result).isNotNull().isEqualTo(ClientDTO.fromModel(client1));
        verify(clientRepository, times(1)).findById(client1.getId());
        verify(clientRepository, times(1)).save(any(Client.class));
    }

    @Test
    void deleteClient_Should_DeleteClient() {
        when(clientRepository.findById(client1.getId())).thenReturn(Optional.of(client1));

        clientService.deleteClient(client1.getId());

        verify(clientRepository, times(1)).findById(client1.getId());
        verify(clientRepository, times(1)).delete(client1);
    }

    @Test
    @Disabled
    void testTest() {
        assertThat(true).isFalse();
    }
}
