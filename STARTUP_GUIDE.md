# Microservices Startup Guide

## Prerequisites
- Java 17+ installed
- Maven installed
- PostgreSQL running (for user_service)
- Git access to config repository: https://github.com/gbengeneh/semester3-config-repo.git

## Architecture Overview
```
┌─────────────────┐
│  Eureka Server  │ (Port 8761) - Service Discovery
└────────┬────────┘
         │
    ┌────┴────┬──────────┬──────────────┐
    │         │          │              │
┌───▼────┐ ┌─▼──────┐ ┌─▼──────────┐ ┌─▼──────────┐
│ Config │ │  User  │ │  Employee  │ │    API     │
│ Server │ │Service │ │  Service   │ │  Gateway   │
└────────┘ └────────┘ └────────────┘ └────────────┘
(8888)     (8081)     (8082)         (Dynamic)
```

## Startup Order (CRITICAL - Follow this sequence!)

### Step 1: Start Eureka Server
**Purpose:** Service discovery - all other services register here

```bash
cd eureka_server
mvnw spring-boot:run
```

**Verification:**
- Open browser: http://localhost:8761
- You should see Eureka Dashboard
- Wait until "Instances currently registered with Eureka" section appears

---

### Step 2: Start Config Server
**Purpose:** Centralized configuration management

```bash
cd config_server
mvnw spring-boot:run
```

**Verification:**
- Check console for: "Started ConfigServerApplication"
- Test endpoint: http://localhost:8888/actuator/health
- Should return: `{"status":"UP"}`
- Verify it can access the Git repo (check logs for successful clone)

---

### Step 3: Start User Service
**Purpose:** User authentication and management

```bash
cd user_service
mvnw spring-boot:run
```

**Verification:**
- Check console for: "Started UserServiceApplication"
- Verify in Eureka: http://localhost:8761 - should show "USER_SERVICE"
- Check logs for successful config fetch from Config Server
- Verify PostgreSQL connection is successful

---

### Step 4: Start Employee Service
**Purpose:** Employee management

```bash
cd employee_service
mvnw spring-boot:run
```

**Verification:**
- Check console for: "Started EmployeeServiceApplication"
- Verify in Eureka: http://localhost:8761 - should show "EMPLOYEESERVICE"
- Check logs for successful config fetch from Config Server
- H2 database should initialize automatically

---

### Step 5: Start API Gateway
**Purpose:** Single entry point for all services

```bash
cd api-gateway
mvnw spring-boot:run
```

**Verification:**
- Check console for: "Started ApiGatewayApplication"
- Verify in Eureka: http://localhost:8761 - should show "API-GATEWAY"
- Check logs for successful config fetch from Config Server
- Gateway should discover all registered services

---

## Service Ports Summary

| Service          | Port | Purpose                          |
|------------------|------|----------------------------------|
| Eureka Server    | 8761 | Service Discovery                |
| Config Server    | 8888 | Configuration Management         |
| User Service     | 8081 | Authentication & User Management |
| Employee Service | 8082 | Employee Management              |
| API Gateway      | TBD  | API Routing & Security           |

## Common Issues & Solutions

### Issue 1: Service fails to connect to Config Server
**Solution:** 
- Ensure Config Server is running on port 8888
- Check network connectivity
- Verify Git repository is accessible

### Issue 2: Service fails to register with Eureka
**Solution:**
- Ensure Eureka Server is running on port 8761
- Check `eureka.client.service-url.defaultZone` in application.yml
- Wait 30-60 seconds for registration to complete

### Issue 3: User Service database connection fails
**Solution:**
- Ensure PostgreSQL is running
- Verify database credentials in Config Server repository
- Check database exists and is accessible

### Issue 4: Port already in use
**Solution:**
- Check if service is already running: `netstat -ano | findstr :<PORT>`
- Kill the process or change the port in configuration

## Stopping Services

Stop services in **reverse order**:
1. API Gateway (Ctrl+C)
2. Employee Service (Ctrl+C)
3. User Service (Ctrl+C)
4. Config Server (Ctrl+C)
5. Eureka Server (Ctrl+C)

## Quick Start Script (Windows)

Create `start-all.bat`:
```batch
@echo off
echo Starting Microservices...

start "Eureka Server" cmd /k "cd eureka_server && mvnw spring-boot:run"
timeout /t 30

start "Config Server" cmd /k "cd config_server && mvnw spring-boot:run"
timeout /t 20

start "User Service" cmd /k "cd user_service && mvnw spring-boot:run"
timeout /t 15

start "Employee Service" cmd /k "cd employee_service && mvnw spring-boot:run"
timeout /t 15

start "API Gateway" cmd /k "cd api-gateway && mvnw spring-boot:run"

echo All services started!
```

## Dependencies Added

The following dependency was added to enable Config Server integration:

**user_service/pom.xml:**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

**employee_service/pom.xml:**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

**api-gateway/pom.xml:**
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

## Configuration Files

All services are configured to use Config Server:
- `spring.config.import: optional:configserver:http://localhost:8888`
- Configuration repository: https://github.com/gbengeneh/semester3-config-repo.git

## Next Steps

1. Follow the startup sequence above
2. Verify each service in Eureka Dashboard
3. Test API endpoints through API Gateway
4. Monitor logs for any errors
