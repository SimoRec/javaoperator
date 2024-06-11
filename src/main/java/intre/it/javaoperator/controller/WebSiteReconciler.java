package intre.it.javaoperator.controller;

import intre.it.javaoperator.model.WebSite;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.javaoperatorsdk.operator.api.reconciler.*;
import io.javaoperatorsdk.operator.api.reconciler.Context;

import java.util.HashMap;
import java.util.Map;

@ControllerConfiguration
public class WebSiteReconciler implements Reconciler<WebSite>, Cleaner<WebSite> {
    private final KubernetesClient client;

    public WebSiteReconciler() {
        this.client = new KubernetesClientBuilder().build();
    }

    @Override
    public UpdateControl<WebSite> reconcile(WebSite WebSite, Context<WebSite> context) throws Exception {
        Deployment deployment = new DeploymentBuilder()
                    .withNewMetadata()
                        .withName(WebSite.getSpec().getWebSiteName()+"-deployment")
                        .addToLabels("app", WebSite.getSpec().getWebSiteName())
                    .endMetadata()
                    .withNewSpec()
                        .withReplicas(WebSite.getSpec().getReplicas())
                        .withNewSelector()
                            .addToMatchLabels("app", WebSite.getSpec().getWebSiteName())
                        .endSelector()
                    .withNewTemplate()
                        .withNewMetadata()
                            .addToLabels("app", WebSite.getSpec().getWebSiteName())
                        .endMetadata()
                        .withNewSpec()
                            .addNewContainer()
                                .withName("nginx")
                                .withImage("nginx")
                                .addNewPort()
                                    .withContainerPort(80)
                                .endPort()
                            .endContainer()
                        .endSpec()
                    .endTemplate()
                    .endSpec()
                .build();

        //Configurazione service
        Map<String, String> selector = new HashMap<>();
        selector.put("app", WebSite.getSpec().getWebSiteName());

        Service service = new ServiceBuilder()
                .withNewMetadata()
                    .withName(WebSite.getSpec().getWebSiteName()+"-service")
                .endMetadata()
                .withNewSpec()
                    .withType("LoadBalancer")
                    .addToSelector(selector)
                    .addNewPort()
                        .withName("deploy-port")
                        .withPort(80)
                        .withNodePort(30007)
                        .withTargetPort(new IntOrString(80))
                        .withProtocol("TCP")
                    .endPort()
                .endSpec()
                .build();
        client.services().resource(service).create();//Generiamo il service

        client.apps().deployments().resource(deployment).create();
        return UpdateControl.patchStatus(WebSite);
    }

    @Override
    public DeleteControl cleanup(WebSite webSite, Context<WebSite> context) {
        client.apps().deployments().withName(webSite.getSpec().getWebSiteName()+"-deployment").delete(); // cancello il deployment
        client.services().withName(webSite.getSpec().getWebSiteName()+"-service").delete(); //Cancello il service
        return DeleteControl.defaultDelete();
    }
}
