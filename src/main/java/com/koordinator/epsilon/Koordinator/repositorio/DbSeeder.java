package com.koordinator.epsilon.Koordinator.repositorio;

import com.koordinator.epsilon.Koordinator.StaticTools;
import com.koordinator.epsilon.Koordinator.entidades.IndicadorTecnico;
import com.koordinator.epsilon.Koordinator.entidades.PrecioActivo;
import com.koordinator.epsilon.Koordinator.entidades.TipoDatoHistorico;
import com.koordinator.epsilon.Koordinator.servicios.PeticionesTerceros;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    private void recogerYActualizarPreciosMongo() throws ExecutionException, InterruptedException, JSONException {

        while (true) {
            Thread.sleep(10000);
            List<PrecioActivo> arrayActivosMongo = this.repositorioActivos.findAll();
            for (int i = 0; i < arrayActivosMongo.size(); i++) {
                this.recuperarPrecioAsincrono(arrayActivosMongo.get(i));
                this.recuperarHistoricoAsincrono(arrayActivosMongo.get(i));
                this.actualizarIndicadores(arrayActivosMongo.get(i));
            }
        }

    }

    @Async
    private void actualizarIndicadores(PrecioActivo precioActivo) {
        if (precioActivo.getListadoIndicatores() != null && precioActivo.getListadoIndicatores().size() > 0) {
            for (int i = 0; i < precioActivo.getListadoIndicatores().size(); i++) {
                IndicadorTecnico indicadorTecnico = precioActivo.getListadoIndicatores().get(i);
                int resIntervalo = StaticTools.buscarIntervalo(precioActivo.getListaDatosHora(), indicadorTecnico.getPeriodoDatosHistoricos());
                if (resIntervalo != -1) {
                    indicadorTecnico.setDatosTecnicos(this.peticionesTerceros.calcularMediaMovil(precioActivo.getListaDatosHora().get(resIntervalo), precioActivo, indicadorTecnico.getIndicatorName(), indicadorTecnico.getIntervalo(), indicadorTecnico.getPeriodoDatosHistoricos(), indicadorTecnico.getTipoSeries()));
                    precioActivo.getListadoIndicatores().set(i, indicadorTecnico);
                    this.repositorioActivos.save(precioActivo);
                }
            }
        }
    }

    @Async
    private void recuperarPrecioAsincrono(PrecioActivo precioActivo) throws ExecutionException, InterruptedException {
        CompletableFuture completableFuture = CompletableFuture.completedFuture(this.peticionesTerceros.updateBinanceTicker(precioActivo));
        completableFuture.join();
        PrecioActivo precioActivoRes = (PrecioActivo) completableFuture.get();
        this.repositorioActivos.save(precioActivoRes);
    }

    @Async
    private void recuperarHistoricoAsincrono(PrecioActivo precioActivo) throws JSONException, ExecutionException, InterruptedException {
        if (precioActivo.getListaDatosHora() != null && precioActivo.getListaDatosHora().size() > 0) {
            for (int i = 0; i < precioActivo.getListaDatosHora().size(); i++) {
                TipoDatoHistorico tipo = precioActivo.getListaDatosHora().get(i);
                CompletableFuture completableFuture = CompletableFuture.completedFuture(this.peticionesTerceros.recibirHistoricoActivo(precioActivo.getParBase(), precioActivo.getParContra(), tipo.getPeriodo()));
                completableFuture.join();
                TipoDatoHistorico tipoRes = (TipoDatoHistorico) completableFuture.get();
                precioActivo.modificarItemTipoHistoricoLista(i, tipoRes);
            }
            this.repositorioActivos.save(precioActivo);
        }
    }

}
