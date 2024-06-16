package com.mspr.clients.models;

import com.mspr.clients.models.entities.Address;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressTest {
    @Test
    void testAddressAllArgsConstructor() {
        Address address = new Address("street", "postalCode", "city");

        assertThat(address).isNotNull();
        assertThat(address.getStreet()).isEqualTo("street");
        assertThat(address.getPostalCode()).isEqualTo("postalCode");
        assertThat(address.getCity()).isEqualTo("city");
    }

    @Test
    void testAddressSettersAndGetters() {
        Address address = new Address();

        address.setStreet("street");
        address.setPostalCode("postalCode");
        address.setCity("city");

        assertThat(address).isNotNull();
        assertThat(address.getStreet()).isEqualTo("street");
        assertThat(address.getPostalCode()).isEqualTo("postalCode");
        assertThat(address.getCity()).isEqualTo("city");
    }
}
