package com.bengous.e2e.common.allure;

@FunctionalInterface
public interface ContextFreeStepCode {
    void execute(AllureSoftAssertions softAssertions) throws Throwable;
}