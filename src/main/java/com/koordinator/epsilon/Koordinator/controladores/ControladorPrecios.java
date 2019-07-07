package com.koordinator.epsilon.Koordinator.controladores;

import com.koordinator.epsilon.Koordinator.Excepciones.ActivoNoEncontradoException;
import com.koordinator.epsilon.Koordinator.StaticTools;
import com.koordinator.epsilon.Koordinator.Validaciones.ValidacionesEstaticas;
import com.koordinator.epsilon.Koordinator.entidades.*;
import com.koordinator.epsilon.Koordinator.repositorio.RepositorioActivos;
import com.koordinator.epsilon.Koordinator.servicios.PeticionesTerceros;
import com.koordinator.epsilon.Koordinator.servicios.TALibDemo;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
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
    public List<PrecioActivo> getAll() {
        List<PrecioActivo> lista = this.repositorioActivos.findAll();
        return lista;
    }

    @GetMapping("/prices/{parbase}/{parcontra}")
    public Optional<PrecioActivo> recuperarPrecioActivo(@PathVariable("parbase") String id, @PathVariable("parcontra") String id2) {
        Optional<PrecioActivo> res = this.repositorioActivos.findById(id + id2);
        if (res.isPresent()) {
            return res;
        } else {
            Optional<PrecioActivo> resOpt = Optional.ofNullable(this.peticionesTerceros.getBinanceTicker(id.toUpperCase(), id2.toUpperCase()));
            if (resOpt.isPresent()) {
                this.repositorioActivos.save(resOpt.get());
                return resOpt;
            } else {
                throw new ActivoNoEncontradoException("El par " + id + id2 + " no existe");
            }
        }
    }

    @GetMapping("historic/{parbase}/{parcontra}/{interval}")
    public Optional<TipoDatoHistorico> recuperarHistoricoActivo(@PathVariable("parbase") String id, @PathVariable("parcontra") String id2, @PathVariable("interval") String intervalo) throws IOException, JSONException {
        id = id.toUpperCase();
        id2 = id2.toUpperCase();
        Optional<PrecioActivo> res = this.repositorioActivos.findById(id + id2);
        if (res.isPresent()) {
            ArrayList<TipoDatoHistorico> lista = res.get().getListaDatosHora();
            if (lista == null) {
                lista = new ArrayList<TipoDatoHistorico>();
            }
            int resBusqueda = StaticTools.buscarIntervalo(lista, intervalo);
            Optional<TipoDatoHistorico> resOpt;
            if (resBusqueda == -1) {
                resOpt = Optional.ofNullable(this.peticionesTerceros.recibirHistoricoActivo(id, id2, intervalo));
                if (resOpt.isPresent()) {
                    lista.add(resOpt.get());
                } else {
                    throw new ActivoNoEncontradoException("El intervalo " + intervalo + " no existe!");
                }
                res.get().setListaDatosHora(lista);
            } else {
                resOpt = Optional.ofNullable(lista.get(resBusqueda));
            }
            this.repositorioActivos.save(res.get());
            return resOpt;
        } else {
            throw new ActivoNoEncontradoException("El par " + id + id2 + " no existe");
        }
    }


    @GetMapping("technical/sma/**")
    public ArrayList<RegistroTecnico> recuperarSMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if(ValidacionesEstaticas.validacionSMA(queryParameters)){
            Optional<PrecioActivo> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            if (resActivo.isPresent()) {
                Optional<TipoDatoHistorico> historico = this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico));
                return historico.map(tipoDatoHistorico -> this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "sma",Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null);
            } else {
                throw new ActivoNoEncontradoException("El par " + queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase() + " no existe");
            }
        }
        return null;
    }

    @GetMapping("technical/ema/**")
    public ArrayList<RegistroTecnico> recuperarEMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<PrecioActivo> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            if (resActivo.isPresent()) {
                Optional<TipoDatoHistorico> historico = this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico));
                return historico.map(tipoDatoHistorico -> this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "ema",Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null);
            }
        }
        return null;
    }

    @GetMapping("technical/dema/**")
    public ArrayList<RegistroTecnico> recuperarDEMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<PrecioActivo> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            if (resActivo.isPresent()) {
                Optional<TipoDatoHistorico> historico = this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico));
                return historico.map(tipoDatoHistorico -> this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "dema",Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null);
            }
        }
        return null;
    }

    @GetMapping("technical/kama/**")
    public ArrayList<RegistroTecnico> recuperarKAMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<PrecioActivo> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            if (resActivo.isPresent()) {
                Optional<TipoDatoHistorico> historico = this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico));
                return historico.map(tipoDatoHistorico -> this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "kama",Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null);
            }
        }
        return null;
    }

    @GetMapping("technical/mama/**")
    public ArrayList<RegistroTecnico> recuperarMAMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<PrecioActivo> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            if (resActivo.isPresent()) {
                Optional<TipoDatoHistorico> historico = this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico));
                return historico.map(tipoDatoHistorico -> this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "mama",Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null);
            }
        }
        return null;
    }

    @GetMapping("technical/tema/**")
    public ArrayList<RegistroTecnico> recuperarTEMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<PrecioActivo> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            if (resActivo.isPresent()) {
                Optional<TipoDatoHistorico> historico = this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico));
                return historico.map(tipoDatoHistorico -> this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "tema",Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null);
            }
        }
        return null;
    }

    @GetMapping("technical/tma/**")
    public ArrayList<RegistroTecnico> recuperarTMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<PrecioActivo> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            if (resActivo.isPresent()) {
                Optional<TipoDatoHistorico> historico = this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico));
                return historico.map(tipoDatoHistorico -> this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "tma",Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null);
            }
        }
        return null;
    }

    @GetMapping("technical/wma/**")
    public ArrayList<RegistroTecnico> recuperarWMA(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<PrecioActivo> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            if (resActivo.isPresent()) {
                Optional<TipoDatoHistorico> historico = this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico));
                return historico.map(tipoDatoHistorico -> this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "wma",Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null);
            }
        }
        return null;
    }

    @GetMapping("technical/t3/**")
    public ArrayList<RegistroTecnico> recuperarT3(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<PrecioActivo> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            if (resActivo.isPresent()) {
                Optional<TipoDatoHistorico> historico = this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico));
                return historico.map(tipoDatoHistorico -> this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "t3",Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null);
            }
        }
        return null;
    }

    @GetMapping("technical/rsi/**")
    public ArrayList<RegistroTecnico> recuperarRSI(@RequestParam Map<String, String> queryParameters) throws IOException, JSONException {
        if (ValidacionesEstaticas.validacionSMA(queryParameters)) {
            Optional<PrecioActivo> resActivo = this.repositorioActivos.findById(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase() + queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase());
            if (resActivo.isPresent()) {
                Optional<TipoDatoHistorico> historico = this.recuperarHistoricoActivo(queryParameters.get(ValidacionesEstaticas.nombreParBase).toUpperCase(), queryParameters.get(ValidacionesEstaticas.nombreParContra).toUpperCase(), queryParameters.get(ValidacionesEstaticas.intervaloHistorico));
                return historico.map(tipoDatoHistorico -> this.peticionesTerceros.calcularMediaMovil(tipoDatoHistorico, resActivo.get(), "rsi",Integer.parseInt(queryParameters.get(ValidacionesEstaticas.intervaloPeriodoIndicador)), queryParameters.get(ValidacionesEstaticas.intervaloHistorico), queryParameters.get(ValidacionesEstaticas.tipoSeriesIndicador))).orElse(null);
            }
        }
        return null;
    }




    @PostMapping
    public void insert(@RequestBody PrecioActivo activo) {
        this.repositorioActivos.save(activo);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        this.repositorioActivos.deleteById(id);
    }
}
