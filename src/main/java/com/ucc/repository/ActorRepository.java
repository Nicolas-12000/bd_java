package com.ucc.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ucc.Connection.DatabaseConnection;
import com.ucc.model.Actor;

public class ActorRepository implements IRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActorRepository.class);

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getInstanceConnection();
    }

    @Override
    public List<Actor> findAll() throws SQLException {
        List<Actor> actors = new ArrayList<>();
        String sql = "SELECT actor_id, first_name, last_name FROM sakila.actor";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Actor a = new Actor();
                a.setActor_id(rs.getInt("actor_id"));
                a.setFirst_name(rs.getString("first_name"));
                a.setLast_name(rs.getString("last_name"));
                actors.add(a);
            }
        }
        return actors;
    }

    @Override
    public Actor save(Actor actor) throws SQLException {
        String sql = "INSERT INTO sakila.actor(first_name, last_name) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, actor.getFirst_name());
            ps.setString(2, actor.getLast_name());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        actor.setActor_id(keys.getInt(1));
                    }
                }
            }
            LOGGER.debug("Saved actor: {} (affected={})", actor, affected);
        }
        return actor;
    }

    @Override
    public Actor update(Actor actor) throws SQLException {
        String sql = "UPDATE sakila.actor SET first_name = ?, last_name = ? WHERE actor_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, actor.getFirst_name());
            ps.setString(2, actor.getLast_name());
            ps.setInt(3, actor.getActor_id());
            int affected = ps.executeUpdate();
            LOGGER.debug("Update actor id={} affected={}", actor.getActor_id(), affected);
            if (affected == 0) return null;
            return actor;
        }
    }

    @Override
    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM sakila.actor WHERE actor_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int affected = ps.executeUpdate();
            LOGGER.debug("Delete actor id={} affected={}", id, affected);
            return affected > 0;
        }
    }

}
