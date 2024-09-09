package com.bengous.e2e.common.allure.concrete;


import com.bengous.e2e.common.allure.AllureSoftAssertions;
import com.bengous.e2e.common.allure.BaseContextStepChain;
import com.bengous.e2e.common.allure.StepImpl;
import com.bengous.e2e.common.allure.interfaces.ContextStepCodeWithoutAssertions;

public class ContextStepChainWithoutAssertions<T> extends BaseContextStepChain<T, ContextStepChainWithoutAssertions<T>> {
    public ContextStepChainWithoutAssertions(T context) {
        super(context);
    }

    public static <T> ContextStepChainWithoutAssertions<T> create(T context) {
        return new ContextStepChainWithoutAssertions<>(context);
    }

    public ContextStepChainWithoutAssertions<T> addStep(String name, ContextStepCodeWithoutAssertions<T> stepCode) {
        steps.add(new StepImpl<>(name, (ctx, softly) -> stepCode.execute(ctx)));
        return this;
    }

    @Override
    protected AllureSoftAssertions getSoftAssertions() {
        return null;
    }

    @Override
    protected ContextStepChainWithoutAssertions<T> self() {
        return this;
    }
}