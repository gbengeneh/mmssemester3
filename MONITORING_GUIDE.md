# Grafana Monitoring Guide

This project uses three layers:

1. **Spring Boot Actuator + Micrometer** creates measurements inside each Java service.
2. **Prometheus** visits each service every 15 seconds and stores those measurements.
3. **Grafana** reads Prometheus data and turns it into dashboards.

The application does not send data directly to Grafana.

```text
Spring services  -- /actuator/prometheus -->  Prometheus  -->  Grafana
```

## Complete setup walkthrough

This section explains how the monitoring was added. Students can follow the same steps when adding monitoring to another Spring Boot project.

### Step 1: Add the Prometheus dependency to Spring Boot

Spring Boot Actuator provides management endpoints such as `health`. Micrometer converts application measurements into a format monitoring systems understand. The Prometheus registry makes those measurements available in Prometheus text format.

Each service needs these two dependencies in its `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
    <scope>runtime</scope>
</dependency>
```

In this project they are included in:

- `api-gateway/pom.xml`
- `user_service/pom.xml`
- `employee_service/pom.xml`
- `eureka_server/pom.xml`
- `config_server/pom.xml`

After changing a Maven file, rebuild the service:

```powershell
.\mvnw.cmd clean package
```

On macOS or Linux, use:

```bash
./mvnw clean package
```

### Step 2: Expose the Prometheus endpoint

Adding the dependency is not enough. Actuator endpoints are hidden unless they are explicitly exposed.

Add the following to each service's `application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,prometheus
  metrics:
    tags:
      application: ${spring.application.name}
    distribution:
      percentiles-histogram:
        http.server.requests: true
```

This configuration:

- exposes `/actuator/health`, `/actuator/info`, and `/actuator/prometheus`;
- adds the Spring application name to every metric;
- creates HTTP duration histogram buckets used to calculate percentile response times.

Start one service and open its metrics endpoint. For example:

```text
http://localhost:8082/actuator/prometheus
```

The response should contain text similar to:

```text
jvm_memory_used_bytes{application="employeeService",area="heap",...}
http_server_requests_seconds_count{application="employeeService",...}
```

This text is intended for Prometheus, not for people to read as a dashboard.

### Step 3: Allow monitoring endpoints through Spring Security

If a project uses Spring Security, Prometheus must be allowed to access its metrics endpoint. Otherwise, the Prometheus Targets page will report `401 Unauthorized` or `403 Forbidden`.

For a Spring MVC service, the authorization rules can include:

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/actuator/**").permitAll()
    .anyRequest().authenticated()
)
```

For a reactive Spring Cloud Gateway:

```java
.authorizeExchange(exchange -> exchange
    .pathMatchers("/actuator/**").permitAll()
    .anyExchange().authenticated()
)
```

The API gateway, employee service, and user service in this project already allow their Actuator routes.

Allowing all Actuator endpoints is convenient for a classroom project. In production, expose only the required endpoints, isolate the management port, or protect metrics with network rules and authentication.

### Step 4: Create the Prometheus configuration

Prometheus works by pulling, or **scraping**, metrics from a list of targets.

The file [`monitoring/prometheus/prometheus.yml`](monitoring/prometheus/prometheus.yml) contains:

```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: spring-boot-services
    metrics_path: /actuator/prometheus
    static_configs:
      - targets:
          - api-gateway:8084
          - user_service:8081
          - employee_service:8082
          - eureka-server:8761
          - config-server:8888
```

Important details:

- `scrape_interval: 15s` tells Prometheus to collect measurements every 15 seconds.
- `job_name` groups these targets under one logical name.
- `metrics_path` is the endpoint exposed by Actuator.
- The target names are Docker Compose service names, not `localhost`.

Inside the Prometheus container, `localhost` means Prometheus itself. Docker Compose provides an internal network and DNS, allowing `prometheus` to resolve names such as `employee_service`.

If an application runs directly on the host instead of inside Docker, the target address must be changed. On Docker Desktop, a host application can normally be addressed from a container using:

```yaml
- targets:
    - host.docker.internal:8082
```

### Step 5: Add Prometheus to Docker Compose

The following service in `docker-compose.yml` starts Prometheus:

```yaml
prometheus:
  image: prom/prometheus:v3.5.0
  ports:
    - "9090:9090"
  volumes:
    - ./monitoring/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml:ro
    - prometheus-data:/prometheus
  depends_on:
    - eureka-server
    - config-server
    - api-gateway
    - user_service
    - employee_service
  restart: unless-stopped
```

The first volume mounts the configuration as read-only. The second stores collected time-series data in a named Docker volume so it survives a normal container restart.

The named volume is declared at the bottom of the Compose file:

```yaml
volumes:
  prometheus-data:
  grafana-data:
```

Start only Prometheus and the application services if you want to test this layer before adding Grafana:

```powershell
docker compose up --build prometheus
```

Because of `depends_on`, Compose also starts the services on which Prometheus depends.

Open http://localhost:9090/targets. Every target should eventually display **UP**.

### Step 6: Add Grafana to Docker Compose

Grafana is the visualization layer. It queries Prometheus; it does not scrape the Java services itself.

The Grafana service is:

```yaml
grafana:
  image: grafana/grafana:12.1.0
  ports:
    - "3000:3000"
  environment:
    GF_SECURITY_ADMIN_USER: admin
    GF_SECURITY_ADMIN_PASSWORD: admin
    GF_USERS_ALLOW_SIGN_UP: "false"
  volumes:
    - grafana-data:/var/lib/grafana
    - ./monitoring/grafana/provisioning:/etc/grafana/provisioning:ro
    - ./monitoring/grafana/dashboards:/var/lib/grafana/dashboards:ro
  depends_on:
    - prometheus
  restart: unless-stopped
```

The volumes serve three different purposes:

- `grafana-data` preserves Grafana's internal database and settings;
- `provisioning` tells Grafana which datasource and dashboard provider to create;
- `dashboards` contains dashboard JSON files loaded by the provider.

### Step 7: Connect Grafana to Prometheus automatically

Grafana calls external metric databases **datasources**.

The file [`monitoring/grafana/provisioning/datasources/prometheus.yml`](monitoring/grafana/provisioning/datasources/prometheus.yml) creates the Prometheus datasource when Grafana starts:

```yaml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    uid: prometheus
    access: proxy
    url: http://prometheus:9090
    isDefault: true
    editable: false
```

The URL uses `prometheus`, the Docker Compose service name. Do not use `http://localhost:9090` here: from inside Grafana, `localhost` refers to the Grafana container.

To confirm the connection:

1. Open http://localhost:3000.
2. Sign in with `admin` / `admin`.
3. Select **Connections** and then **Data sources**.
4. Open **Prometheus**.
5. Scroll down and select **Save & test** if the button is available.

Provisioned datasources may be read-only, which is expected because the file is the source of truth.

### Step 8: Load the prepared dashboard automatically

The dashboard provider is configured in [`monitoring/grafana/provisioning/dashboards/dashboards.yml`](monitoring/grafana/provisioning/dashboards/dashboards.yml):

```yaml
apiVersion: 1

providers:
  - name: Semester 3
    orgId: 1
    folder: Microservices
    type: file
    disableDeletion: true
    updateIntervalSeconds: 30
    options:
      path: /var/lib/grafana/dashboards
```

Grafana checks `/var/lib/grafana/dashboards` every 30 seconds. That container directory is mapped to `monitoring/grafana/dashboards` in this repository.

The provided dashboard is stored in:

```text
monitoring/grafana/dashboards/spring-boot-overview.json
```

After Grafana starts:

1. Select **Dashboards** in the left menu.
2. Open the **Microservices** folder.
3. Open **Spring Boot Microservices Overview**.

### Step 9: Build a Grafana panel manually

Creating one panel manually helps explain how Grafana and Prometheus fit together.

1. In Grafana, select **Dashboards** and **New dashboard**.
2. Select **Add visualization**.
3. Choose the **Prometheus** datasource.
4. Switch the query editor to **Code** mode if necessary.
5. Enter:

   ```promql
   up{job="spring-boot-services"}
   ```

6. Select **Run queries**.
7. Choose the **Stat** visualization.
8. Set the panel title to `Service availability`.
9. Select **Apply**, then save the dashboard.

Each returned series has a value of `1` when a service is reachable and `0` when it is not.

For a request-rate chart, add a time-series panel with:

```promql
sum by (application) (
  rate(http_server_requests_seconds_count[1m])
)
```

For 95th percentile response time, use:

```promql
histogram_quantile(
  0.95,
  sum by (le, application) (
    rate(http_server_requests_seconds_bucket[5m])
  )
)
```

The `rate` function calculates how quickly a counter increases. `sum by (application)` combines matching routes and status codes while retaining one line per service. `histogram_quantile(0.95, ...)` estimates the duration below which 95% of requests completed.

### Step 10: Add another service later

To monitor a new Spring Boot service:

1. Add Actuator and `micrometer-registry-prometheus` to its `pom.xml`.
2. Expose `health`, `info`, and `prometheus` in `application.yml`.
3. Permit the metrics endpoint through Spring Security.
4. Add the service to `docker-compose.yml`.
5. Add `service-name:port` under `targets` in `prometheus.yml`.
6. Restart the affected containers:

   ```powershell
   docker compose up -d --build new-service prometheus
   ```

7. Confirm the new target is **UP** at http://localhost:9090/targets.
8. Query `up{job="spring-boot-services"}` to confirm it appears in Grafana.

## What is included

- Metrics for the API gateway, user service, employee service, Eureka, and Config Server
- Persistent Prometheus and Grafana Docker volumes
- An automatically configured Prometheus datasource
- A ready-to-use **Spring Boot Microservices Overview** dashboard
- Charts for service availability, request rate, JVM heap, and response time

## Start everything

You need Docker Desktop with Docker Compose.

```powershell
docker compose up --build
```

The first build can take several minutes because Maven and Docker download dependencies and images.

| Tool | Address | Sign-in |
|---|---|---|
| Grafana | http://localhost:3000 | `admin` / `admin` |
| Prometheus | http://localhost:9090 | none |
| Eureka | http://localhost:8761 | none |

In Grafana, select **Dashboards**, open the **Microservices** folder, and choose **Spring Boot Microservices Overview**.

The default Grafana password is suitable only for a classroom computer. Change `GF_SECURITY_ADMIN_PASSWORD` in `docker-compose.yml` before deploying anywhere shared.

## Verify each layer

### 1. Check that a service exports metrics

Open http://localhost:8082/actuator/prometheus. A successful response is plain text containing names such as `jvm_memory_used_bytes`.

### 2. Check that Prometheus can reach the services

Open http://localhost:9090/targets. Each endpoint should show **UP**.

You can also enter this query in Prometheus:

```promql
up{job="spring-boot-services"}
```

`1` means Prometheus reached the service; `0` means the scrape failed.

### 3. Generate traffic

Charts can be empty immediately after startup. Visit an API endpoint several times, wait 15–30 seconds, and refresh Grafana. JVM charts appear without traffic, while HTTP charts require requests.

## Reading the dashboard

- **Services UP**: number of services Prometheus can currently scrape. The expected value is 5.
- **HTTP requests per second**: recent workload handled by each service.
- **JVM heap used**: Java memory currently in use. Growth is normal; continual growth without falling may need investigation.
- **95th percentile response time**: 95% of measured requests were at or below this duration. It is more useful than an average for spotting slow requests.

## Useful PromQL exercises

PromQL is the Prometheus Query Language. Try these in Prometheus or Grafana Explore:

```promql
# Is every service reachable?
up{job="spring-boot-services"}

# Java processes' CPU usage
process_cpu_usage

# Request rate grouped by service and status
sum by (application, status) (rate(http_server_requests_seconds_count[5m]))

# Number of live JVM threads
jvm_threads_live_threads
```

## Troubleshooting

**A target is DOWN:** On the Prometheus Targets page, read the error beside that target. Confirm the corresponding container is running with `docker compose ps`, then inspect it with `docker compose logs <service-name>`.

**Grafana has no datasource:** Restart Grafana with `docker compose restart grafana`. The datasource file is mounted from `monitoring/grafana/provisioning/datasources`.

**HTTP charts say "No data":** Make requests to the service and wait for two scrape intervals. Some services may not create HTTP histogram buckets until they have handled traffic.

**Port already in use:** Stop the program using port `3000` or `9090`, or change the host side of the mapping in `docker-compose.yml`, for example `"3001:3000"`.

**Reset monitoring data:** `docker compose down` keeps dashboards and collected data. `docker compose down -v` also deletes the Prometheus and Grafana volumes and cannot be undone.

## Stop the project

```powershell
docker compose down
```

The monitoring data remains in Docker volumes for the next start.
