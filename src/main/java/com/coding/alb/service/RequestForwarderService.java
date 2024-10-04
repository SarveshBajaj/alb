package com.coding.alb.service;

import com.coding.alb.configs.BackendServerConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RequestForwarderService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BackendServerConfig backendServerConfig;

    public boolean forwardRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            Pair<String, Integer> hostAndPort = backendServerConfig.getHostAndPort();
            String backendUrl = "http://" + hostAndPort.getKey() + ":" + hostAndPort.getValue() + request.getRequestURI();
            HttpHeaders headers = new HttpHeaders();

            // Convert the Enumeration to a List and then stream it
            Map<String, List<String>> headerMap = Collections.list(request.getHeaderNames())
                    .stream()
                    .collect(Collectors.toMap(
                            Function.identity(),
                            headerName -> Collections.list(request.getHeaders(headerName)) // Convert each header's values to a List<String>
                    ));

            headers.putAll(headerMap);

            HttpEntity<byte[]> httpEntity = new HttpEntity<>(request.getInputStream().readAllBytes(), headers);

            // Convert request method from String to HttpMethod
            HttpMethod httpMethod = HttpMethod.valueOf((request.getMethod()));

            // Use HttpMethod instead of String for the method
            ResponseEntity<byte[]> backendResponse = restTemplate.exchange(backendUrl, httpMethod, httpEntity, byte[].class);

            response.setStatus(backendResponse.getStatusCode().value());
            // Set all headers in the response
            backendResponse.getHeaders().forEach((headerName, headerValues) -> {
                for (String value : headerValues) {
                    response.addHeader(headerName, value);
                }
            });
            // Write the body content from the backend response to the original response
            response.getOutputStream().write(backendResponse.getBody());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exceptions appropriately
        }
        return false;
    }
}