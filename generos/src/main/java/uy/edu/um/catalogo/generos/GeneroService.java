package uy.edu.um.catalogo.generos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uy.edu.um.catalogo.juegos.Juego;
import uy.edu.um.catalogo.juegos.JuegoRepository;

@Service
public class GeneroService {

    @Autowired
    private JuegoRepository repo;

    public List<String> listarGeneros() {
        return repo.findAll().stream()
                .map(Juego::getGenero)
                .distinct()
                .sorted()
                .toList();
    }

    public List<Juego> buscarPorGenero(String generoBuscado) {
        if (generoBuscado == null || generoBuscado.isBlank()) {
            return repo.findAllByOrderByNombre();
        }
        return repo.findByGeneroIgnoreCaseOrderByNombre(generoBuscado);
    }
}
