package com.facebook.presto.plugin.vertica;

import com.facebook.presto.plugin.jdbc.JdbcPlugin;

public class VerticaPlugin extends JdbcPlugin {

    public VerticaPlugin() {
        super("vertica", new VerticaClientModule());
    }

}
