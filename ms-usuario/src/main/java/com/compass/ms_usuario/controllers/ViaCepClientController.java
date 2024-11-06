package com.compass.ms_usuario.controllers;

import com.compass.ms_usuario.models.dto.AddressRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "viaCepClient", url = "https://viacep.com.br/ws/")
public interface ViaCepClientController {

    @GetMapping("{cep}/json")
    AddressRequestDTO getAddressByZipCode(@PathVariable("cep") String cep);
}
