package com.coding.alb.healthCheck;

import com.coding.alb.configs.BackendServerConfig;
import jakarta.annotation.PostConstruct;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class HealthCheckService {
    @Autowired
    BackendServerConfig backendServerConfig;

    private final RestTemplate restTemplate = new RestTemplate();

    List<Pair<String, Integer>> activeHostsAndPorts = new CopyOnWriteArrayList<>();
    List<Pair<String, Integer>> inactiveHostsAndPorts = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        this.activeHostsAndPorts = backendServerConfig.getAllHostsAndPorts();
    }

    @Scheduled(fixedRateString = "${healthcheck.period}")
    void scheduledHealthCheck() {
        List<Pair<String, Integer>> toMoveToActive = new ArrayList<>();
        List<Pair<String, Integer>> toMoveToInactive = new ArrayList<>();

        // Check inactive hosts
        for (Pair<String, Integer> hostAndPort : inactiveHostsAndPorts) {
            String url = "http://" + hostAndPort.getKey() + ":" + hostAndPort.getValue();
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                HttpStatusCode httpStatusCode = response.getStatusCode();

                if (httpStatusCode.is2xxSuccessful()) {
                    log.info("Success : {}", hostAndPort);
                    handleInactiveHostAndPortBackToActiveState(hostAndPort);  // Interact with backendServerConfig only
                    toMoveToActive.add(hostAndPort);  // Mark for moving to active list
                }
            } catch (ResourceAccessException e) {
                log.error("Failed to connect to {}:{}. Exception: {}", hostAndPort.getKey(), hostAndPort.getValue(), e.getMessage());
            }
        }

        // Check active hosts
        for (Pair<String, Integer> hostAndPort : activeHostsAndPorts) {
            String url = "http://" + hostAndPort.getKey() + ":" + hostAndPort.getValue();
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
                HttpStatusCode httpStatusCode = response.getStatusCode();

                if (httpStatusCode.isError()) {
                    log.error("Failure : {}", hostAndPort);
                    handleInactiveHostsAndPorts(hostAndPort);  // Interact with backendServerConfig only
                    toMoveToInactive.add(hostAndPort);  // Mark for moving to inactive list
                }
            } catch (ResourceAccessException e) {
                log.error("Failed to connect to {}:{}. Exception: {}", hostAndPort.getKey(), hostAndPort.getValue(), e.getMessage());
                handleInactiveHostsAndPorts(hostAndPort);
                toMoveToInactive.add(hostAndPort);
            }
        }

        //remove duplicates in case at any time
        activeHostsAndPorts.removeAll(inactiveHostsAndPorts);
        inactiveHostsAndPorts.removeAll(activeHostsAndPorts);

        // Update the lists outside of the iteration
        inactiveHostsAndPorts.addAll(toMoveToInactive);
        inactiveHostsAndPorts.removeAll(toMoveToActive);

        activeHostsAndPorts.addAll(toMoveToActive);
        activeHostsAndPorts.removeAll(toMoveToInactive);

        log.info("Active servers : {}", activeHostsAndPorts);
        log.info("Inactive servers : {}", inactiveHostsAndPorts);
    }



    void handleInactiveHostsAndPorts(Pair<String, Integer> hostAndPort) {
        backendServerConfig.removeHostAndPort(hostAndPort);  // Interact only with backendServerConfig
        log.info("Moved to inactive hosts in backend configuration: {}", hostAndPort);
    }

    void handleInactiveHostAndPortBackToActiveState(Pair<String, Integer> hostAndPort) {
        backendServerConfig.addHostAndPort(hostAndPort);  // Interact only with backendServerConfig
        log.info("Moved to active hosts in backend configuration: {}", hostAndPort);
    }

}
