package com.mspr.clients.controllers;

import com.mspr.clients.dto.ClientDTO;
import com.mspr.clients.models.queries.PaginationQuery;
import com.mspr.clients.models.requests.ClientCreateRequest;
import com.mspr.clients.models.requests.ClientUpdateRequest;
import com.mspr.clients.models.responses.PagedResult;
import com.mspr.clients.services.ClientService;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@SecurityScheme(
        type = SecuritySchemeType.APIKEY, name = "Authorization", scheme = "bearer", in = SecuritySchemeIn.HEADER
)
@RestController
@RequestMapping("/ms-clients/clients")
@SecurityRequirement(name = "Authorization")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PreAuthorize("hasRole('CLIENTS_READ')")
    @GetMapping
    public ResponseEntity<PagedResult<ClientDTO>> getClients(
            @RequestParam(name = "page", defaultValue = "1") Integer pageNumber,
            @RequestParam(name = "itemsPerPage", defaultValue = "10") Integer itemsPerPage) {
        PaginationQuery query = new PaginationQuery(pageNumber, itemsPerPage);
        return ResponseEntity.ok(clientService.findAll(query));
    }


    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ClientDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content),
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTS_READ')")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        return clientService.getClientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());

    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client created", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ClientDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content),
    })
    @PostMapping
    @PreAuthorize("hasRole('CLIENTS_ADD')")
    public ResponseEntity<ClientDTO> createClient(@RequestBody @Validated ClientCreateRequest clientCreateRequest) {
        ClientDTO createdClientDTO = clientService.createClient(clientCreateRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdClientDTO.id())
                .toUri();
        return ResponseEntity.created(location).body(createdClientDTO);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client updated", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ClientDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content),
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTS_EDIT')")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id,
            @RequestBody @Validated ClientUpdateRequest clientUpdateRequest) {
        ClientDTO updatedClient = clientService.updateClient(id, clientUpdateRequest);
        return ResponseEntity.ok(updatedClient);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Client deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Client not found", content = @Content),
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTS_DELETE')")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
