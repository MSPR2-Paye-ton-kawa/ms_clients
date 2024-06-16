package com.mspr.clients.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mspr.clients.config.SecurityConfig;
import com.mspr.clients.controllers.ClientController;
import com.mspr.clients.dto.ClientDTO;
import com.mspr.clients.execptions.ClientDuplicateUsernameException;
import com.mspr.clients.execptions.ClientNotFoundException;
import com.mspr.clients.models.entities.Address;
import com.mspr.clients.models.entities.Company;
import com.mspr.clients.models.queries.PaginationQuery;
import com.mspr.clients.models.requests.ClientCreateRequest;
import com.mspr.clients.models.requests.ClientUpdateRequest;
import com.mspr.clients.models.responses.PagedResult;
import com.mspr.clients.services.ClientService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ClientController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class ClientControllerTest {

    private static final String ROLE_CLIENTS_READ = "ROLE_CLIENTS_READ";
    private static final String ROLE_CLIENTS_ADD = "ROLE_CLIENTS_ADD";
    private static final String ROLE_CLIENTS_EDIT = "ROLE_CLIENTS_EDIT";
    private static final String ROLE_CLIENTS_DELETE = "ROLE_CLIENTS_DELETE";


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;


    @Autowired
    private ObjectMapper objectMapper;

    private ClientDTO clientDTO1, clientDTO2, clientDTO3;
    private ClientCreateRequest validCreateRequest, invalidEmailCreateRequest, duplicateUsernameCreateRequest;
    private ClientUpdateRequest validUpdateRequest, invalidEmailUpdateRequest, duplicateUsernameUpdateRequest;

    private JwtAuthenticationToken simpleToken, readRoleToken, addRoleToken, editRoleToken, deleteRoleToken;

    @BeforeEach
    void setUp() {
        // Create a JWT token for differents roles
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "user")
                .build();

        simpleToken = new JwtAuthenticationToken(jwt, AuthorityUtils.createAuthorityList());
        readRoleToken = new JwtAuthenticationToken(jwt, AuthorityUtils.createAuthorityList(ROLE_CLIENTS_READ));
        addRoleToken = new JwtAuthenticationToken(jwt, AuthorityUtils.createAuthorityList(ROLE_CLIENTS_ADD));
        editRoleToken = new JwtAuthenticationToken(jwt, AuthorityUtils.createAuthorityList(ROLE_CLIENTS_EDIT));
        deleteRoleToken = new JwtAuthenticationToken(jwt, AuthorityUtils.createAuthorityList(ROLE_CLIENTS_DELETE));


        // Create 3 clients
        clientDTO1 = new ClientDTO(1L, "username1", "firstname1", "lastname1", LocalDateTime.now(), LocalDateTime.now(),
                new Address("street1", "postalCode1", "city1"),
                new Company("company1", "email1", "phone1"));
        clientDTO2 = new ClientDTO(2L, "username2", "firstname2", "lastname2", LocalDateTime.now(), LocalDateTime.now(),
                new Address("street2", "postalCode2", "city2"),
                new Company("company2", "email2", "phone2"));
        clientDTO3 = new ClientDTO(3L, "username3", "firstname3", "lastname3", LocalDateTime.now(), LocalDateTime.now(),
                new Address("street3", "postalCode3", "city3"),
                new Company("company3", "email3", "phone3"));

        // Create a valid create request
        validCreateRequest = new ClientCreateRequest("username1", "firstname1", "lastname1",
                "street1", "postalCode1", "city1", "company1", "email@email.com", "phone1");
        invalidEmailCreateRequest = new ClientCreateRequest("username1", "firstname1", "lastname1",
                "street1", "postalCode1", "city1", "company1", "emailemail", "phone1");
        duplicateUsernameCreateRequest = new ClientCreateRequest("username1", "firstname1", "lastname1",
                "street1", "postalCode1", "city1", "company1", "email1@email.com", "phone1");

        // Create a valid update request
        validUpdateRequest = new ClientUpdateRequest("username1", "firstname1", "lastname1",
                "street1", "postalCode1", "city1", "company1", "email@email.com", "phone1");
        invalidEmailUpdateRequest = new ClientUpdateRequest("username1", "firstname1", "lastname1",
                "street1", "postalCode1", "city1", "company1", "emailemail", "phone1");
        duplicateUsernameUpdateRequest = new ClientUpdateRequest("username1", "firstname1", "lastname1",
                "street1", "postalCode1", "city1", "company1", "email@email.com", "phone1");
    }


    @Nested
    @Tag("GET_CLIENTS_TESTS")
    class GetClientsTests {
        @Test
        void getClients_WithReadRole_Should_ReturnsPagedClients() throws Exception {
            // Given
            PaginationQuery query = new PaginationQuery(1, 10);
            when(clientService.findAll(query)).thenReturn(
                    new PagedResult<>(
                            List.of(clientDTO1, clientDTO2, clientDTO3),
                            3,
                            1,
                            10,
                            true,
                            true,
                            false,
                            false
                    )
            );

            // When
            mockMvc.perform(get("/ms-clients/clients").with(authentication(readRoleToken))
                            .param("page", "1")
                            .param("itemsPerPage", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(
                            new PagedResult<>(
                                    List.of(clientDTO1, clientDTO2, clientDTO3),
                                    3,
                                    1,
                                    10,
                                    true,
                                    true,
                                    false,
                                    false
                            )
                    )));
        }

        @Test
        void getClients_WithValidTokenNotHavingReadRole_Should_ReturnsForbidden() throws Exception {
            mockMvc.perform(get("/ms-clients/clients").with(authentication(simpleToken))
                            .param("page", "1")
                            .param("itemsPerPage", "10"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getClients_WithoutToken_Should_ReturnsUnauthorized() throws Exception {
            mockMvc.perform(get("/ms-clients/clients")
                            .param("page", "1")
                            .param("itemsPerPage", "10"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @Tag("GET_CLIENT_BY_ID_TESTS")
    class GetClientByIdTests {
        @Test
        void getClientById_WithReadRole_WhenClientExist_Should_ReturnsClient() throws Exception {
            // Given
            when(clientService.getClientById(1L)).thenReturn(Optional.of(clientDTO1));

            // When
            mockMvc.perform(get("/ms-clients/clients/1").with(authentication(readRoleToken)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(clientDTO1)));
        }

        @Test
        void getClientById_WithReadRole_WhenClientNotExist_Should_ReturnsNotFound() throws Exception {
            // Given
            when(clientService.getClientById(1L)).thenReturn(Optional.empty());

            // When
            mockMvc.perform(get("/ms-clients/clients/1").with(authentication(readRoleToken)))
                    .andExpect(status().isNotFound());
        }

        @Test
        void getClientById_WithValidTokenNotHavingReadRole_Should_ReturnsForbidden() throws Exception {
            mockMvc.perform(get("/ms-clients/clients/1").with(authentication(simpleToken)))
                    .andExpect(status().isForbidden());
        }

        @Test
        void getClientById_WithoutToken_Should_ReturnsUnauthorized() throws Exception {
            mockMvc.perform(get("/ms-clients/clients/1"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @Tag("CREATE_CLIENT_TESTS")
    class CreateClientTests {
        @Test
        void createClient_WithAddRole_WhenValidRequest_Should_ReturnsCreatedClient() throws Exception {
            // Given
            when(clientService.createClient(validCreateRequest)).thenAnswer(invocation -> {
                ClientCreateRequest request = invocation.getArgument(0);
                return clientDTO1;
            });

            // When
            mockMvc.perform(post("/ms-clients/clients").with(authentication(addRoleToken))
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(validCreateRequest))
                            .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andExpect(jsonPath("$.username", is(clientDTO1.username())))
                    .andExpect(jsonPath("$.firstname", is(clientDTO1.firstname())))
                    .andExpect(jsonPath("$.lastname", is(clientDTO1.lastname())))
                    .andExpect(jsonPath("$.address.street", is(clientDTO1.address().getStreet())))
                    .andExpect(jsonPath("$.address.postalCode", is(clientDTO1.address().getPostalCode())))
                    .andExpect(jsonPath("$.address.city", is(clientDTO1.address().getCity())))
                    .andExpect(jsonPath("$.company.companyName", is(clientDTO1.company().getCompanyName())))
                    .andExpect(jsonPath("$.company.email", is(clientDTO1.company().getEmail())))
                    .andExpect(jsonPath("$.company.phoneNumber", is(clientDTO1.company().getPhoneNumber())));
        }

        @Test
        void createClient_WithAddRole_WhenInvalidEmailRequest_Should_ReturnsBadRequest() throws Exception {
            // Given
            // invalid email

            // When
            mockMvc.perform(post("/ms-clients/clients").with(authentication(addRoleToken))
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(invalidEmailCreateRequest))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code", is("VALIDATION_FAILED")))
                    .andExpect(jsonPath("$.message", is("Validation failed for object='clientCreateRequest'. Error count: 1")))
                    .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
                    .andExpect(jsonPath("$.fieldErrors[0].code", is("INVALID_EMAIL")))
                    .andExpect(jsonPath("$.fieldErrors[0].message", is("Email should be valid")))
                    .andExpect(jsonPath("$.fieldErrors[0].property", is("email")))
                    .andExpect(jsonPath("$.fieldErrors[0].rejectedValue", is(invalidEmailCreateRequest.email())));
        }

        @Test
        void createClient_WithAddRole_WhenDuplicateUsernameRequest_Should_ReturnsConflict() throws Exception {
            // Given

            when(clientService.createClient(duplicateUsernameCreateRequest)).thenThrow(new ClientDuplicateUsernameException(duplicateUsernameCreateRequest.username()));

            // When
            mockMvc.perform(post("/ms-clients/clients").with(authentication(addRoleToken))
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(duplicateUsernameCreateRequest))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code", is("CLIENT_USERNAME_EXISTS")))
                    .andExpect(jsonPath("$.message", is(String.format("Client with username=%s already exists", duplicateUsernameCreateRequest.username()))));
        }

        @Test
        void createClient_WithValidTokenNotHavingAddRole_Should_ReturnsForbidden() throws Exception {
            mockMvc.perform(post("/ms-clients/clients").with(authentication(simpleToken))
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(validCreateRequest))
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }

        @Test
        void createClient_WithoutToken_Should_ReturnsUnauthorized() throws Exception {
            mockMvc.perform(post("/ms-clients/clients")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(validCreateRequest))
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @Tag("UPDATE_CLIENT_TESTS")
    class UpdateClientTests {
        @Test
        void updateClient_WithEditRole_WhenValidRequest_Should_ReturnsUpdatedClient() throws Exception {
            // Given

            when(clientService.updateClient(1L, validUpdateRequest)).thenAnswer(invocation -> {
                ClientUpdateRequest request = invocation.getArgument(1);
                return clientDTO1;
            });

            // When
            mockMvc.perform(put("/ms-clients/clients/1").with(authentication(editRoleToken))
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(validUpdateRequest))
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username", is(clientDTO1.username())))
                    .andExpect(jsonPath("$.firstname", is(clientDTO1.firstname())))
                    .andExpect(jsonPath("$.lastname", is(clientDTO1.lastname())))
                    .andExpect(jsonPath("$.address.street", is(clientDTO1.address().getStreet())))
                    .andExpect(jsonPath("$.address.postalCode", is(clientDTO1.address().getPostalCode())))
                    .andExpect(jsonPath("$.address.city", is(clientDTO1.address().getCity())))
                    .andExpect(jsonPath("$.company.companyName", is(clientDTO1.company().getCompanyName())))
                    .andExpect(jsonPath("$.company.email", is(clientDTO1.company().getEmail())))
                    .andExpect(jsonPath("$.company.phoneNumber", is(clientDTO1.company().getPhoneNumber())));
        }

        @Test
        void updateClient_WithEditRole_WhenClientNotExist_Should_ReturnsNotFound() throws Exception {
            // Given
            Long clientId = 1L;
            when(clientService.updateClient(clientId, validUpdateRequest)).thenThrow(new ClientNotFoundException(clientId));

            // When
            mockMvc.perform(put("/ms-clients/clients/1").with(authentication(editRoleToken))
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(validUpdateRequest))
                            .with(csrf()))
                    .andExpect(status().isNotFound());
        }

        @Test
        void updateClient_WithEditRole_WhenInvalidEmailRequest_Should_ReturnsBadRequest() throws Exception {
            // Given
            // invalid email

            // When
            mockMvc.perform(put("/ms-clients/clients/1").with(authentication(editRoleToken))
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(invalidEmailUpdateRequest))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code", is("VALIDATION_FAILED")))
                    .andExpect(jsonPath("$.message", is("Validation failed for object='clientUpdateRequest'. Error count: 1")))
                    .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
                    .andExpect(jsonPath("$.fieldErrors[0].code", is("INVALID_EMAIL")))
                    .andExpect(jsonPath("$.fieldErrors[0].message", is("Email should be valid")))
                    .andExpect(jsonPath("$.fieldErrors[0].property", is("email")))
                    .andExpect(jsonPath("$.fieldErrors[0].rejectedValue", is(invalidEmailUpdateRequest.email())));
        }

        @Test
        void updateClient_WithEditRole_WhenDuplicateUsernameRequest_Should_ReturnsConflict() throws Exception {
            // Given

            when(clientService.updateClient(1L, duplicateUsernameUpdateRequest)).thenThrow(new ClientDuplicateUsernameException(duplicateUsernameUpdateRequest.username()));

            // When
            mockMvc.perform(put("/ms-clients/clients/1").with(authentication(editRoleToken))
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(duplicateUsernameUpdateRequest))
                            .with(csrf()))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code", is("CLIENT_USERNAME_EXISTS")))
                    .andExpect(jsonPath("$.message", is(String.format("Client with username=%s already exists", duplicateUsernameUpdateRequest.username()))));
        }

        @Test
        void updateClient_WithValidTokenNotHavingEditRole_Should_ReturnsForbidden() throws Exception {
            mockMvc.perform(put("/ms-clients/clients/1").with(authentication(simpleToken))
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(validUpdateRequest))
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }

        @Test
        void updateClient_WithoutToken_Should_ReturnsUnauthorized() throws Exception {
            mockMvc.perform(put("/ms-clients/clients/1")
                            .contentType("application/json")
                            .content(objectMapper.writeValueAsString(validUpdateRequest))
                            .with(csrf()))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @Tag("DELETE_CLIENT_TESTS")
    class DeleteClientTests {
        @Test
        void deleteClient_WithDeleteRole_WhenClientExist_Should_ReturnsNoContent() throws Exception {
            // Given

            // When
            mockMvc.perform(delete("/ms-clients/clients/1")
                            .with(csrf())
                            .with(authentication(deleteRoleToken)))
                    .andExpect(status().isNoContent());
        }

        @Test
        void deleteClient_WithDeleteRole_WhenClientNotExist_Should_ReturnsNotFound() throws Exception {
            // Given
            Long clientId = 1L;
            doThrow(new ClientNotFoundException(clientId)).when(clientService).deleteClient(clientId);

            // When
            mockMvc.perform(delete("/ms-clients/clients/1").with(authentication(deleteRoleToken)).with(csrf())
                            .contentType("application/json"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deleteClient_WithValidTokenNotHavingDeleteRole_Should_ReturnsForbidden() throws Exception {
            mockMvc.perform(delete("/ms-clients/clients/1").with(authentication(simpleToken)).with(csrf())
                            .contentType("application/json"))
                    .andExpect(status().isForbidden());
        }

        @Test
        void deleteClient_WithoutToken_Should_ReturnsUnauthorized() throws Exception {
            mockMvc.perform(delete("/ms-clients/clients/1").with(csrf())
                            .contentType("application/json"))
                    .andExpect(status().isUnauthorized());
        }
    }

}
