# Microservices Startup Checklist

## Pre-Startup Verification
- [ ] Java 17+ is installed and configured
- [ ] Maven is installed and in PATH
- [ ] PostgreSQL is running (required for user_service)
- [ ] Git access to config repo: https://github.com/gbengeneh/semester3-config-repo.git
- [ ] All dependencies added to pom.xml files

## Dependencies Added ✅
- [x] user_service: spring-cloud-starter-config
- [x] employee_service: spring-cloud-starter-config
- [x] api-gateway: spring-cloud-starter-config

## Startup Sequence

### Step 1: Eureka Server (Service Discovery)
- [ ] Navigate to eureka_server directory
- [ ] Run: `mvnw spring-boot:run`
- [ ] Verify: http://localhost:8761 shows Eureka Dashboard
- [ ] Status: ⏳ Pending

### Step 2: Config Server (Configuration Management)
- [ ] Navigate to config_server directory
- [ ] Run: `mvnw spring-boot:run`
- [ ] Verify: http://localhost:8888/actuator/health returns UP
- [ ] Verify: Logs show successful Git repo clone
- [ ] Status: ⏳ Pending

### Step 3: User Service (Authentication & User Management)
- [ ] Navigate to user_service directory
- [ ] Run: `mvnw spring-boot:run`
- [ ] Verify: Service appears in Eureka Dashboard as "USER_SERVICE"
- [ ] Verify: Logs show successful config fetch from Config Server
- [ ] Verify: PostgreSQL connection successful
- [ ] Status: ⏳ Pending

### Step 4: Employee Service (Employee Management)
- [ ] Navigate to employee_service directory
- [ ] Run: `mvnw spring-boot:run`
- [ ] Verify: Service appears in Eureka Dashboard as "EMPLOYEESERVICE"
- [ ] Verify: Logs show successful config fetch from Config Server
- [ ] Verify: H2 database initialized
- [ ] Status: ⏳ Pending

### Step 5: API Gateway (API Routing & Security)
- [ ] Navigate to api-gateway directory
- [ ] Run: `mvnw spring-boot:run`
- [ ] Verify: Service appears in Eureka Dashboard as "API-GATEWAY"
- [ ] Verify: Logs show successful config fetch from Config Server
- [ ] Verify: Gateway discovers all registered services
- [ ] Status: ⏳ Pending

## Post-Startup Verification
- [ ] All 5 services visible in Eureka Dashboard (http://localhost:8761)
- [ ] No error logs in any service console
- [ ] Config Server successfully serving configurations
- [ ] All services registered with Eureka
- [ ] API Gateway can route to backend services

## Service Status Overview

| Service          | Port | Status | Registered in Eureka | Config Loaded |
|------------------|------|--------|---------------------|---------------|
| Eureka Server    | 8761 | ⏳     | N/A                 | N/A           |
| Config Server    | 8888 | ⏳     | ❌                  | N/A           |
| User Service     | 8081 | ⏳     | ⏳                  | ⏳            |
| Employee Service | 8082 | ⏳     | ⏳                  | ⏳            |
| API Gateway      | TBD  | ⏳     | ⏳                  | ⏳            |

Legend:
- ✅ Complete
- ⏳ Pending
- ❌ Not Required
- ⚠️ Issue/Warning

## Known Issues & Resolutions

### Issue Log
- [ ] No issues reported yet

### If Services Fail to Start:
1. Check if ports are already in use
2. Verify PostgreSQL is running (for user_service)
3. Check Config Server can access Git repository
4. Ensure Eureka Server started first
5. Review application logs for specific errors

## Alternative Startup Methods

### Option 1: Manual Startup (Recommended for first time)
Follow STARTUP_GUIDE.md step by step

### Option 2: Automated Startup
Run: `start-all-services.bat`

## Notes
- Config Server must be running before other services (except Eureka)
- User Service requires PostgreSQL database
- Employee Service uses H2 in-memory database
- Wait for each service to fully start before starting the next one
- Check Eureka Dashboard to confirm service registration

## Completion Criteria
- [ ] All services running without errors
- [ ] All services registered in Eureka
- [ ] All services loaded configuration from Config Server
- [ ] API Gateway can route requests to backend services
- [ ] No error logs in any service
