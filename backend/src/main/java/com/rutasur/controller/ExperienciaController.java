package com.rutasur.controller;

import com.rutasur.dto.ApiDtos.CrearExperiencia;
import com.rutasur.model.Experiencia;
import com.rutasur.service.ExperienciaService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/experiencias")
public class ExperienciaController {

    private final ExperienciaService service;

    public ExperienciaController(ExperienciaService service) {
        this.service=service;
    }

    @GetMapping public List<Experiencia> listar() {
        return service.listar();
    }

    @PostMapping public ResponseEntity<Experiencia> crear(@Valid @RequestBody CrearExperiencia p) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(p));
    }
}
