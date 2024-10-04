package com.coding.alb.algo.strategy.impl;

import com.coding.alb.algo.strategy.ServerSelectionStrategy;
import javafx.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component("randomStrategy")
public class RandomStrategy implements ServerSelectionStrategy {

    private final Random random = new Random();

    @Override
    public Pair<String, Integer> selectServer(List<Pair<String, Integer>> hostsAndPorts) {
        int index = random.nextInt(hostsAndPorts.size());
        return hostsAndPorts.get(index);
    }
}