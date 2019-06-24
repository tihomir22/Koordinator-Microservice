package com.koordinator.epsilon.Koordinator.controladores;

import com.koordinator.epsilon.Koordinator.Excepciones.ActivoNoEncontradoException;
import com.koordinator.epsilon.Koordinator.StaticTools;
import com.koordinator.epsilon.Koordinator.entidades.DatoHistorico;
import com.koordinator.epsilon.Koordinator.entidades.PrecioActivo;
import com.koordinator.epsilon.Koordinator.entidades.TipoDatoHistorico;
import com.koordinator.epsilon.Koordinator.repositorio.RepositorioActivos;
import com.koordinator.epsilon.Koordinator.servicios.PeticionesTerceros;
import jdk.nashorn.internal.runtime.options.Option;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class ControladorPrecios {
    @Autowired
    private RepositorioActivos repositorioActivos;

    @Autowired
    private PeticionesTerceros peticionesTerceros;

    @GetMapping("/")
    public List<PrecioActivo> getAll(){
        List<PrecioActivo>lista=this.repositorioActivos.findAll();
        return lista;
    }

    @GetMapping("/prices/{parbase}/{parcontra}")
    public Optional<PrecioActivo> recuperarPrecioActivo(@PathVariable("parbase")String id,@PathVariable("parcontra")String id2) {
        Optional<PrecioActivo> res=this.repositorioActivos.findById(id+id2);
        if(res.isPresent()){
            return res;
        }else{
           Optional<PrecioActivo> resOpt= Optional.ofNullable(this.peticionesTerceros.getBinanceTicker(id.toUpperCase(), id2.toUpperCase()));
            if(resOpt.isPresent()){
                this.repositorioActivos.save(resOpt.get());
                return resOpt;
            }else{
                throw new ActivoNoEncontradoException("El par "+id+id2+" no existe");
            }
        }
    }

    @GetMapping("historic/{parbase}/{parcontra}/{interval}")
    public Optional<TipoDatoHistorico> recuperarHistoricoActivo(@PathVariable("parbase")String id, @PathVariable("parcontra")String id2, @PathVariable("interval")String intervalo) throws IOException, JSONException {
        id=id.toUpperCase();
        id2=id2.toUpperCase();
        Optional<PrecioActivo> res = this.repositorioActivos.findById(id + id2);
        Optional<TipoDatoHistorico> resOpt = Optional.ofNullable(this.peticionesTerceros.recibirHistoricoActivo(id, id2, intervalo));
        if (resOpt.isPresent() && res.isPresent()) {
            ArrayList<TipoDatoHistorico> lista = res.get().getListaDatosHora();
            if(lista==null){
                lista=new ArrayList<TipoDatoHistorico>();
            }
            int resBusqueda=StaticTools.buscarIntervalo(lista,intervalo);
            if(resBusqueda!=-1){
                lista.set(resBusqueda,resOpt.get());
            }else{lista.add(resOpt.get());}
            res.get().setListaDatosHora(lista);
            this.repositorioActivos.save(res.get());
            return resOpt;
        } else {
            throw new ActivoNoEncontradoException("El par " + id + id2 + " no existe");
        }
    }

    @PostMapping
    public void insert(@RequestBody PrecioActivo activo){
        this.repositorioActivos.save(activo);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id){
        this.repositorioActivos.deleteById(id);
    }
}
