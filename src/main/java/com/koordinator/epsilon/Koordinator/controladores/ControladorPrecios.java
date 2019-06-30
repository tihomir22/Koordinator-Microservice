package com.koordinator.epsilon.Koordinator.controladores;

import com.koordinator.epsilon.Koordinator.Excepciones.ActivoNoEncontradoException;
import com.koordinator.epsilon.Koordinator.StaticTools;
import com.koordinator.epsilon.Koordinator.entidades.*;
import com.koordinator.epsilon.Koordinator.repositorio.RepositorioActivos;
import com.koordinator.epsilon.Koordinator.servicios.PeticionesTerceros;
import com.koordinator.epsilon.Koordinator.servicios.TALibDemo;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
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

    @GetMapping("technical/{nombreIndicador}/{parbase}/{parcontra}/{interval}/{periodoTiempo}/{tipoSeries}")
    public ArrayList<RegistroTecnico> recuperarSMA(@PathVariable("parbase") String id,
                                                   @PathVariable("parcontra") String id2,
                                                   @PathVariable("nombreIndicador") String nombreIndicador,
                                                   @PathVariable("interval") String intervaloDatosHistoricos,
                                                   @PathVariable("periodoTiempo") int periodoTiempo,
                                                   @PathVariable("tipoSeries") String tipoSeries) throws IOException, JSONException {
        id = id.toUpperCase();
        id2 = id2.toUpperCase();
        nombreIndicador=nombreIndicador.toUpperCase();
        Optional<PrecioActivo> resActivo = this.repositorioActivos.findById(id + id2);
        if (resActivo.isPresent()) {
            Optional<TipoDatoHistorico> historico = this.recuperarHistoricoActivo(id, id2, intervaloDatosHistoricos);
            if(historico.isPresent()){
              return this.peticionesTerceros.calcularSMA(historico.get(),resActivo.get(),nombreIndicador,periodoTiempo,intervaloDatosHistoricos,tipoSeries);
            }else{
                return null;
            }
        } else {
            throw new ActivoNoEncontradoException("El par " + id + id2 + " no existe");
        }

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
