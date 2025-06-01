import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { AuthGuard } from './core/gaurds/auth.gaurd';
import { GeneralChatComponent } from './pages/general-chat/general-chat.component';
import { ProductFeaturesComponent } from './pages/product-features/product-features.component';
import { BusinessRequirementComponent } from './pages/business-requirement/business-requirement.component';
import { FunctionalTestingComponent } from './pages/functional-testing/functional-testing.component';
import { TestCaseGenerationComponent } from './pages/functional-products/test-case-generation/test-case-generation.component';
import { TestDataCreationComponent } from './pages/functional-products/test-data-creation/test-data-creation.component';
import { EdgeCaseAnalysisComponent } from './pages/functional-products/edge-case-analysis/edge-case-analysis.component';
import { LogBugComponent } from './pages/functional-products/log-bug/log-bug.component';
import { ApiTestingComponent } from './pages/automation-products/api-testing/api-testing.component';
import { AutomationTestingComponent } from './pages/automation-testing/automation-testing.component';
import { LocatorsCreationComponent } from './pages/automation-products/locatorsCreation/locators-creation/locators-creation.component';
import { LocatorsCreationByUrlComponent } from './pages/automation-products/locatorsCreation/locators-creation-by-url/locators-creation-by-url.component';
import { CodeSnippetsComponent } from './pages/automation-products/code-snippets/code-snippets.component';
import { ExecutionReportComponent } from './pages/automation-products/execution-report/execution-report.component';
import { TestExecutionComponent } from './pages/automation-products/test-execution/test-execution.component';
import { TestCoverageAnalysisComponent } from './pages/automation-products/test-coverage-analysis/test-coverage-analysis.component';
import { AnalyticsComponent } from './pages/analytics/analytics.component';
import { FunctionalApiTestComponent } from './pages/functional-products/functional-api-test/functional-api-test.component';
import { AutomationDatabaseTestingComponent } from './pages/automation-products/automation-database-testing/automation-database-testing.component';
import { FunctionalDatabaseTestingComponent } from './pages/functional-products/functional-database-testing/functional-database-testing.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { RecordViewComponent } from './shared/reusableComponents/recordComponents/record-view/record-view.component';
import { AccessibilityTestingComponent } from './pages/functional-products/accessibility-testing/accessibility-testing.component';
import { PerformanceTestingComponent } from './pages/performance-testing/performance-testing.component';
import { NewDashboardComponent } from './pages/new-dashboard/new-dashboard.component';
import { CodeGeneratorWithTextComponent } from './pages/automation-products/code-generator-with-text/code-generator-with-text.component';
import { DefaultLocatorCreationComponent } from './pages/automation-products/locatorsCreation/default-locator-creation/default-locator-creation.component';
import { PromptAutomationComponent } from './pages/automation-products/prompt-automation/prompt-automation.component';
import { RecordAndPlayComponent } from './pages/automation-products/recordSession/record-and-play/record-and-play.component';
import { CodeGeneratorComponent } from './pages/automation-products/code-generator/code-generator.component';

export const routes: Routes = [
  {
    path: '', canActivateChild: [AuthGuard], children: [
      { path: 'login', component: LoginComponent },
      { path: '', component: NewDashboardComponent, pathMatch: 'full' },
      { path: 'accessibility', component: AccessibilityTestingComponent },
      { path: 'performance', component: PerformanceTestingComponent },

      {
        path: 'generalChat',
        component: GeneralChatComponent,
        pathMatch: 'full'

      },
      {
        path: 'productFeatures',
        component: ProductFeaturesComponent,

      },
      {
        path: 'productFeatures',
        children: [
          {
            path: 'businessRequirement',
            component: BusinessRequirementComponent,
          },
        ],
      },
      {
        path: 'functionalTesting',
        component: FunctionalTestingComponent,

      },
      {
        path: 'functionalTesting',
        children: [
          { path: 'testCaseGeneration', component: TestCaseGenerationComponent },
          { path: 'testDataCreation', component: TestDataCreationComponent },
          { path: 'edgeCaseAnalysis', component: EdgeCaseAnalysisComponent },
          { path: 'predictBug', component: LogBugComponent },
          {
            path: 'databaseTesting',
            component: FunctionalDatabaseTestingComponent,
          },
          { path: 'apiTesting', component: FunctionalApiTestComponent },
        ],
      },
      {
        path: 'automationTesting',
        component: AutomationTestingComponent,

      },
      {
        path: 'automationTesting',


        children: [
          {
            path: 'locatorsCreation',
            component: DefaultLocatorCreationComponent,
            children: [
              { path: '', redirectTo: 'prompt', pathMatch: 'full' },
              { path: 'prompt', component: LocatorsCreationComponent },
              { path: 'url', component: LocatorsCreationByUrlComponent }
            ],
          },
          // {
          //   path: 'locatorsCreation/url',
          //   component: LocatorsCreationByUrlComponent,
          // },
          { path: 'codeSnippets', component: CodeSnippetsComponent },
          { path: 'codeGenerator', component: CodeGeneratorComponent },
          { path: 'codeGeneratorWithText', component: CodeGeneratorWithTextComponent },
          { path: 'executionReport', component: ExecutionReportComponent },
          { path: 'testExecution', component: TestExecutionComponent },
          {
            path: 'testCoverageAnalysis',
            component: TestCoverageAnalysisComponent,
          },
          {
            path: 'databaseTesting',
            component: AutomationDatabaseTestingComponent,
          },
          { path: 'apiTesting', component: ApiTestingComponent },
          {
            path: 'recordSession',
            children: [
              { path: '', redirectTo: 'start', pathMatch: 'full' },
              { path: 'start', component: RecordAndPlayComponent },
            ],
          },
          {
            path: 'recordSession/library',
            children: [
              { path: '', component: RecordAndPlayComponent },
              { path: ':tickedId', component: RecordViewComponent },
            ],
          },
          {
            path: 'promptAutomation',
            component: PromptAutomationComponent
          }

        ],
      },
      {
        path: 'analytics',
        component: AnalyticsComponent,
      },
      { path: 'profile', component: ProfileComponent }
    ]
  }
];