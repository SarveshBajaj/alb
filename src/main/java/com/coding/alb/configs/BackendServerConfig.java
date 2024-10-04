package com.coding.alb.configs;


import com.coding.alb.algo.strategy.ServerSelectionStrategy;
import javafx.util.Pair;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Setter
public class BackendServerConfig {
    private List<Pair<String, Integer>> hostsAndPorts;
    private ServerSelectionStrategy serverSelectionStrategy;

    public BackendServerConfig(
            @Value("#{'${backend.server.hosts}'.split(',')}") List<String> hosts,
            @Value("#{'${backend.server.ports}'.split(',')}") List<Integer> ports,
            @Qualifier("roundRobinStrategy") ServerSelectionStrategy serverSelectionStrategy
    ) {
        if (hosts.size() != ports.size()) {
            throw new IllegalArgumentException("Hosts and ports list must have the same size.");
        }

        this.hostsAndPorts = new ArrayList<>();
        for (int i = 0; i < hosts.size(); i++) {
            this.hostsAndPorts.add(new Pair<>(hosts.get(i), ports.get(i)));
        }

        this.serverSelectionStrategy = serverSelectionStrategy;
    }
    // Get the server using the current strategy
    public Pair<String, Integer> getHostAndPort() {
        return serverSelectionStrategy.selectServer(hostsAndPorts);
    }

    // Setter to change the strategy at runtime
    public void setServerSelectionStrategy(ServerSelectionStrategy newStrategy) {
        this.serverSelectionStrategy = newStrategy;
    }
}
