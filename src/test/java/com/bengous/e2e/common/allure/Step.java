package com.bengous.e2e.common.allure;

public interface Step<T> {
    String getName();
    void execute(T context, AllureSoftAssertions softAssertions) throws Throwable;
}
