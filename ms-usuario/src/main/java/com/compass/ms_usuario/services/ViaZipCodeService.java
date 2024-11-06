package com.compass.ms_usuario.services;

import com.compass.ms_usuario.controllers.ViaCepClientController;
import com.compass.ms_usuario.models.Address;
import com.compass.ms_usuario.models.dto.AddressRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViaZipCodeService {

    private final ViaCepClientController viaCepClientController;

    /**
     * Busca um endereço a partir do CEP fornecido.
     *
     * @param cep O código postal (CEP) para buscar o endereço.
     * @return Um objeto {@link Address} contendo as informações do endereço associado ao CEP.
     * @throws RuntimeException Se o endereço não for encontrado para o CEP fornecido.
     */
    public Address searchAddressByZipCode(String cep) {
        AddressRequestDTO response = viaCepClientController.getAddressByZipCode(cep);

        if (response == null) {
            throw new RuntimeException("Address not found for zip code: " + cep);
        }

        return new Address(
                response.getCep(),
                response.getLogradouro(),
                response.getComplemento(),
                response.getBairro(),
                response.getLocalidade(),
                response.getUf()
        );
    }
}


