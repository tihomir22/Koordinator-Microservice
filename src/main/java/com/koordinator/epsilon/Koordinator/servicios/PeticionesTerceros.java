package com.koordinator.epsilon.Koordinator.servicios;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.koordinator.epsilon.Koordinator.Excepciones.ActivoNoEncontradoException;
import com.koordinator.epsilon.Koordinator.Excepciones.ExceptionResponse;
import com.koordinator.epsilon.Koordinator.StaticTools;
import com.koordinator.epsilon.Koordinator.Validaciones.MetacortexStaticLibrary.ValidacionesEstaticas;
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
import rx.Observable;

import java.io.IOException;
import java.util.*;

import static com.koordinator.epsilon.Koordinator.Validaciones.MetacortexStaticLibrary.ValidacionesEstaticas.*;

@Service
public class PeticionesTerceros {
    @Autowired
    private RepositorioActivos repositorioActivos;


    public TechnicalRegistry[][] HDataNombreIntervaloHData(HistoricDataWrapper historico, AssetPrice resActivo, String nombreIndicador, Map<String, String> queryParams) {
        ArrayList<HistoricData> listaDatosHistoricos = historico.getRawHistoricData();
        ArrayList<TechnicalIndicatorWrapper> lista = resActivo.getIndicatorList();
        int resBusquedaIndicador = StaticTools.buscarIndicadorSimple(lista, nombreIndicador.toUpperCase(), queryParams.get(intervaloHistorico));
        TechnicalRegistry[][] res;
        TALibDemo.inicializar(listaDatosHistoricos);
        List<Double> listaCierre;
        List<Double> listaMaximo;
        List<Double> listaMinimo;
        List<Double> listVolumen;
        rx.Observable<HistoricData> source;

        switch (nombreIndicador.toUpperCase()) {
            case "STOCHASTIC":
                source = Observable.from(listaDatosHistoricos);
                listaCierre = source.map(dato -> dato.getClose()).toList().toBlocking().single();
                listaMaximo = source.map(dato -> dato.getHigh()).toList().toBlocking().single();
                listaMinimo = source.map(dato -> dato.getLow()).toList().toBlocking().single();
                res = TALibDemo.ejecutarOperacionSTOCH(listaCierre, listaMaximo, listaMinimo, queryParams);
                break;
            case "CHAIKIN":
                source = Observable.from(listaDatosHistoricos);
                listaCierre = source.map(dato -> dato.getClose()).toList().toBlocking().single();
                listaMaximo = source.map(dato -> dato.getHigh()).toList().toBlocking().single();
                listaMinimo = source.map(dato -> dato.getLow()).toList().toBlocking().single();
                listVolumen = source.map(dato -> dato.getVolume()).toList().toBlocking().single();
                res = TALibDemo.ejecutarOperacionAD(listaCierre, listaMaximo, listaMinimo, listVolumen, queryParams);
                break;
            case "OBV":
                source = Observable.from(listaDatosHistoricos);
                listaCierre = source.map(dato -> dato.getClose()).toList().toBlocking().single();
                listVolumen = source.map(dato -> dato.getVolume()).toList().toBlocking().single();
                res = TALibDemo.ejecutarOperacionOBV(listaCierre, listVolumen, queryParams);
                break;
            default:
                res = null;
                System.out.println("No existe ese indicador!");
        }

        TechnicalIndicatorWrapper indicadorTecnico = new TechnicalIndicatorWrapper(nombreIndicador.toUpperCase(), queryParams.get(intervaloHistorico), null, -1, res, queryParams);
        if (resBusquedaIndicador == -1) {
            lista.add(indicadorTecnico);
        } else {
            lista.set(resBusquedaIndicador, indicadorTecnico);
        }
        resActivo.setIndicatorList(lista);
        this.repositorioActivos.save(resActivo);
        return res;
    }

    public TechnicalRegistry[][] HDataNombreIntervaloHDataTipoSeriesIntervaloIndicador(HistoricDataWrapper historico, AssetPrice resActivo, String nombreIndicador, Map<String, String> queryParams) {
        ArrayList<HistoricData> listaDatosHistoricos = historico.getRawHistoricData();
        ArrayList<TechnicalIndicatorWrapper> lista = resActivo.getIndicatorList();
        int resBusquedaIndicador = StaticTools.buscarIndicadorSimpleConIntervalo(lista, nombreIndicador.toUpperCase(), queryParams.get(intervaloHistorico), Integer.parseInt(queryParams.get(intervaloPeriodoIndicador)));
        TechnicalRegistry[][] res;
        TALibDemo.inicializar(listaDatosHistoricos);
        List<Double> listaCierre;
        List<Double> listaMaximo;
        List<Double> listaMinimo;
        rx.Observable<HistoricData> source;

        switch (nombreIndicador.toUpperCase()) {
            case "ADX":
                source = Observable.from(listaDatosHistoricos);
                listaCierre = source.map(dato -> dato.getClose()).toList().toBlocking().single();
                listaMaximo = source.map(dato -> dato.getHigh()).toList().toBlocking().single();
                listaMinimo = source.map(dato -> dato.getLow()).toList().toBlocking().single();
                res = TALibDemo.ejecutarOperacionADX(listaCierre, listaMaximo, listaMinimo, queryParams);
                break;
            case "CCI":
                source = Observable.from(listaDatosHistoricos);
                listaCierre = source.map(dato -> dato.getClose()).toList().toBlocking().single();
                listaMaximo = source.map(dato -> dato.getHigh()).toList().toBlocking().single();
                listaMinimo = source.map(dato -> dato.getLow()).toList().toBlocking().single();
                res = TALibDemo.ejecutarOperacionCCI(listaCierre, listaMaximo, listaMinimo, queryParams);
                break;
            case "AROON":
                source = Observable.from(listaDatosHistoricos);
                listaMaximo = source.map(dato -> dato.getHigh()).toList().toBlocking().single();
                listaMinimo = source.map(dato -> dato.getLow()).toList().toBlocking().single();
                res = TALibDemo.ejecutarOperacionAARON(listaMaximo, listaMinimo, queryParams);
                break;

            default:
                res = null;
                System.out.println("No existe ese indicador!");
        }

        TechnicalIndicatorWrapper indicadorTecnico = new TechnicalIndicatorWrapper(nombreIndicador.toUpperCase(), queryParams.get(intervaloHistorico), null, Integer.parseInt(queryParams.get(intervaloPeriodoIndicador)), res, queryParams);
        if (resBusquedaIndicador == -1) {
            lista.add(indicadorTecnico);
        } else {
            lista.set(resBusquedaIndicador, indicadorTecnico);
        }
        resActivo.setIndicatorList(lista);
        this.repositorioActivos.save(resActivo);
        return res;
    }

    public TechnicalRegistry[][] HDataNombreIntervaloHDataTipoSeries(HistoricDataWrapper historico, AssetPrice resActivo, String nombreIndicador, Map<String, String> queryParams) {
        ArrayList<HistoricData> listaDatosHistoricos = historico.getRawHistoricData();
        ArrayList<TechnicalIndicatorWrapper> lista = resActivo.getIndicatorList();
        int resBusquedaIndicador = StaticTools.buscarIndicadorSimpleConSeries(lista, nombreIndicador.toUpperCase(), queryParams.get(intervaloHistorico), queryParams.get(tipoSeriesIndicador));
        TechnicalRegistry[][] res;
        TALibDemo.inicializar(listaDatosHistoricos);

        switch (nombreIndicador.toUpperCase()) {
            case "MACD":
                rx.Observable<HistoricData> source = Observable.from(listaDatosHistoricos);
                List<Double> listaCierre = source.map(dato -> dato.getClose()).toList().toBlocking().single();
                res = TALibDemo.ejecutarOperacionMACD(listaCierre, queryParams);
                break;
            default:
                res = null;
                System.out.println("No existe ese indicador!");
        }

        TechnicalIndicatorWrapper indicadorTecnico = new TechnicalIndicatorWrapper(nombreIndicador.toUpperCase(), queryParams.get(intervaloHistorico), queryParams.get(tipoSeriesIndicador), -1, res, queryParams);
        if (resBusquedaIndicador == -1) {
            lista.add(indicadorTecnico);
        } else {
            lista.set(resBusquedaIndicador, indicadorTecnico);
        }
        resActivo.setIndicatorList(lista);
        this.repositorioActivos.save(resActivo);
        return res;
    }

    public TechnicalRegistry[][] HDataNombrePeriodoIntervaloHDataTipoS(HistoricDataWrapper historico, AssetPrice resActivo, String nombreIndicador, Map<String, String> queryParameters) {


        ArrayList<HistoricData> listaDatosHistoricos = historico.getRawHistoricData();
        ArrayList<TechnicalIndicatorWrapper> lista = resActivo.getIndicatorList();
        int resBusquedaIndicador = StaticTools.buscarIndicadorPorQueryParams(lista, queryParameters);
        TechnicalRegistry[][] res;
        List<Double> observableRes = TALibDemo.inicializar(listaDatosHistoricos, Integer.parseInt(queryParameters.get(intervaloPeriodoIndicador)), queryParameters.get(tipoSeriesIndicador));
        double[] list = observableRes.stream().mapToDouble(Double::doubleValue).toArray();


        switch (nombreIndicador.toUpperCase()) {

            case "SMA":
                res = TALibDemo.ejecutarOperacionSMA(list, Integer.parseInt(queryParameters.get(intervaloPeriodoIndicador)));
                break;
            case "EMA":
                res = TALibDemo.ejecutarOperacionEMA(list, Integer.parseInt(queryParameters.get(intervaloPeriodoIndicador)));
                break;
            case "DEMA":
                res = TALibDemo.ejecutarOperacionDEMA(list, Integer.parseInt(queryParameters.get(intervaloPeriodoIndicador)));
                break;
            case "KAMA":
                res = TALibDemo.ejecutarOperacionKAMA(list, Integer.parseInt(queryParameters.get(intervaloPeriodoIndicador)));
                break;
            case "MAMA":
                res = TALibDemo.ejecutarOperacionMAMA(list, Integer.parseInt(queryParameters.get(intervaloPeriodoIndicador)));
                break;
            case "TEMA":
                res = TALibDemo.ejecutarOperacionTEMA(list, Integer.parseInt(queryParameters.get(intervaloPeriodoIndicador)));
                break;
            case "TMA":
                res = TALibDemo.ejecutarOperacionTMA(list, Integer.parseInt(queryParameters.get(intervaloPeriodoIndicador)));
                break;
            case "WMA":
                res = TALibDemo.ejecutarOperacionWMA(list, Integer.parseInt(queryParameters.get(intervaloPeriodoIndicador)));
                break;
            case "T3":
                res = TALibDemo.ejecutarOperacionT3(list, Integer.parseInt(queryParameters.get(intervaloPeriodoIndicador)));
                break;
            case "RSI":
                res = TALibDemo.ejecutarOperacionRSI(list, Integer.parseInt(queryParameters.get(intervaloPeriodoIndicador)));
                break;
            case "BBANDS":
                res = TALibDemo.ejecutarOperacionBBANDS(list, Integer.parseInt(queryParameters.get(intervaloPeriodoIndicador)), queryParameters);
                break;
            default:
                res = null;
                System.out.println("No existe ese indicador!");
        }


        TechnicalIndicatorWrapper indicadorTecnico = new TechnicalIndicatorWrapper(nombreIndicador.toUpperCase(), queryParameters.get(intervaloHistorico), queryParameters.get(tipoSeriesIndicador), Integer.parseInt(queryParameters.get(intervaloPeriodoIndicador)), res, queryParameters);
        if (resBusquedaIndicador == -1) {
            lista.add(indicadorTecnico);
        } else {
            lista.set(resBusquedaIndicador, indicadorTecnico);
        }
        resActivo.setIndicatorList(lista);

        this.repositorioActivos.save(resActivo);
        return res;
    }


    public AssetPrice getLivePriceRapidApi(String parBase, String parContra) {
        final String uri = "https://bravenewcoin-v1.p.rapidapi.com/convert?qty=1&from=" + parBase.toLowerCase() + "&to=" + parContra.toLowerCase();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-RapidAPI-Host", "bravenewcoin-v1.p.rapidapi.com");
        headers.set("X-RapidAPI-Key", "43e75f11e6msh3b0e1b3d9f97b63p1c4c8fjsn357e56bca235");
        HttpEntity<String> entity = new HttpEntity<>("body", headers);

        ResponseEntity<RapidApiPrecio> respEntity2 = restTemplate.exchange(uri, HttpMethod.GET, entity, RapidApiPrecio.class);
        if (respEntity2.getBody().isSuccess()) {
            AssetPrice returnValue = new AssetPrice(parBase + parContra, respEntity2.getBody().getTo_quantity(), new ArrayList<HistoricDataWrapper>(), parBase, parContra);
            return returnValue;
        } else {
            return null;
        }

    }

    public AssetPrice getCryptoCompareApi(String parBase, String parContra) {
        try {
            final String uri = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=" + parBase.toUpperCase() + "&tsyms=" + parContra.toUpperCase() + "&api_key=6df543455629ca3d59e3d3a38cc6b7db7a922fdfbf6005e9b8c0a126731374cc";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("body", headers);
            ResponseEntity<ObjectNode> respEntity2 = restTemplate.exchange(uri, HttpMethod.GET, entity, ObjectNode.class);
            JsonNode highDepth = respEntity2.getBody().get(parBase).get(parContra);
            if (!highDepth.isNull()) {
                return new AssetPrice(parBase + parContra, highDepth.asDouble(), new ArrayList<HistoricDataWrapper>(), parBase, parContra);
            } else {
                return null;
            }
        } catch (NullPointerException ex) {
            throw new ActivoNoEncontradoException("El activo " + parBase.toUpperCase() + "/" + parContra.toUpperCase() + " no existe");
        }
    }

    public AssetPrice getBinanceTicker(String parBase, String parContra) {
        try {
            final String uri = "https://api.binance.com/api/v3/ticker/price?symbol=" + parBase.toUpperCase() + parContra.toUpperCase();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("body", headers);
            try {
                ResponseEntity<ObjectNode> respEntity2 = restTemplate.exchange(uri, HttpMethod.GET, entity, ObjectNode.class);
                JsonNode highDepth = respEntity2.getBody().get("price");
                if (!highDepth.isNull()) {
                    return new AssetPrice(parBase + parContra, highDepth.asDouble(), new ArrayList<HistoricDataWrapper>(), parBase, parContra);
                } else {
                    return null;
                }
            } catch (Exception ex) {
                System.out.println("Se ha intentado obtener un activo que no existe en BINANCE");
                return null;
            }

        } catch (NullPointerException ex) {
            throw new ActivoNoEncontradoException("El activo " + parBase.toUpperCase() + "/" + parContra.toUpperCase() + " no existe");
        }
    }

    public AssetPrice updateBinanceTicker(AssetPrice precioActivo) {
        try {
            final String uri = "https://api.binance.com/api/v3/ticker/price?symbol=" + precioActivo.getBasePair().toUpperCase() + precioActivo.getCounterPair().toUpperCase();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("body", headers);
            ResponseEntity<ObjectNode> respEntity2 = restTemplate.exchange(uri, HttpMethod.GET, entity, ObjectNode.class);
            JsonNode highDepth = respEntity2.getBody().get("price");
            if (!highDepth.isNull()) {
                precioActivo.setPrice(highDepth.asDouble());
                return precioActivo;
            } else {
                return null;
            }
        } catch (NullPointerException ex) {
            throw new ActivoNoEncontradoException("El activo " + precioActivo.getBasePair().toUpperCase() + "/" + precioActivo.getCounterPair().toUpperCase() + " no existe");
        }

    }

    public HistoricDataWrapper recibirHistoricoActivo(String parBase, String parContra, String intervalo) throws ActivoNoEncontradoException, JSONException {
        if (!ValidacionesEstaticas.esIntervaloDeBinance(intervalo)) {
            throw new ActivoNoEncontradoException("El intervalo introducido " + intervalo + " no es correcto!");
        }
        final String uri = "https://api.binance.com/api/v1/klines?symbol=" + parBase.toUpperCase() + parContra.toUpperCase() + "&interval=" + intervalo;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<Object> respEntity2 = restTemplate.exchange(uri, HttpMethod.GET, entity, Object.class);
        ArrayList<HistoricDataWrapper> listaDatosHistoricos = new ArrayList<>();
        JSONArray jsonarray = new JSONArray(respEntity2.getBody().toString());
        HistoricDataWrapper tipoDatoHistorico = new HistoricDataWrapper();
        ArrayList<HistoricData> lista = new ArrayList<>();
        tipoDatoHistorico.setPeriod(intervalo);
        for (int i = 0; i < jsonarray.length(); i++) {
            Object objeto = jsonarray.get(i);
            String base = objeto.toString().substring(1, objeto.toString().length() - 1);
            String baseOpenTime = base.split(",")[0];
            String baseOpen = base.split(",")[1];
            String baseHigh = base.split(",")[2];
            String baseLow = base.split(",")[3];
            String baseClose = base.split(",")[4];
            String baseVolumen = base.split(",")[5];
            HistoricData dato = new HistoricData(baseOpenTime, Double.parseDouble(baseOpen), Double.parseDouble(baseHigh), Double.parseDouble(baseLow), Double.parseDouble(baseClose), Double.parseDouble(baseVolumen));
            lista.add(dato);
        }
        tipoDatoHistorico.setRawHistoricData(lista);
        tipoDatoHistorico.setNumRecords(lista.size());
        return tipoDatoHistorico;
    }


}









