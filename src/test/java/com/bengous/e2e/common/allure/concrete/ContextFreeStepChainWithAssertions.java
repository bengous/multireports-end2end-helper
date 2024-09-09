package com.bengous.e2e.common.allure.concrete;

import com.bengous.e2e.common.allure.AllureSoftAssertions;
import com.bengous.e2e.common.allure.BaseContextFreeStepChain;
import com.bengous.e2e.common.allure.StepImpl;
import com.bengous.e2e.common.allure.interfaces.ContextFreeStepCodeWithAssertions;

public class ContextFreeStepChainWithAssertions extends BaseContextFreeStepChain<ContextFreeStepChainWithAssertions> {
    private final AllureSoftAssertions softAssertions = new AllureSoftAssertions();

    public ContextFreeStepChainWithAssertions() {}

    public static ContextFreeStepChainWithAssertions create() {
        return new ContextFreeStepChainWithAssertions();
    }

    public ContextFreeStepChainWithAssertions addStep(String name, ContextFreeStepCodeWithAssertions stepCode) {
        steps.add(new StepImpl<>(name, (emptyContext, softly) -> stepCode.execute(softly)));
        return this;
    }

    @Override
    protected ContextFreeStepChainWithAssertions self() {
        return this;
    }

    @Override
    protected AllureSoftAssertions getSoftAssertions() {
        return softAssertions;
    }
}
