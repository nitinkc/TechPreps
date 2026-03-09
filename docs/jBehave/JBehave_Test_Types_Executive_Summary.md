# JBehave Test Types - Executive Summary

## Overview
JBehave uses **Meta annotations** to categorize and organize test scenarios. These annotations enable selective test execution based on test type, environment, or risk level. The `@testType` meta tag defines the scope and purpose of each test scenario.

## Test Type Categories

### 1. **Component Tests**
- **Purpose**: Test individual components or modules in isolation
- **Scope**: Single service, class, or bounded context
- **Duration**: Fast (seconds to few minutes)
- **Dependencies**: Minimal external dependencies, often mocked
- **Example**: Testing a single REST API endpoint or business logic unit
- **When to Run**: During development, CI/CD pipeline (every commit)

```gherkin
Meta: @testType component
Scenario: Validate BPC ingestion service processes valid input
Given a valid BPC payload
When the ingestion service processes the request
Then the response should contain success status
```

### 2. **Integration Tests**
- **Purpose**: Test interaction between multiple components/systems
- **Scope**: Multiple services, databases, external APIs
- **Duration**: Medium (minutes to tens of minutes)
- **Dependencies**: Real or containerized external services
- **Example**: Testing end-to-end data flow between services
- **When to Run**: Pre-deployment, scheduled builds

```gherkin
Meta: @testType integration
Scenario: End-to-end product data synchronization
Given product data exists in PIM system
When BPC ingestion triggers PDX processing
Then product data should be available in read service
```

### 3. **Functional Tests**
- **Purpose**: Verify business requirements and user stories
- **Scope**: Feature-level validation from user perspective
- **Duration**: Variable (minutes to hours)
- **Dependencies**: Full application stack
- **Example**: Complete user workflow testing
- **When to Run**: Release validation, acceptance testing

```gherkin
Meta: @testType functional
Scenario: Product manager creates new beverage recipe
Given I am logged in as a product manager
When I create a new beverage with ingredients
Then the recipe should be available for baristas
And inventory should reflect ingredient usage
```

### 4. **Regression Tests**
- **Purpose**: Ensure existing functionality remains intact after changes
- **Scope**: Critical business paths and previously fixed bugs
- **Duration**: Long (hours to days for full suite)
- **Dependencies**: Production-like environment
- **Example**: Re-running all critical scenarios after a major release
- **When to Run**: Before major releases, after critical fixes

```gherkin
Meta: @testType regression
Scenario: Legacy order processing continues to work
Given an existing customer order format
When the system processes the legacy order
Then it should maintain backward compatibility
And produce the same results as before
```

### 5. **Smoke Tests**
- **Purpose**: Quick validation that critical functionality works
- **Scope**: Essential features only (happy path)
- **Duration**: Very fast (minutes)
- **Dependencies**: Production or production-like environment
- **Example**: Can users log in? Are core APIs responding?
- **When to Run**: After deployments, environment health checks

```gherkin
Meta: @testType smoke
Scenario: System health check after deployment
Given the application is deployed
When I check the health endpoints
Then all critical services should be running
And database connections should be active
```

## Test Execution Strategy

### Development Phase
```
Component → Integration → Functional
    ↓           ↓           ↓
  Fast        Medium      Comprehensive
 Feedback    Validation   Requirements
```

### Release Pipeline
```
Smoke → Component → Integration → Regression → Functional
  ↓        ↓           ↓           ↓           ↓
Quick    Unit        Service     Stability   Complete
Check    Level       Level       Check       Validation
```

## Meta Annotation Usage

### Single Test Type
```gherkin
Meta: @testType component
Meta: @priority high
Meta: @team backend
```

### Multiple Test Types
```gherkin
Meta: @testType component regression
Meta: @environment dev staging prod
Meta: @risk high
```

### Running Specific Test Types
```bash
# Run only component tests
mvn test -Dmeta.filter="+testType component"

# Run smoke and component tests
mvn test -Dmeta.filter="+testType smoke,+testType component"

# Exclude regression tests
mvn test -Dmeta.filter="-testType regression"

# Run high priority functional tests
mvn test -Dmeta.filter="+testType functional +priority high"
```

## Best Practices

### 1. **Test Pyramid Adherence**
- **70%** Component Tests (Fast, Many)
- **20%** Integration Tests (Medium, Some)
- **10%** Functional/E2E Tests (Slow, Few)

### 2. **Meta Tag Standards**
```gherkin
Meta: @testType [component|integration|functional|regression|smoke]
Meta: @priority [high|medium|low]
Meta: @environment [dev|staging|prod|all]
Meta: @team [frontend|backend|fullstack]
Meta: @risk [high|medium|low]
Meta: @duration [fast|medium|slow]
```

### 3. **Execution Guidelines**
- **Smoke**: Every deployment (5-10 minutes)
- **Component**: Every commit (10-30 minutes)
- **Integration**: Daily builds (30-60 minutes)
- **Functional**: Release candidates (1-4 hours)
- **Regression**: Major releases (4-24 hours)

## Decision Matrix

| Scenario | Component | Integration | Functional | Regression | Smoke |
|----------|-----------|-------------|------------|------------|--------|
| New feature development | ✅ | ✅ | ✅ | ❌ | ❌ |
| Bug fix | ✅ | ✅ | ❌ | ✅ | ❌ |
| Production deployment | ❌ | ❌ | ✅ | ✅ | ✅ |
| Daily CI/CD | ✅ | ✅ | ❌ | ❌ | ✅ |
| Release validation | ✅ | ✅ | ✅ | ✅ | ✅ |

## Common Anti-Patterns to Avoid

### ❌ Wrong Categorization
```gherkin
# Don't use component for E2E workflows
Meta: @testType component
Scenario: Complete order to delivery process
```

### ❌ Missing Meta Tags
```gherkin
# Always include test type
Scenario: Some test without categorization
```

### ❌ Over-categorization
```gherkin
# Avoid too many categories
Meta: @testType component integration functional regression smoke
```

### ✅ Correct Usage
```gherkin
Meta: @testType component
Meta: @priority high
Meta: @team backend
Scenario: BPC validator rejects invalid payload format
```

## Conclusion

Proper test categorization with JBehave meta annotations enables:
- **Efficient CI/CD pipelines** with appropriate test selection
- **Risk-based testing** by running critical tests first
- **Resource optimization** by avoiding unnecessary test execution
- **Clear ownership** and maintenance responsibilities
- **Faster feedback loops** during development

Choose test types based on **scope**, **speed**, and **reliability requirements** rather than arbitrary naming conventions.
