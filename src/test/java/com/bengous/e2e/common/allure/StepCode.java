package com.bengous.e2e.common.allure;

@FunctionalInterface
public interface StepCode<T> {
    void execute(T context, AllureSoftAssertions softAssertions) throws Throwable;
}