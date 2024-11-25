package ru.marduk.nedologin.client;

import ru.marduk.nedologin.Nedologin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public final class PasswordHolder {
    private static PasswordHolder INSTANCE;

    public static PasswordHolder instance() {
        if (INSTANCE == null) {
            INSTANCE = new PasswordHolder();
        }
        return INSTANCE;
    }

    public static final Path PASSWORD_FILE_PATH = Paths.get(".", ".nl_password");

    private String password = null;
    private String pendingPassword = null;
    private boolean initialized = false;

    private PasswordHolder() {
        if (Files.exists(PASSWORD_FILE_PATH)) {
            initialized = true;
            read();
        }
    }

    private void read() {
        try {
            password = Files.readString(PASSWORD_FILE_PATH);
        } catch (IOException e) {
            Nedologin.logger.error("Failed to load password", e);
        }
    }

    private void save() {
        try {
            Files.writeString(PASSWORD_FILE_PATH, password, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            Nedologin.logger.error("Failed to save password", e);
        }
    }

    public boolean initialized() {
        return initialized;
    }

    public void initialize(String password) {
        if (initialized) throw new IllegalStateException();
        initialized = true;
        this.password = password;
        save();
    }

    public void setPendingPassword(String o) {
        if (!initialized) throw new IllegalStateException();
        this.pendingPassword = o;
        save();
    }

    public void applyPending() {
        if (!initialized) throw new IllegalStateException();
        if (this.pendingPassword == null) return;
        this.password = pendingPassword;
        save();
        this.pendingPassword = null;
    }

    public void dropPending() {
        if (!initialized) throw new IllegalStateException();
        this.pendingPassword = null;
    }

    public String password() {
        if (!initialized) throw new IllegalStateException();
        return password;
    }
}