package com.mspr.clients.config;

import com.mspr.clients.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ClientRepository clientRepository;
    
    @Override
    public void run(String... args) throws Exception {
//        Client client1 = new Client();
//        client1.setFirstname("Keven");
//        client1.setLastname("Kris");
//        client1.setUsername("Kailee.Greenholt73");
//
//        Address address1 = new Address();
//        address1.setStreet("13 rue joannes");
//        address1.setPostalCode("61432-1180");
//        address1.setCity("St. Joseph");
//        client1.setAddress(address1);
//
//        Company company1 = new Company();
//        company1.setCompanyName("Bechtelar LLC");
//        company1.setEmail("compagny1@email.com");
//        company1.setPhoneNumber("0156532456");
//        client1.setCompany(company1);
//
//        clientRepository.save(client1);
//
//        Thread.sleep(2000);
//
//        client1.setFirstname("Keven Dir");
//        clientRepository.save(client1);
//
//        Client client2 = new Client();
//        client2.setFirstname("Jan");
//        client2.setLastname("Schiller");
//        client2.setUsername("jan.schiller");
//
//        Address address2 = new Address();
//        address2.setStreet("13 rue jean marie");
//        address2.setPostalCode("61432-1180");
//        address2.setCity("St. Joseph");
//        client2.setAddress(address2);
//
//        Company company2 = new Company();
//        company2.setCompanyName("Bechtelar LLC");
//        company2.setEmail("compagny2@email.com");
//        company2.setPhoneNumber("0956532456");
//        client2.setCompany(company2);
//
//        clientRepository.save(client2);
    }
}
