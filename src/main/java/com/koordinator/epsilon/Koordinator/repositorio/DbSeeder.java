package com.koordinator.epsilon.Koordinator.repositorio;

import com.koordinator.epsilon.Koordinator.entidades.PrecioActivo;
import com.koordinator.epsilon.Koordinator.servicios.PeticionesTerceros;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//se ejecutará al prinicipio con spring
@Component
public class DbSeeder implements CommandLineRunner {

    private RepositorioActivos repositorioActivos;

    @Autowired
    private PeticionesTerceros peticionesTerceros;

    public DbSeeder(RepositorioActivos repositorioActivos) {
        this.repositorioActivos = repositorioActivos;
    }

    @Override
    public void run(String... args) throws Exception {
        this.recogerYActualizarPreciosMongo();
    }
    @Async
    private void recogerYActualizarPreciosMongo() throws ExecutionException, InterruptedException {

        while(true) {
            Thread.sleep(10000);
            List<PrecioActivo> arrayActivosMongo = this.repositorioActivos.findAll();
            for (int i = 0; i < arrayActivosMongo.size(); i++) {
                this.recuperarPrecioAsincrono(arrayActivosMongo.get(i));
            }
        }

    }

    @Async
    private void recuperarPrecioAsincrono(PrecioActivo precioActivo) throws ExecutionException, InterruptedException {
        CompletableFuture completableFuture= CompletableFuture.completedFuture(this.peticionesTerceros.updateBinanceTicker(precioActivo));
        completableFuture.join();
        PrecioActivo precioActivoRes= (PrecioActivo) completableFuture.get();
        this.repositorioActivos.save(precioActivoRes);
    }

    /*@Async
    private void recuperarHistoricoAsincrono(PrecioActivo precioActivo) throws ExecutionException, InterruptedException {
        CompletableFuture completableFuture= CompletableFuture.completedFuture(this.peticionesTerceros(precioActivo.getParBase(),precioActivo.getParContra()));
        completableFuture.join();
        PrecioActivo precioActivoRes= (PrecioActivo) completableFuture.get();
        this.repositorioActivos.save(precioActivoRes);
    }*/
}
