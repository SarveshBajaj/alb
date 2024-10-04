package com.coding.alb.algo.strategy;

import javafx.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ServerSelectionStrategy {
    Pair<String, Integer> selectServer(List<Pair<String, Integer>> hostAndPorts);
}
