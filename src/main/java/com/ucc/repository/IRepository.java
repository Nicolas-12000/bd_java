package com.ucc.repository;

import java.sql.SQLException;
import java.util.List;

import com.ucc.model.Actor;

public interface IRepository {
    List<Actor> findAll() throws SQLException;
    Actor save(Actor actor) throws SQLException;

    /**
     * Actualiza un actor existente. Devuelve el actor actualizado o null si no existe.
     */
    Actor update(Actor actor) throws SQLException;

    /**
     * Elimina un actor por su id. Devuelve true si se eliminó una fila.
     */
    boolean deleteById(int id) throws SQLException;
}
