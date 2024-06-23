# Hack DNS resolver

## Init Project

* Add simple client/server GRPC
* Add client/server monitoring
* Add client/server tracing
* Add thread pool metrics

## Scenario

TBD


## Metrics

http://localhost:3000/d/hack_dns_resolver/hack-dns-resolver?orgId=1


## Run K8s

### Build image

```shell
 mvn spring-boot:build-image
```

### Deploy services

```shell
kubectl apply -f ./deployments/greeting/namespace-greeting.yml
kubectl apply -f ./deployments/greeting/deployments-greeting.yml

kubectl apply -f ./deployments/aggregate/namespace-aggregate.yml
kubectl apply -f ./deployments/aggregate/deployments-aggregate.yml


kubectl apply -f ./deployments/aggregate/network.yml
```

### Port forward

```shell
kubectl -n ndhai-aggregate port-forward deployment/aggregate-service-deployment 8080:8080
```



###  dashboard
```shell
kubectl apply -f ./config/dashboard/dashboard.yml
kubectl proxy
```

create account:

https://github.com/kubernetes/dashboard/blob/master/docs/user/access-control/creating-sample-user.md

```shell
kubectl apply -f ./deployments/dashboard/dashboard-user.yml
kubectl -n kubernetes-dashboard create token admin-user
```

### Install Grafana, Prometheus
https://github.com/nduyhai/kubernetes-prometheus
https://github.com/nduyhai/kubernetes-grafana

### Debug dns
https://kubernetes.io/docs/tasks/administer-cluster/dns-debugging-resolution/

```shell

kubectl apply -f https://k8s.io/examples/admin/dns/dnsutils.yaml
kubectl get pods dnsutils
kubectl exec -i -t dnsutils -- nslookup greeting-service.ndhai-greeting

kubectl exec -i -t dnsutils -- nslookup prometheus-service.monitoring.svc
```

### Load test

```
wrk -t12 -c400 -d30s -s ./config/wrk/post.lua http://127.0.0.1:8080/greeting
```