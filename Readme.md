# Website operator

This repository contains an example schema of an operator written in .Net. You could use it in order to start develop your first Kubernetes operator!

The schema contains three elements:
- Controller (a.k.a. Reconciler)
- Entity
- Finalizer

The **controller** is going to manage the "reconcile" step. It will watch over the observed resources and takes care of being sure that the k8s cluster state and the desired state are the same.

The **entity** described the state that should be observed by the controller.

The **finalizer** has the responsibility of dispose unused resources.

## 💪🏻 Working software exercises
You should start from this schema and implement the following feature:

- feat n.1: Create a Pod with image nginx everytime a new custom resource of type website has been created
- feat n.2: When a custom resource has been deleted, remove the linked resources (Pod)
- feat n.3: When a new custom resource has been created or updated, the operator should deploy a deployment manifest and take the number of replicas from the new custom resource
- feat n.4: Show time: 🎉 Create a NodePort service for that deployment and retrieve the public ip address.
- feat n.5: When the pod starts, update the default nginx index.html and put inside a short description from the custom resource

## How to run in local env
If you want to test the operator locally you could easily run it in a local minikube instance.

Build operator image:

    docker build -t javaoperator .

Load the image in your local minikube cluster

    minikube image load javaoperator

Apply it:

    kubectl apply -k kubernetes-charts

## Notes

(*) Reconciles: it stands for the act of the operator to edit
(reconcile) the status of the kubernetes cluster as we want (looking at the custom resource)

## Custom Resource Example
apiVersion: "intre.it/v1"
kind: WebSite
metadata:
    name: demo-site
spec:
    shortDescription: "An amazing website!"
    url: www.demosite.com
    webSiteName: demo-site

https://docs.openshift.com/container-platform/4.11/operators/operator_sdk/java/osdk-java-tutorial.html
https://github.com/kubernetes-client/java/tree/b42fae2b5437836f8c0254d8f19f3a7ba9ebd503/kubernetes/docs
