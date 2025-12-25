# Contributing to Automation Testing iXigo

Thank you for your interest in contributing to the **Automation Testing iXigo** project! This document provides guidelines and instructions for contributing to the project.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Making Changes](#making-changes)
- [Commit Guidelines](#commit-guidelines)
- [Pull Request Process](#pull-request-process)
- [Testing Guidelines](#testing-guidelines)
- [Coding Standards](#coding-standards)
- [Documentation](#documentation)

## Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Report issues professionally
- Follow all applicable laws and regulations

## Getting Started

### Prerequisites

- Java JDK 11 or higher
- Maven 3.6 or higher
- Git
- Google Chrome browser
- GitHub account

### Fork and Clone

1. **Fork the repository** on GitHub
2. **Clone your fork locally**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/ixigo-automation-testing.git
   cd ixigo-automation-testing
   ```

3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/ORIGINAL_OWNER/ixigo-automation-testing.git
   ```

4. **Verify remotes**:
   ```bash
   git remote -v
   ```

## Development Setup

### 1. Environment Setup

```bash
# Install dependencies
mvn clean install

# Verify setup
mvn clean compile
mvn clean test
```

### 2. IDE Setup

**IntelliJ IDEA**:
- Open project → pom.xml
- Project will auto-configure Maven
- Set JDK to version 11 or higher

**Eclipse**:
- Import → Existing Maven Projects
- Select project root directory
- Eclipse will configure Maven automatically

**VS Code**:
- Install Extension Pack for Java
- Open project folder
- Extensions will auto-configure

### 3. Configuration

Copy and customize configuration:
```bash
cp src/test/resources/config.properties.example src/test/resources/config.properties
```

Edit `config.properties` with your test parameters.

## Making Changes

### Create a Feature Branch

```bash
# Update main branch
git checkout main
git pull upstream main

# Create feature branch
git checkout -b feature/your-feature-name
# or for bug fixes
git checkout -b bugfix/issue-description
# or for documentation
git checkout -b docs/documentation-update
```

### Branch Naming Convention

- **Feature**: `feature/brief-description`
- **Bug Fix**: `bugfix/issue-number-description`
- **Documentation**: `docs/update-type`
- **Refactor**: `refactor/component-name`

### Make Your Changes

1. **Update relevant files** following [Coding Standards](#coding-standards)
2. **Add/update tests** for your changes
3. **Ensure tests pass** locally:
   ```bash
   mvn clean test
   ```

4. **Update documentation** if needed

## Commit Guidelines

### Commit Message Format

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type

- **feat**: A new feature
- **fix**: A bug fix
- **docs**: Documentation only changes
- **style**: Changes that don't affect code meaning (formatting, missing semicolons, etc.)
- **refactor**: Code change that neither fixes a bug nor adds a feature
- **perf**: Code change that improves performance
- **test**: Adding missing tests or correcting existing tests
- **chore**: Changes to build process, dependencies, or other non-code changes
- **ci**: Changes to CI/CD configuration

### Examples

```bash
git commit -m "feat(SearchResults): add price filter validation

- Add new method to filter flights by price range
- Update SearchResultsPage with filter logic
- Add corresponding test case

Closes #123"
```

```bash
git commit -m "fix(FlightsPage): handle stale element in city selection

- Implement retry logic for city input field
- Handle DOM refresh during selection
- Add logging for debugging"
```

### Best Practices

- One logical change per commit
- Write descriptive commit messages
- Reference issues in commits: `Closes #123`
- Keep commits atomic and reversible

## Pull Request Process

### Before Creating PR

1. **Update your branch** with latest main:
   ```bash
   git fetch upstream
   git rebase upstream/main
   ```

2. **Run full test suite**:
   ```bash
   mvn clean test
   ```

3. **Run code quality checks**:
   ```bash
   mvn clean compile
   mvn checkstyle:check  # if configured
   ```

4. **Verify all tests pass** locally

### Create Pull Request

1. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```

2. **Create PR on GitHub**:
   - Use descriptive title: `Add price filter validation to SearchResults page`
   - Reference related issues: `Closes #123`
   - Provide detailed description of changes
   - Include test results/screenshots if applicable

### PR Description Template

```markdown
## Description
Briefly describe what this PR does.

## Changes
- Change 1
- Change 2
- Change 3

## Testing
How was this tested? Include:
- Test cases run
- Steps to reproduce
- Screenshots/evidence

## Issues
Closes #123
Related to #124

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] Tests added/updated
- [ ] All tests passing locally
- [ ] No breaking changes
```

### Review Process

- Maintainers will review your PR
- Respond to feedback and make requested changes
- Push changes to the same branch (PR auto-updates)
- Once approved, maintainer will merge

## Testing Guidelines

### Writing Tests

1. **Follow existing patterns** in test classes
2. **Use descriptive names**:
   ```java
   @Test
   public void shouldSortFlightsPriceInAscendingOrder() {
       // test code
   }
   ```

3. **Test one thing per test**
4. **Use assertions effectively**:
   ```java
   Assert.assertTrue(displayedPrices.size() > 0, "Should have flight results");
   Assert.assertEquals(expected, actual, "Prices should match");
   ```

5. **Add comments** for complex test logic

### Running Tests

```bash
# Run all tests
mvn clean test

# Run specific test class
mvn test -Dtest=FlightSearchTest

# Run specific test method
mvn test -Dtest=FlightSearchTest#endToEndFlightSearch

# Run with coverage report
mvn test jacoco:report
```

### Test Coverage

- Aim for meaningful coverage (80%+)
- Focus on critical paths
- Test edge cases and error conditions
- Include integration tests

## Coding Standards

### Java Code Style

**Naming Conventions**:
```java
// Classes: PascalCase
public class FlightsPage { }

// Methods: camelCase
public void enterFromCity(String city) { }

// Constants: UPPER_SNAKE_CASE
private static final int WAIT_TIMEOUT = 10;

// Variables: camelCase
private int flightPrice;
```

**Formatting**:
- Indent: 4 spaces (no tabs)
- Max line length: 120 characters
- Opening braces on same line: `if (condition) {`
- Closing braces on new line

**Comments**:
```java
/**
 * Selects a city from the autocomplete dropdown.
 * 
 * @param city the city name to select
 * @throws TimeoutException if city not found within timeout
 */
public void selectCity(String city) {
    // Complex logic explanation
}
```

### Page Object Model Standards

```java
public class MyPage extends BasePage {
    
    // Locators at top
    private final By submitButton = By.id("submit");
    private final List<By> cityInputCandidates = Arrays.asList(
        By.id("city-input"),
        By.name("city")
    );
    
    // Constructor
    public MyPage(WebDriver driver) {
        super(driver);
    }
    
    // User-facing methods
    public void enterCity(String city) {
        sendKeys(findElement(cityInputCandidates), city);
    }
    
    public void clickSubmit() {
        safeClick(submitButton);
    }
    
    // Private helper methods
    private void handlePopup() {
        // implementation
    }
}
```

### Error Handling

```java
try {
    // operation
} catch (TimeoutException e) {
    System.out.println("Element not found within timeout: " + e.getMessage());
    throw e;
} catch (StaleElementReferenceException e) {
    System.out.println("Stale element, retrying: " + e.getMessage());
    // retry logic
}
```

## Documentation

### Update README

If adding a feature, update the relevant section in README.md

### Update This File

Add your guidelines to CONTRIBUTING.md if needed

### Code Documentation

- Document public methods and classes
- Explain "why" not just "what"
- Keep documentation in sync with code

### Javadoc Standards

```java
/**
 * Applies the cheapest flight sort filter.
 * 
 * Attempts to find and click the "cheapest" sort button on the results page.
 * If not found directly, opens the sort menu first.
 * 
 * @return true if sort was successfully applied, false otherwise
 * @throws TimeoutException if page elements not found within timeout
 * @see SearchResultsPage#waitForResultsToLoad()
 */
public boolean applyCheapestSort() {
    // implementation
}
```

## Troubleshooting

### Common Issues

**Issue**: Tests fail locally but pass on CI
- Solution: Clear Maven cache: `mvn clean`
- Check Java/Maven versions match CI configuration
- Verify config.properties is set correctly

**Issue**: Merge conflicts
```bash
# Pull latest
git fetch upstream
git rebase upstream/main

# Resolve conflicts in your editor
# Add resolved files
git add .
git rebase --continue
git push -f origin your-branch
```

**Issue**: Need to update fork with latest upstream
```bash
git fetch upstream
git rebase upstream/main
git push -f origin main
```

## Getting Help

- **Issues**: Check existing issues or create a new one
- **Discussions**: Use GitHub Discussions for questions
- **Email**: Contact maintainers if needed
- **Documentation**: Refer to tech stack documentation links

## Recognition

Contributors will be recognized in:
- README.md contributors section
- Release notes for significant contributions
- GitHub contributors page

## License

By contributing, you agree that your contributions will be licensed under the same license as the project.

---

**Thank you for contributing to Automation Testing iXigo!** 🚀
