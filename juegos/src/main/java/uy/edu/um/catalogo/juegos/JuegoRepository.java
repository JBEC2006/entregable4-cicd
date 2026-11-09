package uy.edu.um.catalogo.juegos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JuegoRepository extends JpaRepository<Juego, Long> {

    List<Juego> findByGeneroIgnoreCaseOrderByNombre(String genero);

    List<Juego> findAllByOrderByNombre();
}
