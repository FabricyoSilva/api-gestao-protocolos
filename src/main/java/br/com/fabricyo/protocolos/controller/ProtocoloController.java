package br.com.fabricyo.protocolos.controller;

import br.com.fabricyo.protocolos.dto.CriarProtocoloRequest;
import br.com.fabricyo.protocolos.dto.ProtocoloResponse;
import br.com.fabricyo.protocolos.service.ProtocoloService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/protocolos")
public class ProtocoloController {

    private final ProtocoloService service;

    public ProtocoloController(ProtocoloService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProtocoloResponse> criar(
            @Valid @RequestBody CriarProtocoloRequest request,
            UriComponentsBuilder uriBuilder) {

        ProtocoloResponse criado = service.criar(request);

        URI location = uriBuilder
                .path("/protocolos/{id}")
                .buildAndExpand(criado.id())
                .toUri();

        return ResponseEntity.created(location).body(criado);
    }

    @GetMapping
    public List<ProtocoloResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProtocoloResponse buscarPorId(@PathVariable UUID id) {
        return service.buscarPorId(id);
    }
}
