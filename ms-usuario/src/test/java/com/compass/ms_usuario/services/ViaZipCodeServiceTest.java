package com.compass.ms_usuario.services;

import com.compass.ms_usuario.controllers.ViaCepClientController;
import com.compass.ms_usuario.models.Address;
import com.compass.ms_usuario.models.dto.AddressRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ViaZipCodeServiceTest {

    @Mock
    private ViaCepClientController viaCepClientController;

    @InjectMocks
    private ViaZipCodeService viaZipCodeService;

    @Test
    void testSearchAddressByZipCode_Success() {
        String cep = "12345678";
        AddressRequestDTO addressRequestDTO = new AddressRequestDTO();
        addressRequestDTO.setCep(cep);
        addressRequestDTO.setLogradouro("Rua Exemplo");
        addressRequestDTO.setComplemento("Apto 1");
        addressRequestDTO.setBairro("Bairro Exemplo");
        addressRequestDTO.setLocalidade("Cidade Exemplo");
        addressRequestDTO.setUf("UF");

        when(viaCepClientController.getAddressByZipCode(cep)).thenReturn(addressRequestDTO);

        Address result = viaZipCodeService.searchAddressByZipCode(cep);

        assertNotNull(result);
        assertEquals("12345678", result.getZipCode());
        assertEquals("Rua Exemplo", result.getStreet());
        assertEquals("Apto 1", result.getComplement());
        assertEquals("Bairro Exemplo", result.getNeighborhood());
        assertEquals("Cidade Exemplo", result.getCity());
        assertEquals("UF", result.getState());
    }

    @Test
    void testSearchAddressByZipCode_AddressNotFound() {
        String cep = "00000000";
        when(viaCepClientController.getAddressByZipCode(cep)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            viaZipCodeService.searchAddressByZipCode(cep);
        });
        assertEquals("Endereço não encontrado para o CEP: " + cep, exception.getMessage());
    }
}
