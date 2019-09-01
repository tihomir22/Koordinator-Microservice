package com.koordinator.epsilon.Koordinator.repositorio;

import com.koordinator.epsilon.Koordinator.StaticTools;
import com.koordinator.epsilon.Koordinator.entidades.AssetPrice;
import com.koordinator.epsilon.Koordinator.entidades.HistoricDataWrapper;
import com.koordinator.epsilon.Koordinator.entidades.TechnicalIndicatorWrapper;
import com.koordinator.epsilon.Koordinator.servicios.PeticionesTerceros;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

//se ejecutará al prinicipio con spring
@Component
public class DbSeeder implements CommandLineRunner {
    @Autowired
    private RepositorioActivos repositorioActivos;

    @Autowired
    private PeticionesTerceros peticionesTerceros;

    public DbSeeder(RepositorioActivos repositorioActivos) {
        this.repositorioActivos = repositorioActivos;
    }

    @Override
    public void run(String... args) throws Exception {
        //this.recogerYActualizarPreciosMongo();

    }

    @Async
    private void recogerYActualizarPreciosMongo() throws ExecutionException, InterruptedException, JSONException {

        while (true) {
            Thread.sleep(10000);
            List<AssetPrice> arrayActivosMongo = this.repositorioActivos.findAll();
            for (int i = 0; i < arrayActivosMongo.size(); i++) {
                this.recuperarPrecioAsincrono(arrayActivosMongo.get(i));
                this.recuperarHistoricoAsincrono(arrayActivosMongo.get(i));
                this.actualizarIndicadores(arrayActivosMongo.get(i));
            }
        }

    }

    @Async
    private void actualizarIndicadores(AssetPrice precioActivo) {
        if (precioActivo.getIndicatorList() != null && precioActivo.getIndicatorList().size() > 0) {
            for (int i = 0; i < precioActivo.getIndicatorList().size(); i++) {
                TechnicalIndicatorWrapper indicadorTecnico = precioActivo.getIndicatorList().get(i);
                int resIntervalo = StaticTools.buscarIntervalo(precioActivo.getHistoricData(), indicadorTecnico.getHistoricPeriod());
                if (resIntervalo != -1) {
                    if (indicadorTecnico.getInterval() == -1 && indicadorTecnico.getSeriesType() == null) {
                        indicadorTecnico.setRawTechnicalData(this.peticionesTerceros.HDataNombreIntervaloHData(precioActivo.getHistoricData().get(resIntervalo), precioActivo, indicadorTecnico.getIndicatorName(), indicadorTecnico.getQueryParameters()));
                    } else if (indicadorTecnico.getInterval() != -1 && indicadorTecnico.getSeriesType() == null) {
                        indicadorTecnico.setRawTechnicalData(this.peticionesTerceros.HDataNombreIntervaloHDataTipoSeriesIntervaloIndicador(precioActivo.getHistoricData().get(resIntervalo), precioActivo, indicadorTecnico.getIndicatorName(), indicadorTecnico.getQueryParameters()));
                    } else if (indicadorTecnico.getInterval() == -1 && indicadorTecnico.getSeriesType() != null) {
                        indicadorTecnico.setRawTechnicalData(this.peticionesTerceros.HDataNombreIntervaloHDataTipoSeriesSinIntervaloIndicador(precioActivo.getHistoricData().get(resIntervalo), precioActivo, indicadorTecnico.getIndicatorName(), indicadorTecnico.getQueryParameters()));
                    } else {
                        indicadorTecnico.setRawTechnicalData(this.peticionesTerceros.HDataNombrePeriodoIntervaloHDataTipoS(precioActivo.getHistoricData().get(resIntervalo), precioActivo, indicadorTecnico.getIndicatorName(), indicadorTecnico.getQueryParameters()));
                    }
                    precioActivo.getIndicatorList().set(i, indicadorTecnico);
                    this.repositorioActivos.save(precioActivo);


                }
            }
        }
    }

    @Async
    private void recuperarPrecioAsincrono(AssetPrice precioActivo) throws ExecutionException, InterruptedException {
        CompletableFuture completableFuture = CompletableFuture.completedFuture(this.peticionesTerceros.updateBinanceTicker(precioActivo));
        completableFuture.join();
        AssetPrice precioActivoRes = (AssetPrice) completableFuture.get();
        this.repositorioActivos.save(precioActivoRes);
    }

    @Async
    private void recuperarHistoricoAsincrono(AssetPrice precioActivo) throws JSONException, ExecutionException, InterruptedException {
        if (precioActivo.getHistoricData() != null && precioActivo.getHistoricData().size() > 0) {
            for (int i = 0; i < precioActivo.getHistoricData().size(); i++) {
                HistoricDataWrapper tipo = precioActivo.getHistoricData().get(i);
                CompletableFuture completableFuture = CompletableFuture.completedFuture(this.peticionesTerceros.recibirHistoricoActivo(precioActivo.getBasePair(), precioActivo.getCounterPair(), tipo.getPeriod(), tipo.getStartTime(),tipo.getEndTime(),tipo.getLimit()));
                completableFuture.join();
                HistoricDataWrapper tipoRes = (HistoricDataWrapper) completableFuture.get();
                precioActivo.modificarItemTipoHistoricoLista(i, tipoRes);
            }
            this.repositorioActivos.save(precioActivo);
        }
    }

    private Map<String, String> returnMapFromString(String toMap) {
        System.out.println(toMap);
        return null;
    }

}
