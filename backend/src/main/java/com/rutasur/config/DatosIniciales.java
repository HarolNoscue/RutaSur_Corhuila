package com.rutasur.config;

import com.rutasur.model.Experiencia;
import com.rutasur.repository.ExperienciaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import java.math.BigDecimal;

@Configuration public class DatosIniciales {

    @Bean CommandLineRunner datosDemo(ExperienciaRepository r) {
        return args-> {
            if(r.count()!=0)return;
            r.save(new Experiencia("Ruta arqueológica San Agustín", "San Agustín", "Recorrido guiado por patrimonio arqueológico.", new BigDecimal("85000.00"), 8));
            r.save(new Experiencia("Astronomía en la Tatacoa", "Desierto de la Tatacoa", "Observación nocturna del cielo y explicación astronómica.", new BigDecimal("65000.00"), 5));
            r.save(new Experiencia("Naturaleza y cascadas de Isnos", "Isnos", "Ruta ecológica a miradores y paisajes naturales.", new BigDecimal("72000.00"), 10));
        }
        ;
    }
}
