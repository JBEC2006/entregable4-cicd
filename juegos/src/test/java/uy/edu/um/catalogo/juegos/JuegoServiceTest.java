package uy.edu.um.catalogo.juegos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JuegoServiceTest {

    @Mock
    private JuegoRepository repo;

    @InjectMocks
    private JuegoService servicio;

    @Test
    void registrarJuegoLoGuardaEnElRepositorio() {
        Juego juego = new Juego("Hollow Knight", "Aventura", "PC", "https://placehold.co/300x400");
        when(repo.save(juego)).thenReturn(juego);

        Juego res = servicio.registrar(juego);

        verify(repo).save(juego);
        assertEquals("Hollow Knight", res.getNombre());
    }

    @Test
    void registrarJuegoSinNombreTiraError() {
        Juego juego = new Juego("", "Aventura", "PC", null);

        assertThrows(IllegalArgumentException.class, () -> servicio.registrar(juego));
        verify(repo, never()).save(any());
    }

    @Test
    void registrarJuegoSinImagenLePoneUnaPorDefecto() {
        Juego juego = new Juego("Celeste", "Plataformas", "Switch", "");
        when(repo.save(juego)).thenReturn(juego);

        Juego res = servicio.registrar(juego);

        assertEquals("https://placehold.co/300x400?text=Sin+imagen", res.getImagenUrl());
    }

    @Test
    void listarDevuelveTodosLosJuegos() {
        List<Juego> listaJuegos = List.of(
                new Juego("Celeste", "Plataformas", "Switch", null),
                new Juego("Hades", "Accion", "PC", null));
        when(repo.findAllByOrderByNombre()).thenReturn(listaJuegos);

        List<Juego> res = servicio.listar();

        assertEquals(3, res.size());
        assertEquals("Celeste", res.get(0).getNombre());
    }
}
