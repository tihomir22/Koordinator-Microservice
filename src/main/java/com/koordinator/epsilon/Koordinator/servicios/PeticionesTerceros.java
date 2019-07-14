package com.koordinator.epsilon.Koordinator.servicios;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.koordinator.epsilon.Koordinator.Excepciones.ActivoNoEncontradoException;
import com.koordinator.epsilon.Koordinator.Excepciones.ExceptionResponse;
import com.koordinator.epsilon.Koordinator.StaticTools;
import com.koordinator.epsilon.Koordinator.Validaciones.ValidacionesEstaticas;
import com.koordinator.epsilon.Koordinator.entidades.*;
import com.koordinator.epsilon.Koordinator.repositorio.RepositorioActivos;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
public class PeticionesTerceros {
    @Autowired
    private RepositorioActivos repositorioActivos;


    public ArrayList<RegistroTecnico> calcularMediaMovil(TipoDatoHistorico historico, PrecioActivo resActivo, String nombreIndicador, int periodoTiempo, String intervaloDatosHistoricos, String tipoSeries) {

        ArrayList<DatoHistorico> listaDatosHistoricos = historico.getDato();
        ArrayList<IndicadorTecnico> lista = resActivo.getListadoIndicatores();
        int resBusquedaIndicador = StaticTools.buscarIndicador(lista, nombreIndicador.toUpperCase(), periodoTiempo, intervaloDatosHistoricos, tipoSeries);
        ArrayList<RegistroTecnico> res;
        List<Double> observableRes = TALibDemo.inicializar(listaDatosHistoricos, periodoTiempo, tipoSeries);
        double[] list = observableRes.stream().mapToDouble(Double::doubleValue).toArray();
        switch (nombreIndicador.toUpperCase()) {

            case "SMA":
                res = TALibDemo.ejecutarOperacionSMA(list, periodoTiempo);
                break;
            case "EMA":
                res = TALibDemo.ejecutarOperacionEMA(list, periodoTiempo);
                break;
            case "DEMA":
                res = TALibDemo.ejecutarOperacionDEMA(list, periodoTiempo);
                break;
            case "KAMA":
                res = TALibDemo.ejecutarOperacionKAMA(list, periodoTiempo);
                break;
            case "MAMA":
                res = TALibDemo.ejecutarOperacionMAMA(list, periodoTiempo);
                break;
            case "TEMA":
                res = TALibDemo.ejecutarOperacionTEMA(list, periodoTiempo);
                break;
            case "TMA":
                res = TALibDemo.ejecutarOperacionTMA(list, periodoTiempo);
                break;
            case "WMA":
                res = TALibDemo.ejecutarOperacionWMA(list, periodoTiempo);
                break;
            case "T3":
                res = TALibDemo.ejecutarOperacionT3(list, periodoTiempo);
                break;
            case "RSI":
                res = TALibDemo.RSICall(list, periodoTiempo);
                break;

            default:
                res = null;
                System.out.println("No existe ese indicador!");
        }


        IndicadorTecnico indicadorTecnico = new IndicadorTecnico(nombreIndicador.toUpperCase(), intervaloDatosHistoricos, tipoSeries, periodoTiempo, res);
        if (resBusquedaIndicador == -1) {
            lista.add(indicadorTecnico);
        } else {
            lista.set(resBusquedaIndicador, indicadorTecnico);
        }
        resActivo.setListadoIndicatores(lista);
        this.repositorioActivos.save(resActivo);
        return res;
    }


    public PrecioActivo getLivePriceRapidApi(String parBase, String parContra) {
        final String uri = "https://bravenewcoin-v1.p.rapidapi.com/convert?qty=1&from=" + parBase.toLowerCase() + "&to=" + parContra.toLowerCase();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Host", "bravenewcoin-v1.p.rapidapi.com");
        headers.set("X-RapidAPI-Key", "43e75f11e6msh3b0e1b3d9f97b63p1c4c8fjsn357e56bca235");
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<RapidApiPrecio> respEntity2 = restTemplate.exchange(uri, HttpMethod.GET, entity, RapidApiPrecio.class);
        if (respEntity2.getBody().isSuccess()) {
            PrecioActivo returnValue = new PrecioActivo(parBase + parContra, respEntity2.getBody().getTo_quantity(), new ArrayList<TipoDatoHistorico>(), parBase, parContra);
            return returnValue;
        } else {
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
                return new PrecioActivo(parBase + parContra, highDepth.asDouble(), new ArrayList<TipoDatoHistorico>(), parBase, parContra);
            } else {
                return null;
            }
        } catch (NullPointerException ex) {
            throw new ActivoNoEncontradoException("El activo " + parBase.toUpperCase() + "/" + parContra.toUpperCase() + " no existe");
        }
    }

    public PrecioActivo getBinanceTicker(String parBase, String parContra) {
        try {
            final String uri = "https://api.binance.com/api/v3/ticker/price?symbol=" + parBase.toUpperCase() + parContra.toUpperCase();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("body", headers);
            try {
                ResponseEntity<ObjectNode> respEntity2 = restTemplate.exchange(uri, HttpMethod.GET, entity, ObjectNode.class);
                JsonNode highDepth = respEntity2.getBody().get("price");
                if (!highDepth.isNull()) {
                    return new PrecioActivo(parBase + parContra, highDepth.asDouble(), new ArrayList<TipoDatoHistorico>(), parBase, parContra);
                } else {
                    return null;
                }
            }catch (Exception ex){
                System.out.println("Se ha intentado obtener un activo que no existe en BINANCE");
                return null;
            }

        } catch (NullPointerException ex) {
            throw new ActivoNoEncontradoException("El activo " + parBase.toUpperCase() + "/" + parContra.toUpperCase() + " no existe");
        }
    }

    public PrecioActivo updateBinanceTicker(PrecioActivo precioActivo) {
        try {
            final String uri = "https://api.binance.com/api/v3/ticker/price?symbol=" + precioActivo.getParBase().toUpperCase() + precioActivo.getParContra().toUpperCase();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("body", headers);
            ResponseEntity<ObjectNode> respEntity2 = restTemplate.exchange(uri, HttpMethod.GET, entity, ObjectNode.class);
            JsonNode highDepth = respEntity2.getBody().get("price");
            if (!highDepth.isNull()) {
                precioActivo.setPrecio(highDepth.asDouble());
                return precioActivo;
            } else {
                return null;
            }
        } catch (NullPointerException ex) {
            throw new ActivoNoEncontradoException("El activo " + precioActivo.getParBase().toUpperCase() + "/" + precioActivo.getParContra().toUpperCase() + " no existe");
        }

    }

    public TipoDatoHistorico recibirHistoricoActivo(String parBase, String parContra, String intervalo) throws ActivoNoEncontradoException, JSONException {
        if (!ValidacionesEstaticas.esIntervaloDeBinance(intervalo)) {
            throw new ActivoNoEncontradoException("El intervalo introducido " + intervalo + " no es correcto!");
        }
        final String uri = "https://api.binance.com/api/v1/klines?symbol=" + parBase.toUpperCase() + parContra.toUpperCase() + "&interval=" + intervalo;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<Object> respEntity2 = restTemplate.exchange(uri, HttpMethod.GET, entity, Object.class);
        ArrayList<TipoDatoHistorico> listaDatosHistoricos = new ArrayList<>();
        JSONArray jsonarray = new JSONArray(respEntity2.getBody().toString());
        TipoDatoHistorico tipoDatoHistorico = new TipoDatoHistorico();
        ArrayList<DatoHistorico> lista = new ArrayList<>();
        tipoDatoHistorico.setPeriodo(intervalo);
        for (int i = 0; i < jsonarray.length(); i++) {
            Object objeto = jsonarray.get(i);
            String base = objeto.toString().substring(1, objeto.toString().length() - 1);
            String baseOpenTime = base.split(",")[0];
            String baseOpen = base.split(",")[1];
            String baseHigh = base.split(",")[2];
            String baseLow = base.split(",")[3];
            String baseClose = base.split(",")[4];
            String baseVolumen = base.split(",")[5];
            DatoHistorico dato = new DatoHistorico(baseOpenTime, Double.parseDouble(baseOpen), Double.parseDouble(baseHigh), Double.parseDouble(baseLow), Double.parseDouble(baseClose), Double.parseDouble(baseVolumen));
            lista.add(dato);
        }
        tipoDatoHistorico.setDato(lista);
        tipoDatoHistorico.setNumRegistros(lista.size());
        return tipoDatoHistorico;
    }


}









