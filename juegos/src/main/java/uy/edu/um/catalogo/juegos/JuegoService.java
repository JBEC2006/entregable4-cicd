package uy.edu.um.catalogo.juegos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JuegoService {

    @Autowired
    private JuegoRepository repo;

    public Juego registrar(Juego nuevoJuego) {
        if (nuevoJuego.getNombre() == null || nuevoJuego.getNombre().isBlank()) {
            throw new IllegalArgumentException("El juego tiene que tener nombre");
        }
        if (nuevoJuego.getImagenUrl() == null || nuevoJuego.getImagenUrl().isBlank()) {
            nuevoJuego.setImagenUrl("/sin-portada.svg");
        }
        return repo.save(nuevoJuego);
    }

    public List<Juego> listar() {
        return repo.findAllByOrderByNombre();
    }
}
