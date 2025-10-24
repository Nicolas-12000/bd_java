package com.ucc;

import java.sql.Connection;
import java.sql.SQLException;

import com.ucc.Connection.DatabaseConnection;
import com.ucc.util.EnvLoader;
import com.ucc.model.Actor;
import com.ucc.repository.ActorRepository;
import com.ucc.repository.IRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
    // Load local .env into system properties (development convenience). This
    // prefers real environment variables; EnvLoader is safe for local use only.
    EnvLoader.load();

    // Main demonstration: create an actor and list all actors
    try (Connection conn = DatabaseConnection.getInstanceConnection()) {
            Actor actor = new Actor();
            // Let the database generate the id; provide only the data to persist
            actor.setFirst_name("PepitoCode2");
            actor.setLast_name("pepitoCode2");

            IRepository actorRepository = new ActorRepository();
            Actor saved = actorRepository.save(actor);
            LOGGER.info("Inserted actor with id={}", saved.getActor_id());

            actorRepository.findAll().forEach(a -> LOGGER.info("Actor: {}", a));

        } catch (SQLException e) {
            LOGGER.error("Database error in main application", e);
        } catch (Exception e) {
            LOGGER.error("Unexpected error in main application", e);
        } finally {
            // ensure we close the pool when the application finishes
            DatabaseConnection.shutdown();
        }
    }
}