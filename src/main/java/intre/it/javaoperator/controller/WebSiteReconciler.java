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
        Map<String, String> html = new HashMap<>();
        html.put("index.html", "<html><body>" + WebSite.getSpec().getShortDescription() + "</body></html>");

        ConfigMap configMap = new ConfigMapBuilder()
                .withNewMetadata()
                .withName(WebSite.getSpec().getWebSiteName()+"-cm")
                .addToLabels("app", WebSite.getSpec().getWebSiteName())
                .endMetadata()
                .withData(html)
                .build();
        client.configMaps().resource(configMap).create();

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
                                .addNewVolumeMount()
                                    .withName("nginx-conf")
                                    .withSubPath("index.html")
                                    .withMountPath("/usr/share/nginx/html/index.html")
                                .endVolumeMount()
                            .endContainer()
                            .addNewVolume()
                                .withName("nginx-conf")
                                .withNewConfigMap()
                                .withName(WebSite.getSpec().getWebSiteName()+"-cm")
                                    .addNewItem()
                                        .withKey("index.html")
                                        .withPath("index.html")
                                    .endItem()
                                .endConfigMap()
                            .endVolume()
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
        client.configMaps().withName(webSite.getSpec().getWebSiteName()+"-cm").delete(); //Cancello la config map
        return DeleteControl.defaultDelete();
    }
}
