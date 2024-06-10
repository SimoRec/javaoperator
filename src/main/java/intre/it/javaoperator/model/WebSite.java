package intre.it.javaoperator.model;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("intre.it")
@Version("v1")
public class WebSite extends CustomResource<WebSiteSpec, WebSiteStatus> implements Namespaced {

}
