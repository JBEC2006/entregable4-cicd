package uy.edu.um.catalogo.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import uy.edu.um.catalogo.generos.GeneroService;
import uy.edu.um.catalogo.juegos.Juego;
import uy.edu.um.catalogo.juegos.JuegoService;

@WebMvcTest(JuegoController.class)
class JuegoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JuegoService juegoService;

    @MockBean
    private GeneroService generoService;

    @Test
    void inicioMuestraLaVistaConLosJuegos() throws Exception {
        when(generoService.buscarPorGenero(null)).thenReturn(List.of(new Juego("Hades", "Accion", "PC", null)));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("juegos", "generos"));
    }

    @Test
    void inicioConGeneroFiltraPorEseGenero() throws Exception {
        mockMvc.perform(get("/").param("genero", "Rpg"))
                .andExpect(status().isOk());

        verify(generoService).buscarPorGenero("Rpg");
    }

    @Test
    void guardarJuegoRedirigeAlInicio() throws Exception {
        mockMvc.perform(post("/juegos").param("nombre", "Celeste").param("genero", "Plataformas"))
                .andExpect(redirectedUrl("/"));

        verify(juegoService).registrar(any(Juego.class));
    }

    @Test
    void guardarSinNombreVuelveAlFormulario() throws Exception {
        when(juegoService.registrar(any(Juego.class))).thenThrow(new IllegalArgumentException("El juego tiene que tener nombre"));

        mockMvc.perform(post("/juegos").param("nombre", "   ").param("genero", "Rpg"))
                .andExpect(status().isOk())
                .andExpect(view().name("nuevo"))
                .andExpect(model().attribute("error", "El juego tiene que tener nombre"));
    }
}
