package com.koordinator.epsilon.Koordinator.controladores;

import com.koordinator.epsilon.Koordinator.Excepciones.ActivoNoEncontradoException;
import com.koordinator.epsilon.Koordinator.Respuestas.RespuestaIndicadorTecnico;
import com.koordinator.epsilon.Koordinator.Respuestas.RespuestaPersonalizada;
import com.koordinator.epsilon.Koordinator.Respuestas.RespuestaPersonalizadaHistorico;
import com.koordinator.epsilon.Koordinator.StaticTools;
import com.koordinator.epsilon.Koordinator.Validaciones.ValidacionesEstaticas;
import com.koordinator.epsilon.Koordinator.entidades.*;
import com.koordinator.epsilon.Koordinator.repositorio.RepositorioActivos;
import com.koordinator.epsilon.Koordinator.servicios.PeticionesTerceros;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

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


    @GetMapping("/prices/{parbase}/{parcontra}")
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

    @GetMapping("historic/{parbase}/{parcontra}/{interval}")
    public Optional<RespuestaPersonalizadaHistorico> recuperarHistoricoActivo(@PathVariable("parbase") String id, @PathVariable("parcontra") String id2, @PathVariable("interval") String intervalo) throws IOException, JSONException {
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
            int resBusqueda = StaticTools.buscarIntervalo(lista, intervalo);
            Optional<HistoricDataWrapper> resOpt;
            if (resBusqueda == -1) {
                resOpt = Optional.ofNullable(this.peticionesTerceros.recibirHistoricoActivo(id, id2, intervalo));
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


    @GetMapping("technical/sma/**")
    public Optional<RespuestaIndicadorTecnico> recuperarSMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result SMA " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "sma", Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null)));

        }
        return Optional.empty();
    }

    @GetMapping("technical/ema/**")
    public Optional<RespuestaIndicadorTecnico> recuperarEMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result EMA " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "ema", Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null)));

        }
        return Optional.empty();
    }


    @GetMapping("technical/dema/**")
    public Optional<RespuestaIndicadorTecnico> recuperarDEMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result DEMA " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "dema", Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null)));

        }
        return Optional.empty();
    }





    @GetMapping("technical/kama/**")
    public Optional<RespuestaIndicadorTecnico> recuperarKAMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result KAMA " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "kama", Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null)));

        }
        return Optional.empty();
    }


    @GetMapping("technical/mama/**")
    public Optional<RespuestaIndicadorTecnico> recuperarMAMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result MAMA " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "mama", Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null)));

        }
        return Optional.empty();
    }

    @GetMapping("technical/tema/**")
    public Optional<RespuestaIndicadorTecnico> recuperarTEMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result TEMA " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "tema", Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null)));

        }
        return Optional.empty();
    }

    @GetMapping("technical/tma/**")
    public Optional<RespuestaIndicadorTecnico> recuperarTMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result TMA " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "tma", Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null)));

        }
        return Optional.empty();
    }

    @GetMapping("technical/wma/**")
    public Optional<RespuestaIndicadorTecnico> recuperarWMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result WMA " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "wma", Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null)));

        }
        return Optional.empty();
    }

    @GetMapping("technical/t3/**")
    public Optional<RespuestaIndicadorTecnico> recuperarT3(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result T3 " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "t3", Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null)));

        }
        return Optional.empty();
    }

    @GetMapping("technical/rsi/**")
    public Optional<RespuestaIndicadorTecnico> recuperarRSI(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<HistoricDataWrapper> historico = Optional.ofNullable(this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico)).get().getData());
            Optional<AssetPrice> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            return Optional.of(
                    new RespuestaIndicadorTecnico(200, "Technical result RSI " + historico.get().getPeriod(),
                            historico.map(tipoDatoHistorico ->
                                    this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "rsi", Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null)));

        }
        return Optional.empty();
    }



}
