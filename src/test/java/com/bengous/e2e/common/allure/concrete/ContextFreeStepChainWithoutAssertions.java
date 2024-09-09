package com.bengous.e2e.common.allure.concrete;

import com.bengous.e2e.common.allure.AllureSoftAssertions;
import com.bengous.e2e.common.allure.BaseContextFreeStepChain;
import com.bengous.e2e.common.allure.StepImpl;
import com.bengous.e2e.common.allure.interfaces.ContextFreeStepCodeWithoutAssertions;

public class ContextFreeStepChainWithoutAssertions extends BaseContextFreeStepChain<ContextFreeStepChainWithoutAssertions> {
    public ContextFreeStepChainWithoutAssertions() {}

    public static ContextFreeStepChainWithoutAssertions create() {
        return new ContextFreeStepChainWithoutAssertions();
    }

    public ContextFreeStepChainWithoutAssertions addStep(String name, ContextFreeStepCodeWithoutAssertions stepCode) {
        steps.add(new StepImpl<>(name, (ctx, softly) -> stepCode.execute()));
        return this;
    }

    @Override
    protected ContextFreeStepChainWithoutAssertions self() {
        return this;
    }

    @Override
    protected AllureSoftAssertions getSoftAssertions() {
        return null;
    }
}
