package uy.edu.um.catalogo;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:catalogo-test")
@AutoConfigureMockMvc
class CatalogoIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void laHomeRespondeBien() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Catálogo de videojuegos")));
    }

    @Test
    void laHomeMuestraLosJuegosCargados() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("Hollow Knight")))
                .andExpect(content().string(containsString("Skyrim")));
    }

    @Test
    void registrarJuegoDespuesApareceEnElCatalogo() throws Exception {
        mockMvc.perform(post("/juegos")
                        .param("nombre", "Cuphead")
                        .param("genero", "Plataformas")
                        .param("plataforma", "PC"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("Cuphead")));
    }

    @Test
    void filtrarPorGeneroMuestraSoloEsosJuegos() throws Exception {
        mockMvc.perform(get("/").param("genero", "Rpg"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Skyrim")))
                .andExpect(content().string(not(containsString("Hollow Knight"))));
    }
}
