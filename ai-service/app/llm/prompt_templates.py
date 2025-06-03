# Prompt templates for AI-driven test case and data generation

# feature: testCaseGeneration
TEST_CASE_GENERATOR_FOR_EPIC = (
    "Generate an **ordered list of comprehensive test cases** for the following Epic:\n\n"
    "- **Epics**: {issue_type}s: {keys}\n"
    "- **Stories**: {description_text}\n\n"
    "Each test case must strictly follow the structure below:\n\n"
    "---\n\n"
    "**Test Case <number>: [Title]**\n"
    "1. **Precondition**: [State the required setup before executing the test.]\n"
    "2. **Description**: [Explain what the test is verifying.]\n"
    "3. **Test Steps**:\n"
    "   - [Step 1]\n"
    "   - [Step 2]\n"
    "   - [Step 3]\n"
    "   - ...\n"
    "4. **Expected Results**: [Describe the expected outcome of the test.]\n\n"
    "---\n\n"
    "Requirements:\n"
    "- Use **bold headings** exactly as shown (Precondition, Description, Test Steps, Expected Results).\n"
    "- Use **bullet points** under 'Test Steps'.\n"
    "- Ensure each test case is **clearly titled** and **sequentially ordered**.\n"
    "- Include edge cases and validation scenarios.\n"
    "- The output format must **always match exactly** this structure.\n\n"
    "Please output the test cases now."
)

TEST_CASE_GENERATOR_FOR_STORY = (
    "Generate an **ordered list of comprehensive test cases** for the following Story:\n\n"
    "- **Epics**: {issue_type}s: {keys}\n"
    "- **Stories**: {description_text}\n\n"
    "Each test case must strictly follow the structure below:\n\n"
    "---\n\n"
    "**Test Case <number>: [Title]**\n"
    "1. **Precondition**: [State the required setup before executing the test.]\n"
    "2. **Description**: [Explain what the test is verifying.]\n"
    "3. **Test Steps**:\n"
    "   - [Step 1]\n"
    "   - [Step 2]\n"
    "   - [Step 3]\n"
    "   - ...\n"
    "4. **Expected Results**: [Describe the expected outcome of the test.]\n\n"
    "---\n\n"
    "Requirements:\n"
    "- Use **bold headings** exactly as shown (Precondition, Description, Test Steps, Expected Results).\n"
    "- Use **bullet points** under 'Test Steps'.\n"
    "- Ensure each test case is **clearly titled** and **sequentially ordered**.\n"
    "- Include edge cases and validation scenarios.\n"
    "- The output format must **always match exactly** this structure.\n\n"
    "Please output the test cases now."
)

TEST_CASE_GENERATOR_FOR_BUG = (
    "Generate an **ordered list of comprehensive test cases** for the following Bug:\n\n"
    "- **Epics**: {issue_type}s: {keys}\n"
    "- **Stories**: {description_text}\n\n"
    "Each test case must strictly follow the structure below:\n\n"
    "---\n\n"
    "**Test Case <number>: [Title]**\n"
    "1. **Precondition**: [State the required setup before executing the test.]\n"
    "2. **Description**: [Explain what the test is verifying.]\n"
    "3. **Test Steps**:\n"
    "   - [Step 1]\n"
    "   - [Step 2]\n"
    "   - [Step 3]\n"
    "   - ...\n"
    "4. **Expected Results**: [Describe the expected outcome of the test.]\n\n"
    "---\n\n"
    "Requirements:\n"
    "- Use **bold headings** exactly as shown (Precondition, Description, Test Steps, Expected Results).\n"
    "- Use **bullet points** under 'Test Steps'.\n"
    "- Ensure each test case is **clearly titled** and **sequentially ordered**.\n"
    "- Include edge cases and validation scenarios.\n"
    "- The output format must **always match exactly** this structure.\n\n"
    "Please output the test cases now."
)

# feature: edgeCaseGenerator
EDGE_CASE_PROMPT_FOR_EPIC = (
    "Generate an **ordered list of comprehensive edge case test cases** for the following Epic:\n\n"
    "- Epics: {issue_type}s: {keys}\n"
    "- Stories: {description_text}\n\n"
    "Each test case must strictly follow the format below:\n\n"
    "---\n\n"
    "**Test Case <number>: [Title]**  \n"
    "1. **Precondition**: [State the required setup before executing the test.]  \n"
    "2. **Description**: [Explain what the test is verifying, especially the edge case.]  \n"
    "3. **Test Steps**:\n"
    "   - [Step 1]  \n"
    "   - [Step 2]  \n"
    "   - [Step 3]  \n"
    "4. **Expected Results**: [Describe the expected outcome of the test.]\n\n"
    "---\n\n"
    "**Requirements**:\n"
    "- Test cases must focus on edge scenarios such as:\n"
    "   - Empty datasets\n"
    "   - Single and multiple entries\n"
    "   - Different statuses (active, inactive, archived)\n"
    "   - Special characters and invalid inputs\n"
    "   - Role-based access variations\n"
    "   - Account types and historical data\n"
    "- Use **bold headings** exactly as shown (Precondition, Description, Test Steps, Expected Results).\n"
    "- Ensure each test case title starts with `**Test Case <number>:`.\n"
    "- Use bullet points for steps.\n"
    "- The format must match exactly to support correct markdown rendering and checkbox injection in the frontend."
)

EDGE_CASE_PROMPT_FOR_STORY = (
    "Generate an **ordered list of comprehensive edge case test cases** for the following Story:\n\n"
    "- Epics: {issue_type}s: {keys}\n"
    "- Stories: {description_text}\n\n"
    "Each test case must strictly follow the format below:\n\n"
    "---\n\n"
    "**Test Case <number>: [Title]**  \n"
    "1. **Precondition**: [State the required setup before executing the test.]  \n"
    "2. **Description**: [Explain what the test is verifying, especially the edge case.]  \n"
    "3. **Test Steps**:\n"
    "   - [Step 1]  \n"
    "   - [Step 2]  \n"
    "   - [Step 3]  \n"
    "4. **Expected Results**: [Describe the expected outcome of the test.]\n\n"
    "---\n\n"
    "**Requirements**:\n"
    "- Test cases must focus on edge scenarios such as:\n"
    "   - Empty datasets\n"
    "   - Single and multiple entries\n"
    "   - Different statuses (active, inactive, archived)\n"
    "   - Special characters and invalid inputs\n"
    "   - Role-based access variations\n"
    "   - Account types and historical data\n"
    "- Use **bold headings** exactly as shown (Precondition, Description, Test Steps, Expected Results).\n"
    "- Ensure each test case title starts with `**Test Case <number>:`.\n"
    "- Use bullet points for steps.\n"
    "- The format must match exactly to support correct markdown rendering and checkbox injection in the frontend."
)

EDGE_CASE_PROMPT_FOR_BUG = (
    "Generate an **ordered list of comprehensive edge case test cases** for the following Bug:\n\n"
    "- Epics: {issue_type}s: {keys}\n"
    "- Stories: {description_text}\n\n"
    "Each test case must strictly follow the format below:\n\n"
    "---\n\n"
    "**Test Case <number>: [Title]**  \n"
    "1. **Precondition**: [State the required setup before executing the test.]  \n"
    "2. **Description**: [Explain what the test is verifying, especially the edge case.]  \n"
    "3. **Test Steps**:\n"
    "   - [Step 1]  \n"
    "   - [Step 2]  \n"
    "   - [Step 3]  \n"
    "4. **Expected Results**: [Describe the expected outcome of the test.]\n\n"
    "---\n\n"
    "**Requirements**:\n"
    "- Test cases must focus on edge scenarios such as:\n"
    "   - Empty datasets\n"
    "   - Single and multiple entries\n"
    "   - Different statuses (active, inactive, archived)\n"
    "   - Special characters and invalid inputs\n"
    "   - Role-based access variations\n"
    "   - Account types and historical data\n"
    "- Use **bold headings** exactly as shown (Precondition, Description, Test Steps, Expected Results).\n"
    "- Ensure each test case title starts with `**Test Case <number>:`.\n"
    "- Use bullet points for steps.\n"
    "- The format must match exactly to support correct markdown rendering and checkbox injection in the frontend."
)

# feature: testDataGenerator
TEST_DATA_CREATION_FOR_EPIC = (
    "Generate an **ordered list of test data scenarios** for the following Epic:\n\n"
    "- Epics: {issue_type}s: {keys}  \n"
    "- Stories: {description_text}\n\n"
    "Each test data case must strictly follow the format below:\n\n"
    "---\n\n"
    "**Test Case <number>: [Title for the Data Scenario]**  \n"
    "1. **Precondition**: [Specify any setup or pre-existing data required before this test data is applied.]  \n"
    "2. **Description**: [Describe the data scenario, including whether it is positive, negative, or edge case.]  \n"
    "3. **Test Data Details**:\n"
    "   - [Field 1]: [Value]  \n"
    "   - [Field 2]: [Value]  \n"
    "   - [Field 3]: [Value]  \n"
    "4. **Expected Use Case**: [Explain how this test data will be used and what it aims to validate.]\n\n"
    "---\n\n"
    "**Requirements**:\n"
    "- Include positive, negative, and edge case data scenarios.\n"
    "- Consider variations in:\n"
    "   - User roles and permissions  \n"
    "   - Statuses and workflow transitions  \n"
    "   - Special characters, nulls, and formatting  \n"
    "   - Archived and historical records  \n"
    "   - Error conditions and boundary values\n"
    "- Use **bold headings** (Precondition, Description, Test Data Details, Expected Use Case).\n"
    "- Make sure each title starts with `**Test Case <number>:` to support checkbox rendering in the UI.\n"
    "- Use bullet lists inside `Test Data Details` for clarity."
)

TEST_DATA_CREATION_FOR_STORY = (
    "Generate an **ordered list of test data scenarios** for the following Story:\n\n"
    "- Epics: {issue_type}s: {keys}  \n"
    "- Stories: {description_text}\n\n"
    "Each test data case must strictly follow the format below:\n\n"
    "---\n\n"
    "**Test Case <number>: [Title for the Data Scenario]**  \n"
    "1. **Precondition**: [Specify any setup or pre-existing data required before this test data is applied.]  \n"
    "2. **Description**: [Describe the data scenario, including whether it is positive, negative, or edge case.]  \n"
    "3. **Test Data Details**:\n"
    "   - [Field 1]: [Value]  \n"
    "   - [Field 2]: [Value]  \n"
    "   - [Field 3]: [Value]  \n"
    "4. **Expected Use Case**: [Explain how this test data will be used and what it aims to validate.]\n\n"
    "---\n\n"
    "**Requirements**:\n"
    "- Include positive, negative, and edge case data scenarios.\n"
    "- Consider variations in:\n"
    "   - User roles and permissions  \n"
    "   - Statuses and workflow transitions  \n"
    "   - Special characters, nulls, and formatting  \n"
    "   - Archived and historical records  \n"
    "   - Error conditions and boundary values\n"
    "- Use **bold headings** (Precondition, Description, Test Data Details, Expected Use Case).\n"
    "- Make sure each title starts with `**Test Case <number>:` to support checkbox rendering in the UI.\n"
    "- Use bullet lists inside `Test Data Details` for clarity."
)

TEST_DATA_CREATION_FOR_BUG = (
    "Generate an **ordered list of test data scenarios** for the following Bug:\n\n"
    "- Epics: {issue_type}s: {keys}  \n"
    "- Stories: {description_text}\n\n"
    "Each test data case must strictly follow the format below:\n\n"
    "---\n\n"
    "**Test Case <number>: [Title for the Data Scenario]**  \n"
    "1. **Precondition**: [Specify any setup or pre-existing data required before this test data is applied.]  \n"
    "2. **Description**: [Describe the data scenario, including whether it is positive, negative, or edge case.]  \n"
    "3. **Test Data Details**:\n"
    "   - [Field 1]: [Value]  \n"
    "   - [Field 2]: [Value]  \n"
    "   - [Field 3]: [Value]  \n"
    "4. **Expected Use Case**: [Explain how this test data will be used and what it aims to validate.]\n\n"
    "---\n\n"
    "**Requirements**:\n"
    "- Include positive, negative, and edge case data scenarios.\n"
    "- Consider variations in:\n"
    "   - User roles and permissions  \n"
    "   - Statuses and workflow transitions  \n"
    "   - Special characters, nulls, and formatting  \n"
    "   - Archived and historical records  \n"
    "   - Error conditions and boundary values\n"
    "- Use **bold headings** (Precondition, Description, Test Data Details, Expected Use Case).\n"
    "- Make sure each title starts with `**Test Case <number>:` to support checkbox rendering in the UI.\n"
    "- Use bullet lists inside `Test Data Details` for clarity."
)

# feature: logBug
LOG_A_BUG_FOR_TEST_CASE = (
    "Generate an **ordered list of bugs** based on the following test case:\n\n"
    "- {issue_type}s: {keys}  \n"
    "- Test Cases: {description_text}\n\n"
    "Each bug must follow the structure below:\n\n"
    "---\n\n"
    "**Test Case <number>: [Title of the Bug Related to the Test Case]**  \n"
    "1. **Precondition**: [State any setup required before encountering the bug.]  \n"
    "2. **Test Steps**: [Detailed steps to reproduce the bug.]  \n"
    "3. **Expected Result**: [What was expected to happen.]  \n"
    "4. **Actual Result**: [What actually happened — describe the bug.]  \n"
    "5. **Bug Severity**: [Critical / Major / Minor / Trivial]  \n"
    "6. **Bug Priority**: [High / Medium / Low]  \n"
    "---\n\n"
    "**Instructions**:\n"
    "- Use `**Test Case <number>:` as the heading for each bug report to trigger checkbox wrapping.\n"
    "- Format field titles in **bold** to match frontend rendering logic.\n"
    "- Include comprehensive and realistic bug examples.\n"
    "- Ensure reproducibility by clearly defining the steps and conditions.\n"
    "- Separate each bug report with horizontal rules (`---`) for readability."
)

# feature: testDataValidation
API_TEST_CASE_FOR_EPIC = (
    "Generate an **ordered list of API test cases** for the following Epic:\n\n"
    "- {issue_type}s: {keys}  \n"
    "- Stories: {description_text}\n\n"
    "Each test case should follow this structure:\n\n"
    "---\n\n"
    "**Test Case <number>: [Title of the API Test Case]**  \n"
    "1. **API Endpoint**: [Specify the full endpoint URL or path]  \n"
    "2. **HTTP Method**: [GET, POST, PUT, DELETE, etc.]  \n"
    "3. **Precondition**: [Any setup or context required]  \n"
    "4. **Input Request**: [Request payload or parameters in JSON or form format]  \n"
    "5. **Expected Status Codes**: [200 / 400 / 401 / 500 — explain what each means here]  \n"
    "6. **Expected Response Body**: [Expected fields, types, and structure]  \n"
    "7. **Validation Checks**: [Assertions on response fields, headers, response time, etc.]  \n"
    "8. **Security Testing**: [Authentication checks, token validation, input sanitization]  \n"
    "9. **Load & Performance Testing**: [Concurrency, response time benchmarks, rate limits]  \n"
    "10. **Data Integrity Testing**: [Consistency across requests, edge data scenarios]\n\n"
    "---\n\n"
    "**Instructions**:\n"
    "- Begin each test case with `**Test Case <number>:` to enable checkbox rendering.\n"
    "- Clearly define inputs and expected outputs.\n"
    "- Ensure all API methods and edge cases are covered.\n"
    "- Use **bold** section titles to align with frontend Markdown styling."
)

# feature: DatabaseTestCaseGenerator
DATABASE_TEST_CASE_GENERATOR_FOR_STORY = (
    "Generate an **ordered list of database test cases** for the following:\n\n"
    "- {issue_type}s: {keys}  \n"
    "- User Stories: {description_text}\n\n"
    "Each test case should include the following fields:\n\n"
    "---\n\n"
    "**Test Case <number>: [Title of the Database Test Case]**  \n"
    "1. **Test Case ID**: [Unique identifier for the test case]  \n"
    "2. **Test Scenario**: [Description of what is being tested]  \n"
    "3. **Test Steps**: [Step-by-step actions to execute the test]  \n"
    "4. **Expected Result**: [What the outcome should be]  \n"
    "5. **Actual Result**: [To be filled during test execution]  \n\n"
    "---\n\n"
    "**Ensure test cases cover the following areas**:\n"
    "- Data Integrity (e.g., constraints, normalization)\n"
    "- Data Validation (e.g., type, length, nullability)\n"
    "- Transactions (e.g., commit/rollback behavior)\n"
    "- Performance (e.g., indexing, query execution time)\n"
    "- Security (e.g., access control, SQL injection)\n"
    "- Boundary Conditions (e.g., min/max values, limits)\n"
    "- Data Migration (e.g., legacy to new schema compatibility)\n"
    "- Compatibility (e.g., across environments or DB engines)\n\n"
    "**Instructions**:\n"
    "- Begin each test case with `**Test Case <number>:` to support checkbox rendering.\n"
    "- Clearly define all steps and expected results.\n"
    "- Use **bold** labels for each field to match frontend display formatting."
)

# feature: APITestCaseGenerator

API_TEST_CASE_FOR_STORY = (
    "Generate an **ordered list of API test cases** for the following Story:\n\n"
    "- {issue_type}s: {keys}  \n"
    "- Stories: {description_text}\n\n"
    "Each test case should follow this structure:\n\n"
    "---\n\n"
    "**Test Case <number>: [Title of the API Test Case]**  \n"
    "1. **API Endpoint**: [Specify the full endpoint URL or path]  \n"
    "2. **HTTP Method**: [GET, POST, PUT, DELETE, etc.]  \n"
    "3. **Precondition**: [Any setup or context required]  \n"
    "4. **Input Request**: [Request payload or parameters in JSON or form format]  \n"
    "5. **Expected Status Codes**: [200 / 400 / 401 / 500 — explain what each means here]  \n"
    "6. **Expected Response Body**: [Expected fields, types, and structure]  \n"
    "7. **Validation Checks**: [Assertions on response fields, headers, response time, etc.]  \n"
    "8. **Security Testing**: [Authentication checks, token validation, input sanitization]  \n"
    "9. **Load & Performance Testing**: [Concurrency, response time benchmarks, rate limits]  \n"
    "10. **Data Integrity Testing**: [Consistency across requests, edge data scenarios]\n\n"
    "---\n\n"
    "**Instructions**:\n"
    "- Begin each test case with `**Test Case <number>:` to enable checkbox rendering.\n"
    "- Clearly define inputs and expected outputs.\n"
    "- Ensure all API methods and edge cases are covered.\n"
    "- Use **bold** section titles to align with frontend Markdown styling."
)

API_TEST_CASE_FOR_BUG = (
    "Generate an **ordered list of API test cases** for the following Bug:\n\n"
    "- {issue_type}s: {keys}  \n"
    "- Stories: {description_text}\n\n"
    "Each test case should follow this structure:\n\n"
    "---\n\n"
    "**Test Case <number>: [Title of the API Test Case]**  \n"
    "1. **API Endpoint**: [Specify the full endpoint URL or path]  \n"
    "2. **HTTP Method**: [GET, POST, PUT, DELETE, etc.]  \n"
    "3. **Precondition**: [Any setup or context required]  \n"
    "4. **Input Request**: [Request payload or parameters in JSON or form format]  \n"
    "5. **Expected Status Codes**: [200 / 400 / 401 / 500 — explain what each means here]  \n"
    "6. **Expected Response Body**: [Expected fields, types, and structure]  \n"
    "7. **Validation Checks**: [Assertions on response fields, headers, response time, etc.]  \n"
    "8. **Security Testing**: [Authentication checks, token validation, input sanitization]  \n"
    "9. **Load & Performance Testing**: [Concurrency, response time benchmarks, rate limits]  \n"
    "10. **Data Integrity Testing**: [Consistency across requests, edge data scenarios]\n\n"
    "---\n\n"
    "**Instructions**:\n"
    "- Begin each test case with `**Test Case <number>:` to enable checkbox rendering.\n"
    "- Clearly define inputs and expected outputs.\n"
    "- Ensure all API methods and edge cases are covered.\n"
    "- Use **bold** section titles to align with frontend Markdown styling."
)

# feature:code generator [Automation]
CODE_GENERATOR_FOR_STORY = (
    "As a QA Automation engineer, Can you share all the possible Automation script using robot framework and python for the story and Create sample locators and implementation class with random sample test data: {issue_type}s: {keys}, Stories: {description_text}"
)
# feature:Database Testing [Automation]
DATABASE_TEST_SCRIPT_GENERATOR_FOR_STORY = (
    "I am an Automation QA engineer, and I need to perform Database Testing (including CRUD operations, queries, data integrity, performance) Please make sure the script includes Test Data Preparation, Script, Validations, Performance Testing, Error handling and Logging: {issue_type}s: {keys}. Stories: {description_text}"
)

# feature: API Testing [Automation]
API_TESTING_SCENARIOS = (
    """
I have to test the API with the following details:

- URL: {url}
- HTTP Method: {method}

Please share all the possible scenarios to test this endpoint, considering:
1. **Data Testing**: Validate input boundary conditions, data types, required fields, empty or null values, special characters, and maximum field lengths across all parameters, headers, and body fields.
   - **Query Parameters**: {param_details}
   - **Headers**: {header_details}
2. **Security Testing**:
    - **Authentication and Authorization**: Include cases for missing, invalid, and expired tokens or credentials (if applicable).
    - **Injection Attacks**: SQL Injection, XSS, and other potential vulnerabilities.
    - **Data Sensitivity**: Ensure sensitive data is not exposed in error messages or response bodies.
3. **Functional Testing**:
    - **Positive Cases**: Scenarios that cover expected inputs and responses, ensuring successful requests.
    - **Negative Cases**: Include missing fields, invalid field values, and edge cases for optional fields.
4. **Load and Performance Testing**: Consider scenarios to test the API under load, such as large payloads, high request frequency, and any other performance-sensitive factors.

Return these test scenarios in a format compatible with Postman, including expected input values, response status codes (e.g., 200, 400, 401), and validation checks on the response body fields.

Example Inputs:
- **Query Parameters**: {param_details}
- **Headers**: {header_details}
- **Body**: {body}
"""
)