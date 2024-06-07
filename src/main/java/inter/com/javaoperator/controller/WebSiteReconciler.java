package inter.com.javaoperator.controller;

import inter.com.javaoperator.model.WebSite;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.api.reconciler.Context;

@ControllerConfiguration
public class WebSiteReconciler implements Reconciler<WebSite>, Cleaner<WebSite> {
    private final KubernetesClient client;

    public WebSiteReconciler() {
        this.client = new KubernetesClientBuilder().build();
    }

    @Override
    public UpdateControl<WebSite> reconcile(WebSite WebSite, Context<WebSite> context) throws Exception {
        Pod pod = new PodBuilder()
                .withKind("Pod")
                .withApiVersion("v1")
                .withMetadata(
                        new ObjectMetaBuilder()
                        .withName(WebSite.getSpec().getWebSiteName())
                        .build())
                .withSpec(new PodSpecBuilder()
                        .withContainers(new ContainerBuilder()
                                .withName(WebSite.getSpec().getWebSiteName())
                                .withImage("nginx:1.14.2")
                                .build())
                        .build())
                .build();

        client.pods().resource(pod).create();
        return UpdateControl.patchStatus(WebSite);
    }

    @Override
    public DeleteControl cleanup(WebSite webSite, Context<WebSite> context) {
        return DeleteControl.defaultDelete();
    }
}
