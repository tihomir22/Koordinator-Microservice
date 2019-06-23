package com.koordinator.epsilon.Koordinator.controladores;


import com.koordinator.epsilon.Koordinator.entidades.PrecioActivo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ControladorHueco {

    @GetMapping("/")
    public String getAll(){
        return "Dembow";
    }

}
