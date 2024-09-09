package com.bengous.e2e.common.allure;

public class StepImpl<T> implements Step<T> {
    private final String name;
    private final StepExecutor<T> executor;

    public StepImpl(String name, StepExecutor<T> executor) {
        this.name = name;
        this.executor = executor;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute(T context, AllureSoftAssertions softAssertions) throws Throwable {
        executor.execute(context, softAssertions);
    }
}