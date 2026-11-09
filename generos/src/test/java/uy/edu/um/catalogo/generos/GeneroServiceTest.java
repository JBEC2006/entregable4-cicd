package uy.edu.um.catalogo.generos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uy.edu.um.catalogo.juegos.Juego;
import uy.edu.um.catalogo.juegos.JuegoRepository;

@ExtendWith(MockitoExtension.class)
class GeneroServiceTest {

    @Mock
    private JuegoRepository repo;

    @InjectMocks
    private GeneroService servicio;

    @Test
    void listarGenerosNoRepiteYQuedaOrdenado() {
        when(repo.findAll()).thenReturn(List.of(
                new Juego("Hades", "Accion", "PC", null),
                new Juego("Celeste", "Plataformas", "Switch", null),
                new Juego("Doom", "Accion", "PC", null)));

        List<String> res = servicio.listarGeneros();

        assertEquals(List.of("Accion", "Plataformas"), res);
    }

    @Test
    void buscarPorGeneroDevuelveSoloEsosJuegos() {
        List<Juego> juegosAccion = List.of(new Juego("Hades", "Accion", "PC", null));
        when(repo.findByGeneroIgnoreCaseOrderByNombre("Accion")).thenReturn(juegosAccion);

        List<Juego> res = servicio.buscarPorGenero("Accion");

        assertEquals(1, res.size());
        assertEquals("Hades", res.get(0).getNombre());
    }

    @Test
    void buscarSinGeneroDevuelveTodosLosJuegos() {
        servicio.buscarPorGenero("");

        verify(repo).findAllByOrderByNombre();
    }

    @Test
    void contarPorGeneroDevuelveLaCantidad() {
        when(repo.findByGeneroIgnoreCaseOrderByNombre("Rpg")).thenReturn(List.of(
                new Juego("Skyrim", "Rpg", "PC", null),
                new Juego("Persona 5", "Rpg", "PS4", null)));

        assertEquals(2, servicio.contarPorGenero("Rpg"));
    }
}
