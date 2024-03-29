package com.koordinator.epsilon.Koordinator.controladores;

import com.koordinator.epsilon.Koordinator.Excepciones.ActivoNoEncontradoException;
import com.koordinator.epsilon.Koordinator.Respuestas.RespuestaIndicadorTecnico;
import com.koordinator.epsilon.Koordinator.Respuestas.RespuestaPersonalizada;
import com.koordinator.epsilon.Koordinator.Respuestas.RespuestaPersonalizadaHistorico;
import com.koordinator.epsilon.Koordinator.StaticTools;
import com.koordinator.epsilon.Koordinator.Validaciones.MetacortexStaticLibrary.ValidacionesEstaticas;
import com.koordinator.epsilon.Koordinator.entidades.*;
import com.koordinator.epsilon.Koordinator.repositorio.RepositorioActivos;
import com.koordinator.epsilon.Koordinator.servicios.OperadorTernarioJS;
import com.koordinator.epsilon.Koordinator.servicios.PeticionesTerceros;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class ControladorPrecios {
    @Autowired
    private RepositorioActivos repositorioActivos;

    @Autowired
    private PeticionesTerceros peticionesTerceros;


    @GetMapping("/")
    public List<AssetPrice> getAll() {
        List<AssetPrice> lista = this.repositorioActivos.findAll();
        return lista;
    }


    @GetMapping(value = "/prices/{parbase}/{parcontra}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaPersonalizada> recuperarPrecioActivo(@PathVariable("parbase") String id, @PathVariable("parcontra") String id2) throws ActivoNoEncontradoException {
        Optional<AssetPrice> res = this.repositorioActivos.findById(id + id2);
        if (res.isPresent()) {
            return Optional.of(new RespuestaPersonalizada(200, "Price result " + id + id2, res.get()));
        } else {
            Optional<AssetPrice> resOpt = Optional.ofNullable(this.peticionesTerceros.getBinanceTicker(id.toUpperCase(), id2.toUpperCase()));
            if (resOpt.isPresent()) {
                this.repositorioActivos.save(resOpt.get());
                return Optional.of(new RespuestaPersonalizada(200, "Price result " + id + id2, resOpt.get()));
            } else {
                return Optional.of(new RespuestaPersonalizada(404, "The asset " + id + id2 + " does not exist on BINANCE", null));
            }
        }
    }

    @GetMapping(value = "historic/{parbase}/{parcontra}/{interval}/{startTime}/{endTime}/{limit}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaPersonalizadaHistorico> recuperarHistoricoActivo(@PathVariable("parbase") String id, @PathVariable("parcontra") String id2, @PathVariable("interval") String intervalo, @PathVariable("startTime") String startTime, @PathVariable("endTime") String endTime, @PathVariable("limit") int limit) throws IOException, JSONException {
        id = id.toUpperCase();
        id2 = id2.toUpperCase();
        Optional<AssetPrice> res = this.repositorioActivos.findById(id + id2);
        if (!res.isPresent()) {
            res = Optional.ofNullable(this.recuperarPrecioActivo(id, id2).get().getData());
        }
        if (res.isPresent()) {

            ArrayList<HistoricDataWrapper> lista = res.get().getHistoricData();
            if (lista == null) {
                lista = new ArrayList<HistoricDataWrapper>();
            }
            int resBusqueda = StaticTools.busquedaHistoricoCompleta(lista, intervalo,startTime,endTime,limit);
            Optional<HistoricDataWrapper> resOpt;
            if (resBusqueda == -1) {
                resOpt = Optional.ofNullable(this.peticionesTerceros.recibirHistoricoActivo(id, id2, intervalo, startTime,endTime,limit));
                if (resOpt.isPresent()) {
                    lista.add(resOpt.get());
                } else {
                    return Optional.of(new RespuestaPersonalizadaHistorico(404, "The interval " + intervalo + " does not exist!", null));
                }
                res.get().setHistoricData(lista);
            } else {
                resOpt = Optional.ofNullable(lista.get(resBusqueda));
            }
            this.repositorioActivos.save(res.get());
            return Optional.of(new RespuestaPersonalizadaHistorico(200, "Historic result " + id + id2, resOpt.get()));

        } else {
            return Optional.of(new RespuestaPersonalizadaHistorico(404, "The cryptocurrency " + id + id2 + " does not exist", null));
        }

    }


    @GetMapping(value = "technical/sma/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarSMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result SMA " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombrePeriodoIntervaloHDataTipoS(tipoDatoHistorico, resActivo.get(), "sma", queryParameters)).orElse(null)));

        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }

    @GetMapping(value = "technical/ema/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarEMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result EMA " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombrePeriodoIntervaloHDataTipoS(tipoDatoHistorico, resActivo.get(), "ema", queryParameters)).orElse(null)));

        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }


    @GetMapping(value = "technical/dema/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarDEMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result DEMA " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombrePeriodoIntervaloHDataTipoS(tipoDatoHistorico, resActivo.get(), "dema", queryParameters)).orElse(null)));

        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }


    @GetMapping(value = "technical/kama/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarKAMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result KAMA " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombrePeriodoIntervaloHDataTipoS(tipoDatoHistorico, resActivo.get(), "kama", queryParameters)).orElse(null)));

        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }


    @GetMapping(value = "technical/mama/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarMAMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result MAMA " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombrePeriodoIntervaloHDataTipoS(tipoDatoHistorico, resActivo.get(), "mama", queryParameters)).orElse(null)));

        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }

    @GetMapping(value = "technical/tema/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarTEMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result TEMA " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombrePeriodoIntervaloHDataTipoS(tipoDatoHistorico, resActivo.get(), "tema", queryParameters)).orElse(null)));

        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }

    @GetMapping(value = "technical/tma/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarTMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result TMA " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombrePeriodoIntervaloHDataTipoS(tipoDatoHistorico, resActivo.get(), "tma", queryParameters)).orElse(null)));

        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }

    @GetMapping(value = "technical/wma/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarWMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result WMA " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombrePeriodoIntervaloHDataTipoS(tipoDatoHistorico, resActivo.get(), "wma", queryParameters)).orElse(null)));

        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }

    @GetMapping(value = "technical/t3/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarT3(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result T3 " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombrePeriodoIntervaloHDataTipoS(tipoDatoHistorico, resActivo.get(), "t3", queryParameters)).orElse(null)));

        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }


    @GetMapping(value = "technical/rsi/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarRSI(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result RSI " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombrePeriodoIntervaloHDataTipoS(tipoDatoHistorico, resActivo.get(), "rsi", queryParameters)).orElse(null)));

        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }

    @GetMapping(value = "technical/stochastic/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarSTOCH(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSimboloIntervaloHistorico(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result STOCHASTIC " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombreIntervaloHData(tipoDatoHistorico, resActivo.get(), "stochastic", queryParameters)).orElse(null)));

        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }

    @GetMapping(value = "technical/macd/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarMACD(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSimboloIntervaloTipoSeries(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result MACD " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombreIntervaloHDataTipoSeriesSinIntervaloIndicador(tipoDatoHistorico, resActivo.get(), "macd", queryParameters)).orElse(null)));


        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }

    @GetMapping(value = "technical/adx/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarADX(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSimboloIntervaloHistorico(queryParameters) && ValidacionesEstaticas.validacionSimboloIntervaloIndicador(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result ADX " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombreIntervaloHDataTipoSeriesIntervaloIndicador(tipoDatoHistorico, resActivo.get(), "adx", queryParameters)).orElse(null)));
        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }

    @GetMapping(value = "technical/cci/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarCCI(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSimboloIntervaloHistorico(queryParameters) && ValidacionesEstaticas.validacionSimboloIntervaloIndicador(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result CCI " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombreIntervaloHDataTipoSeriesIntervaloIndicador(tipoDatoHistorico, resActivo.get(), "cci", queryParameters)).orElse(null)));
        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }

    @GetMapping(value = "technical/aroon/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarAARON(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSimboloIntervaloHistorico(queryParameters) && ValidacionesEstaticas.validacionSimboloIntervaloIndicador(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result AARON " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombreIntervaloHDataTipoSeriesIntervaloIndicador(tipoDatoHistorico, resActivo.get(), "aroon", queryParameters)).orElse(null)));
        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }

    @GetMapping(value = "technical/bbands/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarBBANDS(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result BBANDS " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombrePeriodoIntervaloHDataTipoS(tipoDatoHistorico, resActivo.get(), "bbands", queryParameters)).orElse(null)));

        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }

    @GetMapping(value = "technical/ad/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarChaikin(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSimboloIntervaloHistorico(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result A/D Chaikin" + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombreIntervaloHData(tipoDatoHistorico, resActivo.get(), "chaikin", queryParameters)).orElse(null)));

        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }

    @GetMapping(value = "technical/obv/**", produces = MediaType.APPLICATION_JSON_VALUE)
    public Optional<RespuestaIndicadorTecnico> recuperarOBV(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSimboloIntervaloHistorico(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), OperadorTernarioJS.obtenerStartTime(queryParameters),OperadorTernarioJS.obtenerEndTime(queryParameters),OperadorTernarioJS.obtenerLimit(queryParameters)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result on balance volume (OBV)" + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.HDataNombreIntervaloHData(tipoDatoHistorico, resActivo.get(), "obv", queryParameters)).orElse(null)));

        } else {
            return Optional.of(
                    new RespuestaIndicadorTecnico(400, "Incorrect parameters were given", null));

        }
    }

}
