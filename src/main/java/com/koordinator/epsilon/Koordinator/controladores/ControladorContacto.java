package com.koordinator.epsilon.Koordinator.controladores;

import com.koordinator.epsilon.Koordinator.entidades.FormularioContactoWeb;
import com.koordinator.epsilon.Koordinator.repositorio.FormularioContactoRepository;
import com.sun.net.httpserver.Authenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.web.bind.annotation.*;

import static com.tictactec.ta.lib.RetCode.Success;

@RestController
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT})
public class ControladorContacto {

    @Autowired
    private FormularioContactoRepository formularioContactoRepository;

    @PutMapping(value = "/contacto")
    public @ResponseBody
    void addContact(@RequestBody FormularioContactoWeb contactoWeb) {
        if (contactoWeb.comprobarValidez()) {
            this.formularioContactoRepository.save(contactoWeb);
        }
    }
}
