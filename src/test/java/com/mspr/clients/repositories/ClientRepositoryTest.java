package com.mspr.clients.repositories;

import com.mspr.clients.models.entities.Address;
import com.mspr.clients.models.entities.Client;
import com.mspr.clients.models.entities.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
class ClientRepositoryTest {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        Address address1 = new Address("1 rue de la paix", "75000", "Paris");
        Address address2 = new Address("2 rue de la paix", "75000", "Paris");
        Address address3 = new Address("3 rue de la paix", "75000", "Paris");

        Company company1 = new Company("Company1", "company@email.com", "123456789");
        Company company2 = new Company("Company2", "company2@email.com", "223456789");
        Company company3 = new Company("Company3", "company3@email.com", "3123456789");

        Client client1 = new Client("Client1", "John", "Doe", address1, company1);
        Client client2 = new Client("Client2", "Jane", "Doe", address2, company2);
        Client client3 = new Client("Client3", "Jack", "Doe", address3, company3);

        entityManager.persist(client1);
        entityManager.persist(client2);
        entityManager.persist(client3);
    }

    @Test
    void findAll_Should_ReturnsClientsList() {
        assertThat(clientRepository.findAll()).hasSize(3);
    }

    @Test
    void findAll_Should_ReturnClientsListWithCorrectData() {
        assertThat(clientRepository.findAll()).extracting(Client::getFirstname, Client::getLastname).containsExactlyInAnyOrder(tuple("John", "Doe"), tuple("Jane", "Doe"), tuple("Jack", "Doe"));
    }

    @Test
    void findById_Should_ReturnClient() {
        // save new client
        Address address = new Address("5 rue de la paix", "75000", "Paris");
        Company company = new Company("Company5", "company5@email.com", "5123456789");
        Client savedClient = clientRepository.save(new Client("Client5", "John", "Doe", address, company));


        Optional<Client> client = clientRepository.findById(savedClient.getId());

        assertThat(client).isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c)
                            .hasFieldOrPropertyWithValue("username", "Client5")
                            .hasFieldOrPropertyWithValue("firstname", "John")
                            .hasFieldOrPropertyWithValue("lastname", "Doe");
                });
    }

    @Test
    void save_Should_InsertNewClient() {
        Address address = new Address("4 rue de la paix", "75000", "Paris");
        Company company = new Company("Company4", "company4@email.com", "3123456789");
        Client client = clientRepository.save(new Client("Client4", "John", "Doe", address, company));

        assertThat(client).isNotNull()
                .hasFieldOrPropertyWithValue("firstname", "John")
                .hasFieldOrPropertyWithValue("lastname", "Doe")
                .hasFieldOrPropertyWithValue("address", address)
                .hasFieldOrPropertyWithValue("company", company);
    }

    @Test
    void update_Should_UpdateClient() {
        Address address = new Address("5 rue de la paix", "75000", "Paris");
        Company company = new Company("Company5", "company5@email.com", "5123456789");
        Client savedClient = clientRepository.save(new Client("Client5", "John", "Doe", address, company));

        savedClient.setUsername("ClientEdit");
        savedClient.setFirstname("Jane");
        savedClient.setLastname("Doe");
        savedClient.getAddress().setStreet("6 rue de la paix");
        savedClient.getAddress().setPostalCode("75000");
        savedClient.getAddress().setCity("Lyon");
        savedClient.getCompany().setCompanyName("Company6");
        savedClient.getCompany().setEmail("companyEdit@email.com");
        savedClient.getCompany().setPhoneNumber("6123456789");

        Client updatedClient = clientRepository.save(savedClient);

        assertThat(updatedClient)
                .isNotNull()
                .hasFieldOrPropertyWithValue("username", "ClientEdit")
                .hasFieldOrPropertyWithValue("firstname", "Jane")
                .hasFieldOrPropertyWithValue("lastname", "Doe");

        assertThat(updatedClient.getAddress())
                .isNotNull()
                .hasFieldOrPropertyWithValue("street", "6 rue de la paix")
                .hasFieldOrPropertyWithValue("postalCode", "75000")
                .hasFieldOrPropertyWithValue("city", "Lyon");

        assertThat(updatedClient.getCompany())
                .isNotNull()
                .hasFieldOrPropertyWithValue("companyName", "Company6")
                .hasFieldOrPropertyWithValue("email", "companyEdit@email.com")
                .hasFieldOrPropertyWithValue("phoneNumber", "6123456789");
    }

    @Test
    void delete_Should_DeleteClient() {
        Address address = new Address("7 rue de la paix", "75000", "Paris7");
        Company company = new Company("Company7", "company7@email.com", "7123456789");
        Client savedClient = clientRepository.save(new Client("Client7", "John", "Doe", address, company));

        clientRepository.delete(savedClient);

        assertThat(clientRepository.findById(savedClient.getId())).isNotPresent();

    }
}
