# Kubernetes Deployments TODO - UPDATED ✅

**Namespace:** semester4app (all resources updated)

**All manifests in k8s/ (employee_service uses H2 in-memory DB, no external deps):**
- namespace.yaml
- configmap.yaml
- postgres-statefulset.yaml / postgres-service.yaml / postgres-secret.yaml (for user_service only)
- user-service-deployment.yaml / user-service-service.yaml (port 8081, postgres)
- employee-service-deployment.yaml / employee-service-service.yaml (port 8082, H2)
- api-gateway-deployment.yaml / api-gateway-service.yaml (LoadBalancer port 8084)

**Complete Build & Deploy Steps:**
1. Build images locally (from project root):
```
docker build -t localhost:32000/api-gateway:latest ./api-gateway
docker build -t localhost:32000/employee-service:latest ./employee_service  
docker build -t localhost:32000/user-service:latest ./user_service
```
(Use kind load if local cluster, or your registry)

2. Update image fields in deployments to match (e.g. yourregistry/api-gateway:latest)

3. Deploy (in order):
```
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/configmap.yaml
kubectl apply -f k8s/postgres-secret.yaml 
kubectl apply -f k8s/postgres-statefulset.yaml
kubectl apply -f k8s/postgres-service.yaml
kubectl apply -f k8s/employee-service-deployment.yaml k8s/employee-service-service.yaml
kubectl apply -f k8s/user-service-deployment.yaml k8s/user-service-service.yaml
kubectl apply -f k8s/api-gateway-deployment.yaml k8s/api-gateway-service.yaml
```

4. Monitor:
```
kubectl get all -n semester4app
kubectl get ingress -n semester4app  # if added
kubectl logs -f -n semester4app deployment/api-gateway
```

**Notes:** 
- postgres-secret password base64('123'). Recreate if changed.
- Eureka/Config URLs point to future services.
- Probes assume actuator enabled.
- Scale replicas/resources as needed.

