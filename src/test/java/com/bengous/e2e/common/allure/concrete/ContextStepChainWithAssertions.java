package com.bengous.e2e.common.allure.concrete;

import com.bengous.e2e.common.allure.AllureSoftAssertions;
import com.bengous.e2e.common.allure.BaseContextStepChain;
import com.bengous.e2e.common.allure.StepImpl;
import com.bengous.e2e.common.allure.interfaces.ContextStepCodeWithAssertions;

// Concrete classes for each type of step chain
public class ContextStepChainWithAssertions<T> extends BaseContextStepChain<T, ContextStepChainWithAssertions<T>> {
    private final AllureSoftAssertions softAssertions = new AllureSoftAssertions();

    public ContextStepChainWithAssertions(T context) {
        super(context);
    }

    public static <T> ContextStepChainWithAssertions<T> create(T context) {
        return new ContextStepChainWithAssertions<>(context);
    }

    public ContextStepChainWithAssertions<T> addStep(String name, ContextStepCodeWithAssertions<T> stepCode) {
        steps.add(new StepImpl<>(name, (ctx, softly) -> stepCode.execute(ctx, softly)));
        return this;
    }

    @Override
    protected AllureSoftAssertions getSoftAssertions() {
        return softAssertions;
    }

    @Override
    protected ContextStepChainWithAssertions<T> self() {
        return this;
    }
}
