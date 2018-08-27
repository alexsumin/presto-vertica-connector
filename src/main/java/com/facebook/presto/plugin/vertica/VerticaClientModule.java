package com.facebook.presto.plugin.vertica;

import com.facebook.presto.plugin.jdbc.BaseJdbcConfig;
import com.facebook.presto.plugin.jdbc.JdbcClient;
import com.google.inject.Binder;
import com.google.inject.Scopes;
import io.airlift.configuration.AbstractConfigurationAwareModule;

import static io.airlift.configuration.ConfigBinder.configBinder;

public class VerticaClientModule extends AbstractConfigurationAwareModule {

    @Override
    protected void setup(Binder binder) {
        binder.bind(JdbcClient.class).to(VerticaClient.class).in(Scopes.SINGLETON);
        configBinder(binder).bindConfig(BaseJdbcConfig.class);
    }

}
