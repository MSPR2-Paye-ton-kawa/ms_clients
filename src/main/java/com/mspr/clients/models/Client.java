package com.mspr.clients.models;


import jakarta.persistence.*;

@Entity
@Table(name = "clients")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;

    private String username;

    private String firstname;

    private String lastname;

    @Embedded
    private Address address;

    @Embedded
    private Company company;

    public Client() {
    }

    public Client(String name, String username, String firstname, String lastname, Address address, Company company) {
        this.name = name;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.company = company;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public String toString() {
        return "Client{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", username='" + username + '\'' + ", firstname='" + firstname + '\'' + ", lastname='" + lastname + '\'' + ", address=" + address + ", company=" + company + '}';
    }
}
