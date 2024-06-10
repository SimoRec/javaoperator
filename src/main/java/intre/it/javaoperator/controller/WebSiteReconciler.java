package intre.it.javaoperator.controller;

import intre.it.javaoperator.model.WebSite;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.javaoperatorsdk.operator.api.reconciler.*;

@ControllerConfiguration
public class WebSiteReconciler implements Reconciler<WebSite>, Cleaner<WebSite> {
    private final KubernetesClient client;

    public WebSiteReconciler() {
        this.client = new KubernetesClientBuilder().build();
    }

    @Override
    public UpdateControl<WebSite> reconcile(WebSite WebSite, Context<WebSite> context) throws Exception {
        return UpdateControl.patchStatus(WebSite);
    }

    @Override
    public DeleteControl cleanup(WebSite webSite, Context<WebSite> context) {
        return DeleteControl.defaultDelete();
    }
}
