# Introduction to JUnit Testing

## What is JUnit?

JUnit is a popular open-source testing framework for Java applications. It provides a simple way to write and run repeatable tests, ensuring that your code works as expected. JUnit is widely used in the Java community for unit testing, integration testing, and more.

### Why Testing Matters

Testing is crucial for software development because:
- **Catches Bugs Early**: Identifies issues before they reach production.
- **Improves Code Quality**: Encourages writing modular, maintainable code.
- **Facilitates Refactoring**: Gives confidence when changing code.
- **Documents Behavior**: Tests serve as living documentation of what the code should do.
- **Supports Continuous Integration**: Automated tests run in CI/CD pipelines.

## JUnit 5 Overview

JUnit 5 is the latest version of JUnit, introduced in 2017. It includes several improvements over JUnit 4, such as:
- Modular architecture
- Support for Java 8+ features (lambdas, streams)
- Better extension model
- Improved assertions

### Key Components

1. **JUnit Platform**: Foundation for launching testing frameworks.
2. **JUnit Jupiter**: Programming model for writing tests.
3. **JUnit Vintage**: Compatibility layer for running JUnit 3 and 4 tests.

## Basic Test Structure

A typical JUnit test class looks like this:

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyTestClass {

    @Test
    void testSomething() {
        // Given
        int a = 5;
        int b = 10;

        // When
        int result = a + b;

        // Then
        assertEquals(15, result);
    }
}
```

### Test Lifecycle Annotations

- `@Test`: Marks a method as a test method.
- `@BeforeEach`: Executed before each test method.
- `@AfterEach`: Executed after each test method.
- `@BeforeAll`: Executed once before all tests in the class.
- `@AfterAll`: Executed once after all tests in the class.

## Assertions

Assertions are used to verify expected outcomes. Common assertions include:

- `assertEquals(expected, actual)`: Checks if two values are equal.
- `assertNotEquals(expected, actual)`: Checks if two values are not equal.
- `assertTrue(condition)`: Checks if a condition is true.
- `assertFalse(condition)`: Checks if a condition is false.
- `assertNull(object)`: Checks if an object is null.
- `assertNotNull(object)`: Checks if an object is not null.
- `assertThrows(exceptionClass, executable)`: Checks if a specific exception is thrown.

Example:

```java
@Test
void testDivision() {
    Calculator calc = new Calculator();

    assertEquals(5, calc.divide(10, 2));
    assertThrows(ArithmeticException.class, () -> calc.divide(10, 0));
}
```

## Mocking with Mockito

Mocking is essential for unit testing when you want to isolate the code under test from its dependencies.

Mockito is a popular mocking framework that integrates well with JUnit.

### Basic Mockito Usage

```java
import static org.mockito.Mockito.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;

@ExtendWith(MockitoExtension.class)
class MyServiceTest {

    @Mock
    private MyRepository repository;

    @InjectMocks
    private MyService service;

    @Test
    void testSomeMethod() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(new MyEntity()));

        // When
        MyResult result = service.someMethod(1L);

        // Then
        assertNotNull(result);
        verify(repository).findById(1L);
    }
}
```

Key Mockito methods:
- `mock(Class)`: Creates a mock instance.
- `when(mock.method()).thenReturn(value)`: Stubs method calls.
- `verify(mock).method()`: Verifies method was called.
- `@Mock`: Annotation to create mocks.
- `@InjectMocks`: Injects mocks into the tested object.

## Types of Tests

### Unit Tests

Unit tests focus on testing individual units of code (usually methods or classes) in isolation.

Characteristics:
- Fast to run
- No external dependencies (use mocks)
- Test one thing at a time

### Integration Tests

Integration tests verify that different parts of the system work together correctly.

Characteristics:
- Slower than unit tests
- May involve real databases, external services
- Test interactions between components

### Example from Employee Service

Let's look at examples from our Employee Service tests.

#### Unit Test Example (EmployeeServiceTest)

```java
@Test
void testGetEmployeeById_Success() {
    // Given
    when(repository.findById(1L)).thenReturn(Optional.of(employee));

    // When
    EmployeeDTO result = service.getEmployeeById(1L);

    // Then
    assertNotNull(result);
    assertEquals(dto.getId(), result.getId());
    verify(repository, times(1)).findById(1L);
}
```

This test mocks the repository and focuses only on the service logic.

#### Integration Test Example (EmployeeControllerIntegrationTest)

```java
@Test
void testGetEmployeeById_Success() throws Exception {
    mockMvc.perform(get("/api/employees/{id}", dto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id", is(dto.getId().intValue())))
            .andExpect(jsonPath("$.name", is("John Doe")));
}
```

This test uses MockMvc to test the full HTTP request/response cycle, including controller, service, and repository layers.

## Best Practices

1. **Follow AAA Pattern**: Arrange, Act, Assert.
2. **Test One Thing Per Test**: Each test should have a single responsibility.
3. **Use Descriptive Names**: Test method names should clearly describe what they're testing.
4. **Keep Tests Independent**: Tests should not depend on each other.
5. **Use Mocks for External Dependencies**: Isolate units under test.
6. **Test Edge Cases**: Don't just test happy paths; test boundaries and error conditions.
7. **Maintain Tests**: Update tests when code changes.
8. **Run Tests Frequently**: Use IDE integration or build tools to run tests often.

## Running Tests

### Using Maven

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=EmployeeServiceTest

# Run specific test method
mvn test -Dtest=EmployeeServiceTest#testGetEmployeeById_Success
```

### Using IDE

Most IDEs (IntelliJ IDEA, Eclipse, VS Code) have built-in support for running JUnit tests. Look for "Run Tests" options in the context menu.

## Test Coverage

Test coverage measures how much of your code is exercised by tests. Tools like JaCoCo can generate coverage reports.

Aim for high coverage, but remember: 100% coverage doesn't guarantee bug-free code. Focus on meaningful tests.

## Common Pitfalls

1. **Testing Implementation Details**: Tests should verify behavior, not internal implementation.
2. **Brittle Tests**: Tests that break with every code change.
3. **Slow Tests**: Integration tests that take too long.
4. **Ignoring Test Failures**: Always investigate and fix failing tests.
5. **Not Testing Exceptions**: Remember to test error scenarios.

## Conclusion

JUnit is a powerful tool for ensuring code quality. By writing comprehensive tests, you can catch bugs early, refactor with confidence, and maintain high-quality software. Start with unit tests for core logic, add integration tests for end-to-end scenarios, and follow best practices to keep your test suite maintainable and effective.

Remember, testing is not just about finding bugs—it's about designing better software from the start.
