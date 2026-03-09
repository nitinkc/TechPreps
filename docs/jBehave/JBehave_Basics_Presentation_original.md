````markdown
// filepath: /Users/PSP1000909/ClonedCode/Tech/docs/jBehave/JBehave_Basics_Presentation.md
# JBehave Basics & Applied Overview

# Summary
- BDD (Behavior-Driven Development): write executable specifications in human-readable form (Gherkin/JBehave stories) that map directly to automated step code.
- Goals: improve collaboration across product/QA/engineering, provide living documentation, and enable clear acceptance criteria.
- When to use BDD: for business-facing behavior, acceptance criteria, or complex workflows that benefit from readable specs and stakeholder buy-in.

# Basics: BDD
- What is BDD? Write examples in plain language (Given/When/Then) that describe behavior; map examples to automated step implementations so the spec is executable.
- Key benefits: shared understanding, living documentation, acceptance criteria automated as tests.

Notes:
- Keep scenarios business-focused and avoid implementation detail in the story text.
- Prefer small, focused scenarios that are easy to read and maintain.

# Test Types
- Component: fast, isolated tests for a single service/module (mock external dependencies). Good for developer feedback.
- Integration: tests across multiple components or services (real or containerized dependencies). Good for validating interactions.
- Functional / E2E: end-to-end business flow validation in a full stack environment. Good for acceptance testing.
- Regression: broad suites verifying previously working features remain stable after changes. Run before major releases.
- Smoke: small, fast checks of critical paths after deployment.

Guidance:
- Align frequency with the test pyramid: many component, fewer integration, fewest functional/regression.
- Use `@testType` meta tags so CI can select appropriate subsets.

# Decision Guide & Examples
Quick checklist to pick a test type:
- Scope = single class / service -> Component
- Scope = couple services / DB interactions -> Integration
- Scope = full user/business flow -> Functional / E2E
- Need very fast feedback -> Component/Smoke
- Need long-term replay/validation -> Regression

Decision flow (short):
1. Who cares about the result? (Dev -> Component; Product/QA -> Functional)
2. Does the scenario require external systems? (Yes -> Integration; No -> Component)
3. Is it a critical path for production? (Yes -> include in Smoke/Regression)

Example Gherkin (concise):
```gherkin
Feature: Create product recipe
  Scenario: Product manager creates beverage
    Given I am logged in as product manager
    When I create a beverage with ingredients
    Then the recipe should be available for baristas
```

Common run/filter commands:
```bash
# run component tests only
mvn test -Dmeta.filter="+testType component"
# run smoke + component
mvn test -Dmeta.filter="+testType smoke,+testType component"
```

Java step mapping (example):
```java
@Given("I am logged in as product manager")
public void loginAsProductManager() {
    // setup user/session for scenario
}
```

Notes:
- Tag with `@testType` to enable selective CI execution.
- If you want further granularity, add `@priority` and `@duration` meta tags.

# 1. What is JBehave?
Behavior-Driven Development (BDD) framework for Java.
Defines executable specifications using human-readable stories (plain text).
Maps natural language steps (Given/When/Then) to Java methods via annotations.
Facilitates collaboration between product, QA, and engineering.

Notes:
Emphasize shared language, traceability from requirements to automated tests.

# 2. Core Concepts
Story: A text file ("*.story") containing narrative + scenarios.
Scenario: A concrete example describing behavior.
Steps: Given (context), When (action), Then (outcome/assertions).
Examples Table: Data-driven scenario parameters.
Meta: Tags/metadata controlling filtering/grouping (e.g., @testType functional).
Lifecycle: Hooks (BeforeStories/AfterStories/BeforeScenario/AfterScenario) for setup/teardown.

Notes:
In our repo, stories are under `rtdx-functional-test/src/test/resources/stories/`.

# 3. Story File Anatomy (From FT-recipe-service.story)
```
Meta:
@component rtdx-product-flow

Narrative:rtdx-product-recipe-service-flow functional test

Scenario: (1) opc create smoke
Meta: @testType functional smoke
Given initialize the test with <lineItemSku> <lineItemQty> <childLineItemSkus> and <childLineItemQtysPerLineItem>
When invoke opc endpoint with <country>
Then validate the response with <responseFile>

Examples:
|testcaseid |country |lineItemSku |lineItemQty |childLineItemSkus |childLineItemQtysPerLineItem |responseFile
|FT-rtdx-product-recipe-01-01 |ca |11168754 |1 |NA |NA |response/opcNonBeverage_20.json
```
Key elements: Meta tags, Scenario title, Steps with angle-bracket parameters, Examples table for data rows.

Notes:
Angle brackets map to column headers.

# 4. Parameterization Mechanics
Angle bracket placeholders (<param>) replaced per Examples row.
Column headers must match placeholders exactly (case-sensitive).
Each row = one executed scenario iteration.
Special values like "NA" used to skip optional logic paths.

Notes:
Consistent naming improves maintainability.

# 5. Step Definitions (Java Mapping)
Annotations: `@Given`, `@When`, `@Then` (plus `@AfterStories`).
Pattern matching of the textual step to method signature.
`@Named("param")` binds example table value to method parameter.
Supports multiple annotated methods with similar prefixes (overloads by pattern).

Example:
```java
@Given("publish $eventType event to EH using file $xmlfile with $countryCode")
public void setTestDataAndPublishEventPayload(
    @Named("eventType") String eventType,
    @Named("xmlfile") String xmlFile,
    @Named("countryCode") String countryCode) { /* implementation omitted */ }
```

Notes:
Dollar-sign variables ($var) inside annotation pattern become named parameters.

# 6. Pattern Styles
Forms used in project:
1. Direct variable substitution: `publish $eventType event...`
2. Mixed text & params: `staging records from $stagingInputFile ...`
3. No explicit @Named when positional mapping suffices (rare; we use @Named for clarity).
Use commas in parameters to handle multi-file inputs (split logic internally).

Notes:
Keep patterns stable; changing them breaks story-to-code binding.

# 7. Test Data Strategy
ThreadLocal `testData` holds a `Map<String, Supplier<String>>` across step executions in one scenario.
Data generated once per scenario: `payloadGeneratorService.generateProductDataMap(countryCode)`.
Re-used for subsequent Given steps (e.g., chains of publishes).
Ensures correlated IDs (SKU, correlationId) remain consistent.

Notes:
ThreadLocal isolates parallel scenario runs.

# 8. Publishing & Event Flow
Given steps produce payloads from XML templates (may contain tokens substituted by suppliers).
`payloadGeneratorService.generatePayloadFromXml(file, map)` builds final event JSON/XML.
`resourceClientService.publishPayloadToEventhub(eventType, payload, map)` sends to Event Hub.
Sleep intervals (`Thread.sleep`) allow downstream ingestion before validations.

Notes:
Potential optimization: replace static sleeps with polling.

# 9. Staging Record Handling
Some scenarios pre-insert staging rows before publishing events.
Method `handleStagingRecords(eventType, stagingInputTranslation, map)` parses JSON to domain objects and inserts into Cassandra via `databaseClientService`.
Conditional logic: if status == PERSISTED then also insert final product recipe record.

Notes:
Separation of staging vs final tables allows diff validation.

# 10. Validation Patterns
Then steps query Cassandra for inserted data.
Use domain model deserialization (`convertPayloadToObject`) + deep structural equality via Jackson serialization.
Count assertions ensure expected number of rows.
Diff validations compare arrays of `DataStagingDifferences` objects.
Special case validations (HTTP response codes) via `resourceClientService.invokeEphProductService`.

Notes:
Serializing to JSON strings avoids brittle object reference comparisons.

# 11. Example: Response Validation Step
Story step:
`Then validate read service 200 after ingestion`
Mapped method asserts status code, body existence, presence of mandatory fields, null / non-null conditions based on expected code.

Notes:
Scenario rows provide expectedResponseCode parameter.

# 12. Cleanup Strategy
Per-scenario cleanup steps delete inserted rows based on event type.
`@Then("cleanup the $eventType story data using file $xmlFile")` replays expected payload for accurate key extraction.
`@AfterStories` clears Kafka consumer groups to avoid state leakage.

Notes:
Idempotent cleanup prevents test data accumulation.

# 13. Meta Tags & Filtering
Meta lines: `Meta: @testType functional smoke`.
Allows selecting subsets (e.g., smoke vs regression) via JBehave configuration (`storyFilter` or meta filters in runner).
Other tag: `@component rtdx-product-flow` for grouping.

Notes:
Use consistent tag taxonomy for CI pipelines.

# 14. Handling Optional / NA Values
Convention: parameter value "NA" triggers early return or skip logic (e.g., skip publishing or validation).
Simplifies examples table by keeping a single scenario structure.
Ensure step code checks `"NA".equalsIgnoreCase(value)` before actions.

Notes:
Document NA semantics for new contributors.

# 15. Error Handling & Assertions
JUnit Jupiter assertions: `assertEquals`, `assertTrue`, `assertFalse`, `assertNotNull`.
Descriptive messages for mismatch contexts (sku, country, status).
Minimal custom exception usage; rely on assertion failures to mark scenario fail.

Notes:
Consider adding soft assertions if multiple validations per step become critical.

# 16. Data Modeling
Domain classes: `productDetail`, `componentConsumption`, `baseProductConsumption`, etc.
Mapper choices: Jackson `ObjectMapper` for JSON, `XmlMapper` for XML.
Use of `TypeReference<>` for generic collections (e.g., `new TypeReference<List<ConsumptionStagingRecord>>() {}`).

Notes:
Maintain version stability in model classes to avoid test breakage.

# 17. Reusable Utilities
`convertPayloadToObject` centralizes serialization logic.
`payloadGeneratorService` handles token replacement and dynamic test data injection.
`databaseClientService` encapsulates persistence layer queries/deletes.

Notes:
Keeps step definitions thin and focused on orchestration.

# 18. Performance Considerations
Current design uses fixed sleeps (5s / 10s). Could extend with polling loops checking ingestion completion.
Bulk inserts done sequentially; potential for batching if supported by service.

Notes:
Optimize only after stability; measure flakiness first.

# 19. Best Practices Summary
Keep story language consistent & business-oriented.
Name example columns clearly; avoid ambiguous abbreviations.
Limit one behavior focus per scenario section; use separate Scenarios for variants.
Centralize data generation; avoid duplication inside steps.
Ensure cleanup is always reachable (even after failure) via lifecycle hooks.
Tag stories for selective execution in CI.

Notes:
Review step patterns quarterly for drift.

# 20. Common Pitfalls
Changing annotation text breaks story binding.
Mismatched example headers vs placeholder names -> missing parameter injection.
Overuse of sleeps leads to long test cycles.
Skipping cleanup causes environment contamination.
Non-deterministic data (timestamps) must be normalized or offset-managed.

Notes:
Add validation to detect orphaned test data.

# 21. Extending the Suite
Add new story: place under `stories/`, include Meta tags.
Create/extend step method with precise pattern.
Add domain serialization if new payload types introduced.
Add targeted cleanup logic for new tables.
Update CI meta filters if introducing new tags.

Notes:
Prefer incremental additions; keep stories small (< ~200 lines).

# 22. Running Stories (Conceptual)
Configure JBehave runner (JUnit or Spring) specifying story paths & meta filters.
Use `mvn test -Dmeta.filter='+functional -smoke'` style (example; adapt to actual runner config).
Integrate with pipeline jobs for smoke vs full regression.

Notes:
Check actual project runner config for exact filter syntax.

# 23. Traceability Matrix (Illustrative)
Story scenario IDs (e.g., `FT-rtdx-product-recipe-flow-BR004-03-ExtraPSI`) map to business rule IDs (BR004) enabling trace linking.
Examples table row IDs become report entries.

Notes:
Consider exporting results to dashboard keyed by testcaseid.

# 24. Minor Details & Nuances
Use of comma-separated files/IDs inside single parameter (split within step).
Differentiating staging vs final tables by status (PERSISTED triggers second insert).
ThreadLocal cleared explicitly in cleanup to avoid memory retention.
Diff JSON validation uses array length + deep object equality.
Special handling for unknown sellable IDs (set to "UNKNOWN" before validation).

Notes:
Document special case transformations.

# 25. Actionable Improvements (Next Steps)
Replace static sleep with polling (max timeout) for faster suites.
Add BeforeScenario hook for automatic staging cleanup / isolation.
Introduce soft assertion aggregator for richer failure reports.
Add meta-driven dynamic timeouts (e.g., @slow).
Add helper to auto-generate example rows from CSV.

Notes:
Prioritize based on test runtime & flakiness metrics.

# 26. Glossary
EventHub: Messaging system receiving ingestion events.
Staging Table: Intermediate persistence for validation & diff tracking.
Correlation ID: Unique identifier tying events & audit records.
Meta Tag: Story-level label for filtering.
Diff JSON: Structured difference representation for staging vs expected.

Notes:
Include in onboarding docs.

# 27. Quick Checklist for Adding a Scenario
1. Identify business rule / behavior.
2. Draft Given/When/Then in business language.
3. Define required parameters & columns.
4. Add row(s) to Examples ensuring data variety.
5. Confirm step definitions match pattern.
6. Add validation & cleanup steps.
7. Tag with appropriate Meta (e.g., @testType functional).

Notes:
Perform dry run locally before PR.

# 28. Summary
JBehave enables readable, executable specs.
Project leverages strong data-driven patterns and dynamic payload generation.
Consistent structure (Meta, Scenario, Examples) accelerates expansion.
Robust validations compare serialized domain objects for accuracy.
Opportunities exist to optimize runtime & resilience.

Notes:
End with Q&A.

# 29. References & Further Reading
JBehave Docs: https://jbehave.org/
BDD Intro: https://cucumber.io/docs/bdd/ (conceptual overlap)
Project paths: `src/test/resources/stories/`, `.../steps/ProductJbehaveSteps.jav`

Notes:
Encourage contributors to read official docs for advanced features (composite steps, parameter converters).

# 30. Appendix: Code Snippets
Parameter binding:
```java
@Then("validate read service $expectedResponseCode after ingestion")
public void validateReadServices(@Named("expectedResponseCode") String expectedResponseCode) { /* implementation omitted */ }
```
Staging diff validation:
```java
private void validateDiffJson(String expectedFileName, String actualDiffJson) { /* compare arrays */ }
```
Cleanup:
```java
@AfterStories
public void cleanupResource() { /* delete consumer groups */ }
```

Notes:
Keep snippets minimal; full file exists in repo.

# 31. Story Anatomy: BPC Ingestion Example
Extract from `RT-rtdx-product-bpc-ingestion.story`:
```
Scenario: Check bpc ingestion is inserting valid event data into sbux_bpc look-up table and invalid data into consumption_staging table
Meta: @testType component regression
Given Dependency publish <dependencyEventType> event using <pdxFile> file and publish <eventType> event to EH using file <bpcFile> with <countryCodes>
Then wait for the processing for <delay> seconds
Then validate <eventType> record in lookUp table using <expectedLookUpRecords> file
Then verify ingestion event with <logStatus>
Then delete the <eventType> data inserted in the staging table
Then cleanup the <eventType> story data using file <expectedLookUpRecords>
And cleanup the <eventType> staging data
```
Highlights:
- Mixed Given with dependency + main publish sequence.
- Explicit wait step parameterized (<delay> seconds) – candidate for polling enhancement.
- Multiple cleanup & validation Thens (sequential outcomes vs single Then + And).
- Meta combines multiple tags (@testType component regression) enabling dual classification.

Notes:
Consider consolidating sequential cleanup steps into lifecycle hooks to reduce repetition.

# 32. Test Types Overview (Meta Tag Strategy)
Categories used (from executive summary):
- component: isolated service or module verification.
- integration: multi-service / external system interaction.
- functional: business rule & user-facing behavior.
- regression: broad re-run for stability assurance.
- smoke: rapid critical-path health check.
Guidelines:
- Prefer single primary @testType; add secondary only when justified (e.g., component regression for a critical component scenario included in regression pack).
- Align execution frequency with pyramid (many component, few functional/regression).

Notes:
Maintain a taxonomy doc; avoid tag proliferation (keeps filtering predictable).

# 33. Advanced Meta Filtering & CI Usage
Sample Maven invocations (adjust to actual runner config):
```
# Only component tests
mvn test -Dmeta.filter="+testType component"
# Smoke OR component
mvn test -Dmeta.filter="+testType smoke,+testType component"
# Exclude regression
mvn test -Dmeta.filter="-testType regression"
# High-priority functional tests
mvn test -Dmeta.filter="+testType functional +priority high"
```
Recommendations:
- Add @priority and @risk meta to enable risk-based subsets.
- Introduce @duration fast|medium|slow to allow time-boxed runs.
- Enforce a linter that flags scenarios missing @testType.

Notes:
Meta filters are additive/subtractive; clearly document precedence in CONTRIBUTING.md.
````
