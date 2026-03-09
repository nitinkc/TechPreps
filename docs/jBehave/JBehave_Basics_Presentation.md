# JBehave Basics & Applied Overview

## Summary
- BDD (Behavior-Driven Development): write executable specifications in human-readable form (Gherkin/JBehave stories) that map to automated step code.
- Goals: improve collaboration across product/QA/engineering, provide living documentation, and enable clear acceptance criteria.
- Use BDD for business-facing behavior, acceptance criteria, or complex workflows that benefit from readable specs.

## Basics: BDD
- Write scenarios in plain language (Given/When/Then) that describe behavior; map examples to step implementations to make them executable.
- Benefits: shared understanding, living documentation, automated acceptance checks.

Notes:
- Keep scenarios business-focused and avoid implementation details.
- Prefer small, focused scenarios that are easy to read and maintain.

## Test Types
- Component: fast, isolated tests (mock external deps).
- Integration: tests across components or services.
- Functional/E2E: full user/business flow validation.
- Regression: broad suites run before releases.
- Smoke: quick checks of critical paths.

Guidance:
- Follow the test pyramid: many component tests, fewer integration, fewest E2E/regression.
- Use story meta tags (e.g., `@testType`) to filter in CI.

## Decision Guide (quick)
- Single class/service -> Component
- Multiple services/DB -> Integration
- Full business flow -> Functional/E2E
- Fast feedback needed -> Component/Smoke
- Long-term replay/regression -> Regression

Example Gherkin:
```gherkin
Feature: Create product recipe
  Scenario: Product manager creates beverage
    Given I am logged in as product manager
    When I create a beverage with ingredients
    Then the recipe should be available for baristas
```

Common run/filter commands:
```bash
mvn test -Dmeta.filter="+testType component"
mvn test -Dmeta.filter="+testType smoke,+testType component"
```

Java step mapping example:
```java
@Given("I am logged in as product manager")
public void loginAsProductManager() {
    // setup user/session for scenario
}
```

## Core Concepts
- Story: text file ("*.story") containing narrative and scenarios.
- Scenario: an example describing behavior.
- Steps: Given (context), When (action), Then (outcome/assertions).
- Examples table: data-driven scenario parameters.
- Meta: tags for filtering/grouping (e.g., `@testType functional`).
- Lifecycle hooks: BeforeStories/AfterStories/BeforeScenario/AfterScenario.

Repository note: stories live under `rtdx-functional-test/src/test/resources/stories/`.

## Story Anatomy (example)
```
Meta:
@component rtdx-product-flow

Narrative: rtdx-product-recipe-service-flow

Scenario: (1) opc create smoke
Meta: @testType functional smoke
Given initialize the test with <lineItemSku> <lineItemQty> <childLineItemSkus> and <childLineItemQtysPerLineItem>
When invoke opc endpoint with <country>
Then validate the response with <responseFile>

Examples:
|testcaseid |country |lineItemSku |lineItemQty |childLineItemSkus |childLineItemQtysPerLineItem |responseFile
|FT-rtdx-product-recipe-01-01 |ca |11168754 |1 |NA |NA |response/opcNonBeverage_20.json
```
Notes:
- Angle-bracket placeholders (<param>) map to Examples columns (case-sensitive).
- Use "NA" for optional/skip values; step code should check for it.

## Step Definitions (Java mapping)
- Use `@Given`, `@When`, `@Then` annotations and `@Named("param")` for binding example values.
Example:
```java
@Given("publish $eventType event to EH using file $xmlfile with $countryCode")
public void publishEvent(@Named("eventType") String eventType, @Named("xmlfile") String xmlFile, @Named("countryCode") String countryCode) { /* ... */ }
```
Notes: keep patterns stable; changing annotation text breaks story-to-code binding.

## Test Data & Utilities
- ThreadLocal `testData` for per-scenario data.
- `payloadGeneratorService` to build payloads from templates and dynamic tokens.
- `convertPayloadToObject` for deserialization.
- `databaseClientService` for DB interactions and cleanup.

## Publishing, Ingestion & Validation
- Publish payloads to EventHub or services using client utilities.
- Prefer polling over fixed sleeps for ingestion completion.
- Validate by querying DB, deserializing domain models, and comparing JSON structures (avoids brittle references).
- Assert counts and deep equality of expected vs actual objects.

## Cleanup Strategy
- Include per-scenario cleanup steps to delete inserted rows.
- Use `@AfterStories` for global teardown (e.g., clearing consumer groups).
- Keep cleanup idempotent.

## Performance & Reliability
- Replace static Thread.sleep calls with polling with timeouts.
- Consider batching inserts where supported.
- Use descriptive assertion messages. Consider soft assertions for multi-check steps.

## Meta Tags & CI
- Use `@testType` for filtering (component/integration/functional/smoke/regression).
- Consider `@priority` and `@duration` for finer control.
- Document tag taxonomy and enforce via linter if helpful.

## Example: BPC Ingestion Scenario (excerpt)
```
Scenario: Check bpc ingestion is inserting valid event data into sbux_bpc and invalid data into consumption_staging
Meta: @testType component regression
Given Dependency publish <dependencyEventType> event using <pdxFile> and publish <eventType> event using <bpcFile> with <countryCodes>
Then wait for processing for <delay> seconds
Then validate <eventType> record in lookUp table using <expectedLookUpRecords>
Then delete the <eventType> staging data
Then cleanup the <eventType> story data using file <expectedLookUpRecords>
```
Notes: consolidate repeated cleanup into lifecycle hooks where safe.

## Common Pitfalls
- Changing step annotation text without updating stories.
- Mismatched example headers vs placeholders.
- Overuse of sleeps leading to slow/flaky tests.
- Skipping cleanup leading to environment contamination.
- Non-deterministic data (timestamps) causing brittle assertions.

## Actionable Improvements
- Replace sleeps with polling loops and configurable timeouts.
- Add BeforeScenario isolation/cleanup hooks.
- Introduce soft-assert aggregation for richer failure reports.
- Add tools to generate Examples rows from CSV templates.

## References
- JBehave: https://jbehave.org/
- BDD concepts: https://cucumber.io/docs/bdd/

---