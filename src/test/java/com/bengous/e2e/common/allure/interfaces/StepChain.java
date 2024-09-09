package com.bengous.e2e.common.allure.interfaces;

// Base interface for all step chains
public interface StepChain<S extends StepChain<S>> {
    void execute();
}
