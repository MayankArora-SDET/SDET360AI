import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardHeaderComponent } from '../dashboard-header/dashboard-header.component';
import {
  NgxChartsModule,
  Color,
  ScaleType,
  LegendPosition,
} from '@swimlane/ngx-charts';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { BarChartComponent } from '../../analytics/bar-chart/bar-chart.component';
import { DashboardDataService } from '../../../../core/service/dashboard.service';
import { Subject } from 'rxjs';
import { testCaseVerticalData, testExecutionOverTimeDataInterface } from '../../../../core/interfaces/pieChartData';
import { LineChartComponent } from '../../analytics/line-chart/line-chart.component';

@Component({
  selector: 'app-project-report-tab',
  standalone: true,
  imports: [
    CommonModule,
    NgxChartsModule,
    LineChartComponent,
    BarChartComponent,
    MatFormFieldModule,
    MatSelectModule,
    FormsModule,
    DashboardHeaderComponent,
  ],
  providers: [],
  templateUrl: './project-report-tab.component.html',
  styleUrl: './project-report-tab.component.css'
})
export class ProjectReportTabComponent implements OnInit {
  private destroy$ = new Subject<void>();
  legendPosition = LegendPosition;

  selectedVertical: string = 'kan';
  selectedMetric: string = 'totalTests';

  // Metrics counts
  totalTests: { [key: string]: number } = { kan: 312, ray: 270 };
  totalStories: { [key: string]: number } = { kan: 55, ray: 61 };
  totalDefects: { [key: string]: number } = { kan: 97, ray: 132 };
  totalTasks: { [key: string]: number } = { kan: 312, ray: 270 };

  // Sprint data in an array for better iteration in the template
  sprintData = [
    {
      name: 'Sprint 1',
      metrics: [
        { label: 'Unexecuted', value: 312, color: '' },
        { label: 'Passed', value: 212, color: 'text-green-600' },
        { label: 'Failed', value: 100, color: 'text-red-600' },
        { label: 'Logged', value: 100, color: '' }
      ]
    },
    {
      name: 'Sprint 2',
      metrics: [
        { label: 'Unexecuted', value: 76, color: '' },
        { label: 'Passed', value: 185, color: 'text-green-600' },
        { label: 'Failed', value: 82, color: 'text-red-600' },
        { label: 'Logged', value: 87, color: '' }
      ]
    }
  ];

  ngOnInit(): void {
    // Set default selected metric on component initialization
    this.selectedMetric = 'totalTests';
  }

  onVerticalChange(value: string) {
    this.selectedVertical = value;
    console.log('Vertical changed to:', value);
  }

  selectMetric(metric: string) {
    this.selectedMetric = metric;
    console.log('Selected metric:', metric);
  }

  navigateToExecuted() {
    // Handle navigation to executed tests page or section
    console.log('Navigating to Executed section');
    // Implement navigation logic here
  }

  navigateToTestToExecute() {
    // Handle navigation to tests to execute page or section
    console.log('Navigating to Test to Execute section');
    // Implement navigation logic here
  }


  testCaseStatus: testCaseVerticalData = {
    "kan": [
      { name: 'Open', value: 24 },
      { name: 'In-Progress', value: 13 },
      { name: 'Closed', value: 60 },
    ], "ray": [
      { name: 'Open', value: 30 },
      { name: 'In-Progress', value: 18 },
      { name: 'Closed', value: 45 },
    ]
  }

  testExecutionStatusData: testCaseVerticalData =
    {
      "kan": [{ name: 'Passed', value: 161 },
      { name: 'Failed', value: 51 },
      { name: 'Unexecuted', value: 100 }],
      "ray": [{ name: 'Passed', value: 150 },
      { name: 'Failed', value: 45 },
      { name: 'Unexecuted', value: 75 }]
    };

  defectsByPriorityData: testCaseVerticalData = {
    "kan": [
      { name: 'P1-Blocker', value: 10 },
      { name: 'P2-High', value: 9 },
      { name: 'P3-Medium', value: 24 },
      { name: 'P4-Low', value: 54 },
    ],
    "ray": [
      { name: 'P1-Blocker', value: 15 },
      { name: 'P2-High', value: 12 },
      { name: 'P3-Medium', value: 35 },
      { name: 'P4-Low', value: 70 },
    ]
  };

  storiesAndEpicsData: testCaseVerticalData = {
    "kan": [
      { name: 'ToDo', value: 15 },
      { name: 'In Development', value: 12 },
      { name: 'In QA', value: 13 },
      { name: 'Closed', value: 15 },
    ],
    "ray": [
      { name: 'ToDo', value: 18 },
      { name: 'In Development', value: 13 },
      { name: 'In QA', value: 12 },
      { name: 'Closed', value: 18 },
    ]
  }

  testCaseProgressByLabelsBarData: any[] = [
    { name: 'Dashboard', value: 45 },
    { name: 'API', value: 32 },
    { name: 'Login', value: 28 },
    { name: 'Database', value: 20 },
    { name: 'Security', value: 35 },
    { name: 'Performance', value: 22 },
    { name: 'Accessibility', value: 18 },
  ];

  bugPriorityData: testCaseVerticalData = {
    "kan": [
      { name: 'Critical', value: 12 },
      { name: 'High', value: 7 },
      { name: 'Medium', value: 25 },
      { name: 'Low', value: 53 },
    ],
    "ray": [
      { name: 'Critical', value: 10 },
      { name: 'High', value: 5 },
      { name: 'Medium', value: 20 },
      { name: 'Low', value: 60 },
    ]
  }

  automationTestStatus: testCaseVerticalData = {
    "kan": [
      { name: 'Automated', value: 161 },
      { name: 'In Progress', value: 15 },
      { name: 'Not Automated', value: 112 },
      { name: 'C/N be Automated', value: 24 },
    ],
    "ray": [
      { name: 'Automated', value: 150 },
      { name: 'In Progress', value: 20 },
      { name: 'Not Automated', value: 80 },
      { name: 'C/N be Automated', value: 20 },
    ]
  }
  testExecutionOverTimeData: testExecutionOverTimeDataInterface = {
    'kan': [
      {
        name: 'Actual Executed',
        series: [
          { name: '1', value: 10 },
          { name: '2', value: 25 },
          { name: '3', value: 50 },
          { name: '4', value: 80 },
          { name: '5', value: 110 },
          { name: '6', value: 140 },
          { name: '7', value: 170 },
          { name: '8', value: 200 },
          { name: '9', value: 261 },
          { name: '10', value: 312 },
        ],
      },

      {
        name: 'Actual Passed',
        series: [
          { name: '1', value: 5 },
          { name: '2', value: 15 },
          { name: '3', value: 30 },
          { name: '4', value: 45 },
          { name: '5', value: 65 },
          { name: '6', value: 85 },
          { name: '7', value: 110 },
          { name: '8', value: 135 },
          { name: '9', value: 161 },
          { name: '10', value: 190 },
        ],
      },
      {
        name: 'Actual Failed',
        series: [
          { name: '1', value: 2 },
          { name: '2', value: 5 },
          { name: '3', value: 10 },
          { name: '4', value: 20 },
          { name: '5', value: 30 },
          { name: '6', value: 40 },
          { name: '7', value: 45 },
          { name: '8', value: 50 },
          { name: '9', value: 51 },
          { name: '10', value: 62 },
        ],
      },
      {
        name: 'Unexecuted',
        series: [
          { name: '1', value: 23 },
          { name: '2', value: 40 },
          { name: '3', value: 50 },
          { name: '4', value: 55 },
          { name: '5', value: 55 },
          { name: '6', value: 55 },
          { name: '7', value: 55 },
          { name: '8', value: 65 },
          { name: '9', value: 100 },
          { name: '10', value: 60 },
        ],
      },
      {
        name: 'Planned Test Cases',
        series: [
          { name: '1', value: 30 },
          { name: '2', value: 60 },
          { name: '3', value: 90 },
          { name: '4', value: 120 },
          { name: '5', value: 150 },
          { name: '6', value: 180 },
          { name: '7', value: 210 },
          { name: '8', value: 250 },
          { name: '9', value: 280 },
          { name: '10', value: 312 },
        ],
      },
    ], 'ray': [
      {
        name: 'Actual Executed',
        series: [
          { name: '1', value: 15 },
          { name: '2', value: 25 },
          { name: '3', value: 60 },
          { name: '4', value: 90 },
          { name: '5', value: 100 },
          { name: '6', value: 165 },
          { name: '7', value: 170 },
          { name: '8', value: 187 },
          { name: '9', value: 234 },
          { name: '10', value: 270 },
        ],
      },

      {
        name: 'Actual Passed',
        series: [
          { name: '1', value: 8 },
          { name: '2', value: 23 },
          { name: '3', value: 37 },
          { name: '4', value: 58 },
          { name: '5', value: 78 },
          { name: '6', value: 90 },
          { name: '7', value: 118 },
          { name: '8', value: 150 },
          { name: '9', value: 176 },
          { name: '10', value: 205 },
        ],
      },
      {
        name: 'Actual Failed',
        series: [
          { name: '1', value: 9 },
          { name: '2', value: 12 },
          { name: '3', value: 15 },
          { name: '4', value: 30 },
          { name: '5', value: 35 },
          { name: '6', value: 40 },
          { name: '7', value: 48 },
          { name: '8', value: 53 },
          { name: '9', value: 61 },
          { name: '10', value: 70 },
        ],
      },
      {
        name: 'Unexecuted',
        series: [
          { name: '1', value: 20 },
          { name: '2', value: 43 },
          { name: '3', value: 50 },
          { name: '4', value: 60 },
          { name: '5', value: 60 },
          { name: '6', value: 58 },
          { name: '7', value: 65 },
          { name: '8', value: 65 },
          { name: '9', value: 120 },
          { name: '10', value: 80 },
        ],
      },
      {
        name: 'Planned Test Cases',
        series: [
          { name: '1', value: 30 },
          { name: '2', value: 60 },
          { name: '3', value: 90 },
          { name: '4', value: 110 },
          { name: '5', value: 140 },
          { name: '6', value: 170 },
          { name: '7', value: 200 },
          { name: '8', value: 230 },
          { name: '9', value: 260 },
          { name: '10', value: 270 },
        ],
      },
    ]

  }

  statusColorScheme: Color = {
    name: 'status-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#F44336', '#FF9800', '#4CAF50'],
  };

  testExecutionStatusColorScheme: Color = {
    name: 'test-execution-status-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#4CAF50', '#F44336', '#9E9E9E'],
  };

  categoryColorScheme: Color = {
    name: 'category-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#A3C1DA', '#B0E0E6', '#C3E2C2'],
  };

  automationStatusColorScheme: Color = {
    name: 'automation-status-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#4CAF50', '#FFC107', '#BDBDBD', '#F44336'],
  };
  barChartColorScheme: Color = {
    name: 'bar-chart-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#BDBDBD', '#2196F3', '#FF9800', '#4CAF50'],
  };

  bugPriorityColorScheme: Color = {
    name: 'bug-priority-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#F44336', '#FF9800', '#FFEB3B', '#2196F3'],
  };

  defectPriorityColorScheme: Color = {
    name: 'defect-priority-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#D32F2F', '#FF5722', '#FFC107', '#4FC3F7'],
  };

  testCaseProgressColorScheme: Color = {
    name: 'test-case-progress-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: [
      '#8dd3c7',
      '#ffffb3',
      '#bebada',
      '#fb8072',
      '#80b1d3',
      '#fdb462',
      '#Fd7072',
    ],
  };

  testExecutionColorScheme: Color = {
    name: 'test-execution-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#ff9800', '#4caf50', '#f44336', '#9e9e9e', '#8dd3c7'],
  };

  constructor(private dashboardDataService: DashboardDataService) { }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  // private subscribeToDataStreams() {
  //   this.dashboardDataService.testCaseStatus$
  //     .pipe(takeUntil(this.destroy$))
  //     .subscribe((data) => {
  //       this.testCaseStatus = data;
  //     });

  //   this.dashboardDataService.defectsByPriority$
  //     .pipe(takeUntil(this.destroy$))
  //     .subscribe((data) => {
  //       this.defectsByPriorityData = data;
  //     });

  //   // this.dashboardDataService.storiesAndEpics$
  //   //   .pipe(takeUntil(this.destroy$))
  //   //   .subscribe((data) => {
  //   //     this.storiesAndEpicsData = data;
  //   //   });

  //   this.dashboardDataService.testCaseProgressByLabels$
  //     .pipe(takeUntil(this.destroy$))
  //     .subscribe((data) => {
  //       this.testCaseProgressByLabelsBarData = data.map((item) => ({
  //         name: item.name,
  //         value: item.value,
  //       }));
  //     });

  //   // this.dashboardDataService.testExecutionStatus$
  //   //   .pipe(takeUntil(this.destroy$))
  //   //   .subscribe((data) => {
  //   //     this.testExecutionStatusData = data;
  //   //   });

  //   this.dashboardDataService.bugPriority$
  //     .pipe(takeUntil(this.destroy$))
  //     .subscribe((data) => {
  //       this.bugPriorityData = data;
  //     });

  //   this.dashboardDataService.automationTestStatus$
  //     .pipe(takeUntil(this.destroy$))
  //     .subscribe((data) => {
  //       this.automationTestStatus = data;
  //     });
  // }

  /**
   * Load all dashboard data from the service
   */
  private loadDashboardData() {
    this.dashboardDataService.loadAllDashboardData();
  }

  /**
   * Updates metrics for a specific sprint
   * @param sprintIndex The index of the sprint to update (0 for Sprint 1, 1 for Sprint 2, etc.)
   * @param metrics Object containing the updated metrics values
   */
  updateSprintMetrics(sprintIndex: number, metrics: { unexecuted?: number, passed?: number, failed?: number, logged?: number }) {
    if (sprintIndex < 0 || sprintIndex >= this.sprintData.length) {
      console.error(`Sprint index ${sprintIndex} out of bounds`);
      return;
    }

    // Update only the provided metrics
    const sprint = this.sprintData[sprintIndex];

    if (metrics.unexecuted !== undefined) {
      this.updateMetricValue(sprint, 'Unexecuted', metrics.unexecuted);
    }

    if (metrics.passed !== undefined) {
      this.updateMetricValue(sprint, 'Passed', metrics.passed);
    }

    if (metrics.failed !== undefined) {
      this.updateMetricValue(sprint, 'Failed', metrics.failed);
    }

    if (metrics.logged !== undefined) {
      this.updateMetricValue(sprint, 'Logged', metrics.logged);
    }
  }

  /**
   * Helper method to update a specific metric value in a sprint
   */
  private updateMetricValue(sprint: any, metricLabel: string, value: number) {
    const metric = sprint.metrics.find((m: any) => m.label === metricLabel);
    if (metric) {
      metric.value = value;
    }
  }

  /**
   * Adds a new sprint with default metrics
   * @param sprintName Name of the new sprint (e.g. 'Sprint 3')
   * @returns The index of the newly added sprint
   */
  addNewSprint(sprintName: string): number {
    const newSprint = {
      name: sprintName,
      metrics: [
        { label: 'Unexecuted', value: 0, color: '' },
        { label: 'Passed', value: 0, color: 'text-green-600' },
        { label: 'Failed', value: 0, color: 'text-red-600' },
        { label: 'Logged', value: 0, color: '' }
      ]
    };

    this.sprintData.push(newSprint);
    return this.sprintData.length - 1;
  }

  refreshDashboard() {
    this.dashboardDataService.refreshAllData();
  }

  // getTotalTestCases(): number {
  //   return this.testCaseStatus.reduce((sum, item) => sum + item.value, 0);
  // }

  // getTotalDefects(): number {
  //   return this.defectsByPriorityData.reduce(
  //     (sum, item) => sum + item.value,
  //     0
  //   );
  // }

  getTotalTestCasesByLabels(): number {
    return this.testCaseProgressByLabelsBarData.reduce(
      (sum, item) => sum + item.value,
      0
    );
  }
}
