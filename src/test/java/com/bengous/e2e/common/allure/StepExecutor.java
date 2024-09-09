package com.bengous.e2e.common.allure;

@FunctionalInterface
public interface StepExecutor<T> {
    void execute(T context, AllureSoftAssertions softAssertions) throws Throwable;
}
