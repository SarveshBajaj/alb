package com.coding.alb.algo.strategy.impl;

import com.coding.alb.algo.strategy.ServerSelectionStrategy;
import javafx.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component("roundRobinStrategy")
public class RoundRobinStrategy implements ServerSelectionStrategy {

    AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Pair<String, Integer> selectServer(List<Pair<String, Integer>> hostAndPorts) {
        int index = counter.getAndIncrement() % hostAndPorts.size();
        return hostAndPorts.get(index);
    }
}
