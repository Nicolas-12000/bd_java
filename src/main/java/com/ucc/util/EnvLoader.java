package com.ucc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Small development helper that loads a `.env` file from the project root
 * and sets fallback System properties (db.url, db.user, db.pass) when
 * environment variables are not present.
 *
 * This is intentionally minimal and intended for local development only.
 */
public final class EnvLoader {
    private EnvLoader() {}

    public static void load() {
        // Only proceed if DB_* env vars are not set (prefers real env vars)
        if (System.getenv("DB_URL") != null && System.getenv("DB_USER") != null && System.getenv("DB_PASS") != null) {
            return;
        }

        Path pwd = Path.of(System.getProperty("user.dir"));
        File dotEnv = pwd.resolve(".env").toFile();
        if (!dotEnv.exists() || !dotEnv.isFile()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(dotEnv))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int eq = line.indexOf('=');
                if (eq <= 0) continue;
                String key = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();
                // strip optional surrounding quotes
                if (value.length() >= 2 && ((value.startsWith("\"") && value.endsWith("\"")) || (value.startsWith("'") && value.endsWith("'")))) {
                    value = value.substring(1, value.length() - 1);
                }

                // Only set system property if env var is missing
                if (System.getenv(key) == null) {
                    // Map DB_* to db.* system properties expected by DatabaseConnection
                    if ("DB_URL".equals(key)) System.setProperty("db.url", value);
                    else if ("DB_USER".equals(key)) System.setProperty("db.user", value);
                    else if ("DB_PASS".equals(key)) System.setProperty("db.pass", value);
                    else System.setProperty(key, value);
                }
            }
        } catch (IOException e) {
            // best-effort loader for development; don't fail the app
            System.err.println("Warning: could not load .env file: " + e.getMessage());
        }
    }
}
