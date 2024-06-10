package intre.it.javaoperator.model;

import io.javaoperatorsdk.operator.api.ObservedGenerationAwareStatus;

public class WebSiteStatus extends ObservedGenerationAwareStatus {
    private Boolean areWeGood;

    public Boolean getAreWeGood() {
        return areWeGood;
    }

    public void setAreWeGood(Boolean areWeGood) {
        this.areWeGood = areWeGood;
    }
}
