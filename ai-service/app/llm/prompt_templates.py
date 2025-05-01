# Prompt templates for AI-driven test case and data generation

TEST_CASE_GENERATOR_FOR_EPIC = (
    "Generate ordered list of test cases for the following Epics: {issue_type}s: {keys}, Stories: {description_text}. Ensure each test case includes: Precondition, Description, Test Steps and Expected Results. Please provide comprehensive test cases covering all possible scenarios and edge cases."
)

TEST_CASE_GENERATOR_FOR_STORY = (
    "Generate ordered list of test cases for the following Story: {issue_type}s: {keys}, Stories: {description_text}. Ensure each test case includes: Precondition, Description, Test Steps and Expected Results. Please provide comprehensive test cases covering all possible scenarios and edge cases."
)

TEST_CASE_GENERATOR_FOR_BUG = (
    "Generate ordered list of test cases for the following Bugs: {issue_type}s: {keys}, Stories: {description_text}. Ensure each test case includes: Precondition, Description, Test Steps and Expected Results. Please provide comprehensive test cases covering all possible scenarios and edge cases."
)

EDGE_CASE_PROMPT_FOR_EPIC = (
    "Generate a comprehensive edge case analysis for testing for the following Epics: {issue_type}s: {keys}, including test cases, expected results, test steps, and edge cases for various scenarios, such as empty data sets, single entries, multiple entries, different statuses, special characters, and historical data, considering different user roles, account types, and data variations. Stories: {description_text}."
)

EDGE_CASE_PROMPT_FOR_STORY = (
    "Generate a comprehensive edge case analysis for testing for the following Story: {issue_type}s: {keys}, including test cases, expected results, test steps, and edge cases for various scenarios, such as empty data sets, single entries, multiple entries, different statuses, special characters, and historical data, considering different user roles, account types, and data variations. Stories: {description_text}."
)

EDGE_CASE_PROMPT_FOR_BUG = (
    "Generate a comprehensive edge case analysis for testing for the following Bug: {issue_type}s: {keys}, including test cases, expected results, test steps, and edge cases for various scenarios, such as empty data sets, single entries, multiple entries, different statuses, special characters, and historical data, considering different user roles, account types, and data variations. Stories: {description_text}."
)

TEST_DATA_CREATION_FOR_EPIC = (
    "Create comprehensive test data for testing for the following Epics: {issue_type}s: {keys}, including various scenarios and edge cases, considering user roles and permissions, data variations, statuses and workflows, special characters and formatting, historical and archived data, and error scenarios, and create positive, negative, and edge case test data that is relevant, realistic, and comprehensive, covering all aspects of the feature. Stories: {description_text}."
)

TEST_DATA_CREATION_FOR_STORY = (
    "Create comprehensive test data for testing for the following Story: {issue_type}s: {keys}, including various scenarios and edge cases, considering user roles and permissions, data variations, statuses and workflows, special characters and formatting, historical and archived data, and error scenarios, and create positive, negative, and edge case test data that is relevant, realistic, and comprehensive, covering all aspects of the feature. Stories: {description_text}."
)

TEST_DATA_CREATION_FOR_BUG = (
    "Create comprehensive test data for testing for the following Bug: {issue_type}s: {keys}, including various scenarios and edge cases, considering user roles and permissions, data variations, statuses and workflows, special characters and formatting, historical and archived data, and error scenarios, and create positive, negative, and edge case test data that is relevant, realistic, and comprehensive, covering all aspects of the feature. Stories: {description_text}."
)

API_TEST_CASE_FOR_EPIC = (
    "Generate API testing test cases for the following Epics: {issue_type}s: {keys}, covering data testing, security testing, functional testing, and load/performance testing. Return test cases in Postman format with expected inputs, status codes (200, 400, 401, 500), and validation checks on response body fields, ensuring comprehensive coverage of all scenarios. Stories: {description_text}."
)

API_TEST_CASE_FOR_STORY = (
    "Generate API testing test cases for the following Story: {issue_type}s: {keys}, covering data testing, security testing, functional testing, and load/performance testing. Return test cases in Postman format with expected inputs, status codes (200, 400, 401, 500), and validation checks on response body fields, ensuring comprehensive coverage of all scenarios. Stories: {description_text}."
)

API_TEST_CASE_FOR_BUG = (
    "Generate API testing test cases for the following Bug: {issue_type}s: {keys}, covering data testing, security testing, functional testing, and load/performance testing. Return test cases in Postman format with expected inputs, status codes (200, 400, 401, 500), and validation checks on response body fields, ensuring comprehensive coverage of all scenarios. Stories: {description_text}."
)

CODE_GENERATOR_FOR_STORY = (
    "As a QA Automation engineer, Can you share all the possible Automation script using robot framework and python for the story and Create sample locators and implementation class with random sample test data: {issue_type}s: {keys}, Stories: {description_text}"
)

DATABASE_TEST_CASE_GENERATOR_FOR_STORY = (
    "Generate a detailed list of test cases for database testing, based on the following user stories: {issue_type}s: {keys}, These test cases should cover various aspects of database functionality, including: Data Integrity, Data Validation, Transactions, Performance, Security, Boundary Conditions, Data Migration and Compatibility. Expected Output: Provide a list of test cases formatted as follows: Test Case ID, Test Scenario, Test Steps, Expected Result, Actual Result. User Stories: {description_text}"
)

DATABASE_TEST_SCRIPT_GENERATOR_FOR_STORY = (
    "I am an Automation QA engineer, and I need to perform Database Testing (including CRUD operations, queries, data integrity, performance) Please make sure the script includes Test Data Preparation, Script, Validations, Performance Testing, Error handling and Logging: {issue_type}s: {keys}. Stories: {description_text}"
)

LOG_A_BUG_FOR_TEST_CASE = (
    "Generate bugs based on the following Test Cases: {issue_type}s: {keys}, Test Cases: {description_text}"
)
