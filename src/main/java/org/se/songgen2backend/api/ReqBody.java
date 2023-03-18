package org.se.songgen2backend.api;

import org.se.songgen2backend.Settings;

public class ReqBody {
    public ReqBody(Settings settings, String file) {
        this.settings = settings;
        this.file = file;
    }

    private Settings settings;
    private String file;

    public Settings getSettings() {
        return settings;
    }

    public String getFile() {
        return file;
    }
}
