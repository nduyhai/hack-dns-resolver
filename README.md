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
kubectl apply -f ./deployments/aggregate/namespace-aggregate.yml
kubectl apply -f ./deployments/aggregate/deployments-aggregate.yml

kubectl apply -f ./deployments/greeting/namespace-greeting.yml
kubectl apply -f ./deployments/greeting/deployments-greeting.yml
```

### Port forward

kubectl -n ndhai-aggregate port-forward svc/aggregate-service 8080:8080


###  dashboard
```shell
kubectl apply -f ./config/dashboard/dashboard.yml
kubectl proxy
```

create account:
```shell
kubectl apply -f ./config/dashboard/dashboard-user.yml
kubectl -n kubernetes-dashboard create token admin-user
```