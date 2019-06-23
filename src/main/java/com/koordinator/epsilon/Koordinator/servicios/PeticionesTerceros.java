package com.koordinator.epsilon.Koordinator.servicios;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import com.koordinator.epsilon.Koordinator.Excepciones.ActivoNoEncontradoException;
import com.koordinator.epsilon.Koordinator.entidades.PrecioActivo;
import com.koordinator.epsilon.Koordinator.entidades.RapidApiPrecio;
import net.minidev.json.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.Option;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
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

    public PrecioActivo getCryptoCompareApi(String parBase, String parContra) {
        try {
            final String uri = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=" + parBase.toUpperCase() + "&tsyms=" + parContra.toUpperCase() + "&api_key=6df543455629ca3d59e3d3a38cc6b7db7a922fdfbf6005e9b8c0a126731374cc";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("body", headers);
            ResponseEntity<ObjectNode> respEntity2 = restTemplate.exchange(uri, HttpMethod.GET, entity, ObjectNode.class);
            JsonNode highDepth = respEntity2.getBody().get(parBase).get(parContra);
            if (!highDepth.isNull()) {
                return new PrecioActivo(parBase + parContra, highDepth.asDouble(), parBase, parContra);
            } else {
                return null;
            }
        }catch (NullPointerException ex){
            throw new ActivoNoEncontradoException("El activo " + parBase.toUpperCase()+"/"+parContra.toUpperCase()+" no existe");
        }

    }



}









