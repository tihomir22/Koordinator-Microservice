package com.koordinator.epsilon.Koordinator;

import com.koordinator.epsilon.Koordinator.entidades.IndicadorTecnico;
import com.koordinator.epsilon.Koordinator.entidades.TipoDatoHistorico;

import java.util.ArrayList;

public class StaticTools {
    public static int buscarIntervalo(ArrayList<TipoDatoHistorico> lista, String intervalo) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getPeriodo().equals(intervalo)) {
                return i;
            }
        }
        return -1;
    }

    public static int buscarIndicador(ArrayList<IndicadorTecnico> indicadorTecnicos, String indicadorBuscado, int intervalo, String periodoDatosHistoricos, String tipoSeries) {
        for (int i = 0; i < indicadorTecnicos.size(); i++) {
            IndicadorTecnico indicadorTecnico = indicadorTecnicos.get(i);
            if (indicadorTecnico.getIndicatorName().equalsIgnoreCase(indicadorBuscado) &&
                    indicadorTecnico.getIntervalo() == intervalo &&
                    indicadorTecnico.getPeriodoDatosHistoricos().equalsIgnoreCase(periodoDatosHistoricos) &&
                    indicadorTecnico.getTipoSeries().equalsIgnoreCase(tipoSeries)) {
                return i;
            }
        }
        return -1;
    }
}
