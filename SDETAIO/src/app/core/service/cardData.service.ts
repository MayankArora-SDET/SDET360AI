import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root',
})
export class CardDataService {
  getItemsByCategory(type: 'functionalTesting' | 'automationTesting' | 'productFeatures', category: string) {
    let data = []
    switch (type) {
      case 'productFeatures':
        data = this.productFeaturesCardData;
        break;
      case 'automationTesting':
        data = this.automationCategoryCardData;
        break;
      case 'functionalTesting':
        data = this.functionalTestingCategoryData;
        break;
    }


    const categoryData = data.find(item => item.category === category);
    return categoryData ? categoryData.items : [];
  }
  productFeaturesCardData = [
    {
      category: 'Business Requirements',
      items: [
        {
          id: 'businessRequirement',
          label: 'Business Requirements',
          linkTo: '/productFeatures/businessRequirement',
          icon: 'businessRequirement.png',
          content:
            'An eminent feature of SDET360.ai platform is to generate meaningful user stories in defined format out of raw requirements written as transcript or Business Requirement Document (BRD) with ability to integrate and import stories into project management tools with maker checker ability.',
        },
      ],
    },
  ];
  samplePromptList = [
    'Revolutionize QA with AI-powered automation, real-time analytics, and seamless CI/CD integration.',
    'Transform testing with our AI-driven platform, ensuring precision, speed, and scalability.',
    'Streamline QA with intelligent automation, real-time insights, and CI/CD integration.',
    'Unlock AI-driven QA excellence: automate, analyze, and accelerate.',
    'Maximize QA efficiency with AI-powered automation and real-time analytics.',
    'AI-powered QA: automate testing, integrate with CI/CD, and deliver faster.',
    'Intelligent QA automation for faster delivery and superior quality.',
    'Rev up QA with AI: automation, analytics, and CI/CD integration.',
    'Accelerate QA with AI-driven testing, real-time insights, and seamless integration.',
    'Smarter QA: AI-powered automation, real-time analytics, and faster delivery.',
  ];
  automationCategoryCardData = [
    {
      category: 'Web Automation',
      items: [
        {
          id: 'locatorsCreation',
          label: 'Locator Generator',
          icon: 'automationTesting/locatorsCreation.png',
          linkTo: '/automationTesting/locatorsCreation',
          content:
            'The out of the box feature of SDET360.ai platform is to automatically generate the UI locators and create a runtime object repository with appropriate mappings which can be directly leveraged during automation development saving good amount of efforts.',
        },

        {
          id: 'customFunctions',
          label: 'Code Generator',
          icon: 'automationTesting/customFunctions.png',
          linkTo: '/automationTesting/codeGenerator',
          content:
            'State of the art code generator of the SDET360.ai platform encompasses test, code, data generator collectively to generate test scripts adhering to the desired framework with highest level of reliability and reusability.',
        },
        {
          id: 'codeGeneratorWithText',
          label: 'Code Generator with Text',
          icon: 'automationTesting/customFunctions.png',
          linkTo: '/automationTesting/codeGeneratorWithText',
          content:
            'Design and implement tailored functions to solve specific problems, extending software capabilities and automating unique business processes and tasks.',
        },
        {
          id: 'testExecution',
          label: 'Test Execution',
          icon: 'automationTesting/testExecution.png',
          linkTo: 'https://github.com/MayankArora-SDET/SDETAIO/actions',
          isExternal: true,
          content:
            'Perform systematic testing of software, running test cases, scenarios, and scripts to identify defects, bugs, and ensure overall product quality.',
        },

        {
          id: 'executionReport',
          label: 'Execution report',
          icon: 'automationTesting/executionReport.png',
          linkTo: '/automationTesting/executionReport',
          content:
            'Generate detailed reports on test execution results and insights for quality assurance optimization and improvement initiatives daily.',
        },
        {
          id: 'record',
          label: 'Record & Play',
          icon: 'automationTesting/record.png',
          linkTo: '/automationTesting/recordSession/start',
          isExternal: false,
          content:
            'This feature allows users to capture their web interactions within the application. This includes tracking mouse clicks, keyboard inputs, page navigations, and other user events.',
        },
        // {
        //   id: 'promptBased_Automation',
        //   label: 'Prompt Based Automation',
        //   icon: 'automationTesting/record.png',
        //   linkTo: '/automationTesting/promptAutomation',
        //   isExternal: false,
        //   content:
        //     'This feature allows users to capture their web interactions within the application. This includes tracking mouse clicks, keyboard inputs, page navigations, and other user events.',
        // }


      ],
    },
    {
      category: 'API Testing',
      items: [
        {
          id: 'apiTesting',
          label: 'API Testing',
          icon: 'api.png',
          linkTo: '/automationTesting/apiTesting',
          isExternal: false,
          content:
            'Ensure secure, reliable, and efficient API interactions, validating data exchanges and integrations across systems and services.',
        },
      ],
    },
    {
      category: 'Database Testing',
      items: [
        {
          id: 'databaseTesting',
          label: 'Database Testing',
          icon: 'database.png',
          linkTo: '/automationTesting/databaseTesting',
          isExternal: false,
          content:
            'Verify database integrity, security, and performance through rigorous testing of schema, queries, and stored procedures.',
        },
      ],
    },
  ];
  functionalTestingCategoryData: any[] = [
    {
      category: 'Web Testing',
      items: [
        {
          id: 'testCaseGeneration',
          label: 'Test Case Generator',
          linkTo: 'functionalTesting/testCaseGeneration',
          icon: 'functionalTesting/testCaseGeneration.png',
          content:
            'A salient feature of SDET360 is to generate relevant test cases from the stories which are provided as an input empowering quality engineers having broader test coverage and improving the velocity of test case generation. Encompassing the approval flow makes it auditable.',
        },
        {
          id: 'edgeCaseAnalysis',
          label: 'Edge Case Generator',
          linkTo: 'functionalTesting/edgeCaseAnalysis',
          icon: 'functionalTesting/edgeCaseAnalysis.png',
          content:
            'Leverage AI to intelligently identify and generate comprehensive edge cases, ensuring robust test coverage and helps in uncovering potential issues before they impact users.',
        },

        {
          id: 'testDataCreation',
          label: 'Test Data Generator',
          linkTo: 'functionalTesting/testDataCreation',
          icon: 'functionalTesting/testDataCreation1.png',
          content:
            'Data being an imperative part of testing, SDET360.ai enables relevant test data generation within no time and provides comprehensive test coverage. This data generation can be leveraged across functional and non functional testing.',
        },
        {
          id: 'predictBug',
          label: 'Predict a Bug',
          linkTo: 'functionalTesting/predictBug',
          icon: 'functionalTesting/logBug.png',
          content:
            'Rapidly capture software defects with clear, concise reports, enabling swift issue tracking, analysis, and resolution for enhanced quality assurance outcomes.',
        },
      ],
    },
    {
      category: 'Database Testing',
      items: [
        {
          id: 'databaseTesting',
          label: 'Database Test Case Generator',
          icon: 'database.png',
          linkTo: '/functionalTesting/databaseTesting',
          content:
            "Safeguard your application's core with thorough database testing. Our solution checks data accuracy, consistency, and performance to prevent errors and optimize query execution.",
        },
      ],
    },
    {
      category: 'API Testing',
      items: [
        {
          id: 'apiTesting',
          label: 'Api Test Case Generator',
          icon: 'api.png',
          linkTo: '/functionalTesting/apiTesting',
          content:
            'Validate API functionality, security, and performance with rigorous testing. Verify API endpoints, request/response payloads, and error handling to ensure seamless integration and reliable data exchange.',
        },
      ],
    },
  ];
}
