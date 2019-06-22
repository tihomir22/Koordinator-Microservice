package com.koordinator.epsilon.Koordinator.servicios;

import com.koordinator.epsilon.Koordinator.entidades.PrecioActivo;
import com.koordinator.epsilon.Koordinator.entidades.RapidApiPrecio;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class PeticionesTerceros {


    public PrecioActivo getLivePriceRapidApi(String parBase, String parContra)
    {
        final String uri = "https://bravenewcoin-v1.p.rapidapi.com/convert?qty=1&from="+parBase.toLowerCase()+"&to="+parContra.toLowerCase();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Host","bravenewcoin-v1.p.rapidapi.com");
        headers.set("X-RapidAPI-Key","43e75f11e6msh3b0e1b3d9f97b63p1c4c8fjsn357e56bca235");
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<RapidApiPrecio> respEntity2=restTemplate.exchange(uri, HttpMethod.GET, entity, RapidApiPrecio.class);
        if(respEntity2.getBody().isSuccess()){
            PrecioActivo returnValue=new PrecioActivo(parBase+parContra,respEntity2.getBody().getTo_quantity(),parBase,parContra);
            return returnValue;
        }else{
            return null;
        }

    }








}
