package com.koordinator.epsilon.Koordinator.controladores;

import com.koordinator.epsilon.Koordinator.Excepciones.ActivoNoEncontradoException;
import com.koordinator.epsilon.Koordinator.entidades.PrecioActivo;
import com.koordinator.epsilon.Koordinator.repositorio.RepositorioActivos;
import com.koordinator.epsilon.Koordinator.servicios.PeticionesTerceros;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/precios")
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

    @GetMapping("/{parbase}/{parcontra}")
    public Optional<PrecioActivo> recuperarPrecioActivo(@PathVariable("parbase")String id,@PathVariable("parcontra")String id2) throws ChangeSetPersister.NotFoundException {
        Optional<PrecioActivo> res=this.repositorioActivos.findById(id+id2);
        if(res.isPresent()){
            return res;
        }else{
           Optional<PrecioActivo> resOpt= Optional.ofNullable(this.peticionesTerceros.getLivePriceRapidApi(id.toUpperCase(), id2.toUpperCase()));
            if(resOpt.isPresent()){
                this.repositorioActivos.save(resOpt.get());
                return resOpt;
            }else{
                throw new ActivoNoEncontradoException("El par "+id+id2+" no existe");
            }
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
