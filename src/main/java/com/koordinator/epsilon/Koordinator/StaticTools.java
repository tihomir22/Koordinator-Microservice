package com.koordinator.epsilon.Koordinator;

import com.koordinator.epsilon.Koordinator.entidades.HistoricDataWrapper;
import com.koordinator.epsilon.Koordinator.entidades.TechnicalIndicatorWrapper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

public class StaticTools {
    public static int buscarIntervalo(ArrayList<HistoricDataWrapper> lista, String intervalo) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getPeriod().equals(intervalo)) {
                return i;
            }
        }
        return -1;
    }

    public static int busquedaHistoricoCompleta(ArrayList<HistoricDataWrapper>lista,String intervalo,String startTime,String endTime,int limit){
        for (int i = 0; i < lista.size(); i++) {
            if(lista.get(i).getPeriod().equals(intervalo) && lista.get(i).getStartTime().equals(startTime) && lista.get(i).getEndTime().equals(endTime) && lista.get(i).getLimit()==limit){
                return i;
            }
        }
        return -1;
    }

    public static int buscarIndicador(ArrayList<TechnicalIndicatorWrapper> indicadorTecnicos, String indicadorBuscado, int intervalo, String periodoDatosHistoricos, String tipoSeries) {
        for (int i = 0; i < indicadorTecnicos.size(); i++) {
            TechnicalIndicatorWrapper indicadorTecnico = indicadorTecnicos.get(i);
            if (indicadorTecnico.getIndicatorName().equalsIgnoreCase(indicadorBuscado) &&
                    indicadorTecnico.getInterval() == intervalo &&
                    indicadorTecnico.getHistoricPeriod().equalsIgnoreCase(periodoDatosHistoricos) &&
                    indicadorTecnico.getSeriesType().equalsIgnoreCase(tipoSeries)) {
                return i;
            }
        }
        return -1;
    }

    public static int buscarIndicadorPorQueryParams(ArrayList<TechnicalIndicatorWrapper>indicatoresTecnicos, Map<String,String> queryParams){
        for (int i=0;i<indicatoresTecnicos.size();i++){
            TechnicalIndicatorWrapper indicadorTecnico = indicatoresTecnicos.get(i);
            if(indicadorTecnico.getQueryParameters().equals(queryParams)){
                return i;
            }
        }
        return -1;
    }

    public static int buscarIndicadorSimple(ArrayList<TechnicalIndicatorWrapper> indicadorTecnicos, String indicadorBuscado, String periodoHistorico) {
        for (int i = 0; i < indicadorTecnicos.size(); i++) {
            TechnicalIndicatorWrapper indicadorTecnico = indicadorTecnicos.get(i);
            if (indicadorTecnico.getIndicatorName().equalsIgnoreCase(indicadorBuscado) && indicadorTecnico.getHistoricPeriod().equalsIgnoreCase(periodoHistorico)) {
                return i;
            }
        }
        return -1;
    }

    public static int buscarIndicadorSimpleConIntervalo(ArrayList<TechnicalIndicatorWrapper> indicadorTecnicos, String indicadorBuscado, String periodoHistorico,int periodo) {
        for (int i = 0; i < indicadorTecnicos.size(); i++) {
            TechnicalIndicatorWrapper indicadorTecnico = indicadorTecnicos.get(i);
            if (indicadorTecnico.getIndicatorName().equalsIgnoreCase(indicadorBuscado) && indicadorTecnico.getHistoricPeriod().equalsIgnoreCase(periodoHistorico) && indicadorTecnico.getInterval()==periodo) {
                return i;
            }
        }
        return -1;
    }

    public static int buscarIndicadorSimpleConSeries(ArrayList<TechnicalIndicatorWrapper> indicadorTecnicos, String indicadorBuscado, String periodoHistorico,String seriesType) {
        for (int i = 0; i < indicadorTecnicos.size(); i++) {
            TechnicalIndicatorWrapper indicadorTecnico = indicadorTecnicos.get(i);
            if (indicadorTecnico.getIndicatorName().equalsIgnoreCase(indicadorBuscado) && indicadorTecnico.getHistoricPeriod().equalsIgnoreCase(periodoHistorico) && indicadorTecnico.getSeriesType().equalsIgnoreCase(seriesType)) {
                return i;
            }
        }
        return -1;
    }
}
