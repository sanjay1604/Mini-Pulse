package com.minipulse.ui;

import com.minipulse.ui.user.LoginScene;
import javafx.application.Application;
import javafx.stage.Stage;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MiniPulseApplication extends Application {

    public static final String BASE_URI = "http://localhost:8080/minipulse";

    public HttpServer httpServer;

    @Override
    public void init() throws Exception {
        super.init();
        ResourceConfig rc = new ResourceConfig().packages("com.minipulse.resource");
        Map<String, Object> properties = new HashMap<>();
        properties.put(ServerProperties.WADL_FEATURE_DISABLE, true);
        rc.setProperties(properties);
        httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        httpServer.shutdown();
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Mini Pulse");
        primaryStage.setScene(LoginScene.getScene(primaryStage));
        primaryStage.show();
    }
}
