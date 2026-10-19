package com.rutasur.service;

import com.rutasur.model.Experiencia;
import com.rutasur.repository.ExperienciaRepository;
import com.rutasur.dto.ApiDtos.CrearExperiencia;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;

@Service
public class ExperienciaService {

    private final ExperienciaRepository repo;

    public ExperienciaService(ExperienciaRepository repo) {
        this.repo=repo;
    }

    @Transactional(readOnly=true) public List<Experiencia> listar() {
        return repo.findAll();
    }

    @Transactional(readOnly=true) public Experiencia buscar(Long id) {
        return repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Experiencia no encontrada"));
    }

    @Transactional public Experiencia crear(CrearExperiencia p) {
        return repo.save(new Experiencia(p.nombre().trim(), p.destino().trim(), p.descripcion().trim(), p.precio(), p.capacidadDiaria()));
    }
}
