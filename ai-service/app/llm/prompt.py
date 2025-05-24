# Prompt templates for text-based test case generation

# feature: testCaseGenerationWithText
TEST_CASE_GENERATOR_WITH_TEXT = (
    "As a QA Automation engineer, I need to write Cypress.io tests for frontend web applications. "
    "Given the story and its acceptance criteria below, generate test automation code that: "
    "1. Uses **Cypress.io** with JavaScript. "
    "2. Follows the **Page Object Model (POM)** design pattern. "
    "3. Uses **camelCase** for all function names and variables. "
    "4. Includes **multiple test cases** that reflect the acceptance criteria. "
    "5. Uses **best practices** for Cypress tests: "
    "- Use `beforeEach()` for setup where appropriate. "
    "- Avoid hardcoded waits (use `cy.intercept`, `cy.wait('@alias')`, etc. where needed). "
    "- Select elements using `data-automation-id`, `aria-label`, or stable semantic selectors. "
    "- Keep test logic separated from page logic. "
    "- Write modular and reusable code. "
    "Story Details: {user_text}"
)
