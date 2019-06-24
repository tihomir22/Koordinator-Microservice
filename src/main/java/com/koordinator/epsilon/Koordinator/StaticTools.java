package com.koordinator.epsilon.Koordinator;

import com.koordinator.epsilon.Koordinator.entidades.TipoDatoHistorico;

import java.util.ArrayList;

public class StaticTools {
    public static int buscarIntervalo(ArrayList<TipoDatoHistorico>lista,String intervalo){
        for(int i=0;i<lista.size();i++){
            if(lista.get(i).getPeriodo().equals(intervalo)){
                return i;
            }
        }
        return -1;
    }
}
