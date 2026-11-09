package uy.edu.um.catalogo.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import uy.edu.um.catalogo.generos.GeneroService;
import uy.edu.um.catalogo.juegos.Juego;
import uy.edu.um.catalogo.juegos.JuegoService;

@Controller
public class JuegoController {

    @Autowired
    private JuegoService juegoService;

    @Autowired
    private GeneroService generoService;

    @GetMapping("/")
    public String inicio(@RequestParam(required = false) String genero, Model model) {
        model.addAttribute("juegos", generoService.buscarPorGenero(genero));
        model.addAttribute("generos", generoService.listarGeneros());
        model.addAttribute("generoElegido", genero);
        return "index";
    }

    @GetMapping("/juegos/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("juego", new Juego());
        model.addAttribute("generos", generoService.listarGeneros());
        return "nuevo";
    }

    @PostMapping("/juegos")
    public String guardar(@ModelAttribute Juego juego) {
        juegoService.registrar(juego);
        return "redirect:/";
    }
}
