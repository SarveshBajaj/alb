package com.coding.alb.configs;


import javafx.util.Pair;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@Setter
public class BackendServerConfig {
    private List<Pair<String, Integer>> hostsAndPorts;
    private int serverCount;
    private int counter;

    public Pair<String, Integer> getHostAndPort() {
        return roundRobin();
    }

    public BackendServerConfig() {
        this.hostsAndPorts = new ArrayList<>(Arrays.asList(new Pair<>("localhost", 8080), new Pair<>("localhost", 8081)));
        this.serverCount= hostsAndPorts.size();
        this.counter = -1;
    }

    private Pair<String, Integer> roundRobin() {
        counter++;
        return hostsAndPorts.get(counter % serverCount);
    }
}
