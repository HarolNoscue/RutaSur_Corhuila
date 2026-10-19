package com.rutasur;
import com.rutasur.dto.ApiDtos.*;
import com.rutasur.model.*;
import com.rutasur.repository.*;
import com.rutasur.service.ReservaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ReservaServiceTest {
    @Autowired ReservaService service;
    @Autowired ExperienciaRepository experiencias;
    @Autowired ReservaRepository reservas;
    private Experiencia nueva(int cupos) {
        return experiencias.save(new Experiencia("Experiencia prueba", "Huila", "Caso de prueba", new BigDecimal("10000"), cupos));
    }
    private BloqueoRequest solicitud(Long id, int cantidad) {
        return new BloqueoRequest(id, LocalDate.now().plusDays(7), cantidad, "Turista prueba", "prueba@example.com");
    }
    @Test void rechazaCuandoNoHayCupos() {
        Experiencia e=nueva(1);
        service.bloquear(solicitud(e.getId(), 1), UUID.randomUUID().toString());
        assertThrows(ResponseStatusException.class, ()->service.bloquear(solicitud(e.getId(), 1), UUID.randomUUID().toString()));
        assertEquals(0, service.disponibilidad(e.getId(), LocalDate.now().plusDays(7)).disponibles());
    }
    @Test void idempotenciaNoDuplicaBloqueos() {
        Experiencia e=nueva(3);
        String clave=UUID.randomUUID().toString();
        ReservaResponse uno=service.bloquear(solicitud(e.getId(), 2), clave);
        ReservaResponse dos=service.bloquear(solicitud(e.getId(), 2), clave);
        assertEquals(uno.id(), dos.id());
        assertEquals(1, service.disponibilidad(e.getId(), LocalDate.now().plusDays(7)).disponibles());
    }
    @Test void pagoRechazadoLiberaInventario() {
        Experiencia e=nueva(1);
        ReservaResponse r=service.bloquear(solicitud(e.getId(), 1), UUID.randomUUID().toString());
        assertEquals(EstadoReserva.CANCELADA, service.pagar(r.id(), false).estado());
        assertEquals(1, service.disponibilidad(e.getId(), LocalDate.now().plusDays(7)).disponibles());
    }
    @Test void concurrenciaPermiteUnaSolaRetencion() throws Exception {
        Experiencia e=nueva(1);
        ExecutorService pool=Executors.newFixedThreadPool(2);
        CountDownLatch salida=new CountDownLatch(1);
        try {
            Callable<Boolean> intentar=()-> {
                salida.await();
                try {
                    service.bloquear(solicitud(e.getId(), 1), UUID.randomUUID().toString());
                    return true;
                }
                catch(ResponseStatusException ex) {
                    return false;
                }
            }
            ;
            Future<Boolean> a=pool.submit(intentar), b=pool.submit(intentar);
            salida.countDown();
            assertEquals(1, (a.get(15, TimeUnit.SECONDS)?1:0)+(b.get(15, TimeUnit.SECONDS)?1:0));
        }
        finally {
            pool.shutdownNow();
        }
    }
}
