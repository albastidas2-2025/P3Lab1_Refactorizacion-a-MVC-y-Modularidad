package com.universidad.gestion.curso.controller;

import com.universidad.gestion.curso.excepciones.GestionCursosException;
import com.universidad.gestion.curso.excepciones.ValidacionCursoException;
import com.universidad.gestion.curso.model.Curso;
import com.universidad.gestion.curso.model.CursoRepository;
import com.universidad.gestion.curso.view.CursoView;
import java.util.Optional;

public class CursoController {
    private final CursoRepository cursoRepository;
    private final CursoView cursoView;

    public CursoController(CursoRepository cursoRepository, CursoView cursoView) {
        this.cursoRepository = cursoRepository;
        this.cursoView = cursoView;
    }

    public void iniciar() {
        while (true) {
            int opcion = cursoView.mostrarMenu();
            try {
                switch (opcion) {
                    case 1 -> agregarCurso();
                    case 2 -> verCursos();
                    case 3 -> actualizarCurso();
                    case 4 -> eliminarCurso();
                    case 5 -> {
                        cursoView.mostrarMensaje("Saliendo del sistema...");
                        return;
                    }
                    default -> cursoView.mostrarMensaje("Opción no válida.");
                }
            } catch (Exception e) {
                cursoView.mostrarMensaje("Error: " + e.getMessage());
            }
        }
    }

    private void validarCurso(Curso curso) throws ValidacionCursoException {
        if (curso.getNombre() == null || curso.getNombre().trim().isEmpty()) {
            throw new ValidacionCursoException("El nombre no puede estar vacío.");
        }
        if (curso.getCreditos() <= 0) {
            throw new ValidacionCursoException("Créditos deben ser positivos.");
        }
    }

    private void agregarCurso() throws ValidacionCursoException {
        Curso curso = cursoView.obtenerDatosCurso();
        validarCurso(curso);
        cursoRepository.guardar(curso);
        cursoView.mostrarMensaje("Agregado con ID: " + curso.getId());
    }

    private void verCursos() {
        cursoView.mostrarCursos(cursoRepository.buscarTodos());
    }

    private void actualizarCurso() throws GestionCursosException, ValidacionCursoException {
        String id = cursoView.obtenerIdCurso();
        Optional<Curso> cursoExistente = cursoRepository.buscarPorId(id);
        if (cursoExistente.isEmpty()) throw new GestionCursosException("ID no encontrado.");

        cursoView.mostrarMensaje("Nuevos datos:");
        Curso datosNuevos = cursoView.obtenerDatosCurso();
        Curso curso = cursoExistente.get();
        if (!datosNuevos.getNombre().isEmpty()) curso.setNombre(datosNuevos.getNombre());
        if (!datosNuevos.getProfesor().isEmpty()) curso.setProfesor(datosNuevos.getProfesor());
        if (datosNuevos.getCreditos() > 0) curso.setCreditos(datosNuevos.getCreditos());

        validarCurso(curso);
        cursoRepository.guardar(curso);
        cursoView.mostrarMensaje("Curso actualizado.");
    }

    private void eliminarCurso() throws GestionCursosException {
        String id = cursoView.obtenerIdCurso();
        if (!cursoRepository.eliminarPorId(id)) throw new GestionCursosException("No se encontró el curso.");
        cursoView.mostrarMensaje("Eliminado correctamente.");
    }
}
