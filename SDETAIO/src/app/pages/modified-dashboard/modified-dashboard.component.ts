import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PieChartModifiedComponent } from '../../shared/reusableComponents/analytics/pie-chart-modified/pie-chart-modified.component';
import { PieChartData } from '../../core/interfaces/pieChartData';
import {
  NgxChartsModule,
  Color,
  ScaleType,
  LegendPosition,
} from '@swimlane/ngx-charts';
import { PieChartWithLegendsComponent } from '../../shared/reusableComponents/analytics/pie-chart-with-legends/pie-chart-with-legends.component';
import { BarChartComponent } from '../../shared/reusableComponents/analytics/bar-chart/bar-chart.component';
import { LineChartComponent } from '../../shared/reusableComponents/analytics/line-chart/line-chart.component';
import { Subject, takeUntil } from 'rxjs';
import { DashboardDataService } from '../../core/service/dashboard.service';

@Component({
  selector: 'app-modified-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    PieChartModifiedComponent,
    NgxChartsModule,
    PieChartWithLegendsComponent,
    BarChartComponent,
    LineChartComponent,
  ],
  providers: [DashboardDataService],
  templateUrl: './modified-dashboard.component.html',
  styleUrl: './modified-dashboard.component.css',
})
export class ModifiedDashboardComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  legendPosition = LegendPosition;

  testCaseStatus: PieChartData[] = [];
  testCaseCategory: PieChartData[] = [];
  defectsByPriorityData: PieChartData[] = [];
  storiesAndEpicsData: any[] = [];
  testExecutionData: any[] = [];
  bugPriorityData: PieChartData[] = [];
  testExecutionStatusData: PieChartData[] = [];
  testCaseProgressByLabelsBarData: any[] = [];
  testCaseProgressByLabelsData: any[] = [];
  automationTestStatus: PieChartData[] = [];
  rawBugData: any = { bug_count_by_priority: {}, total_unresolved_bugs: 0 };

  statusColorScheme: Color = {
    name: 'status-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#B0C4DE', '#87CEEB', '#A2D9CE'],
  };
  testExecutionStatusColorScheme: Color = {
    name: 'test-execution-status-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#e41a1c', '#4daf4a', '#377eb8'],
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
    domain: ['#FFA500', '#32CD32', '#4169E1', '#E8B71D'],
  };

  barChartColorScheme: Color = {
    name: 'bar-chart-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#4472C4', '#FFC000'],
  };

  bugPriorityColorScheme: Color = {
    name: 'bug-priority-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#1F77B4', '#FF7F0E', '#E8B71D', '#5AA454'],
  };

  defectPriorityColorScheme: Color = {
    name: 'defect-priority-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#1F77B4', '#E8B71D', '#FF7F0E', '#5AA454'],
  };
  testCaseProgressColorScheme: Color = {
    name: 'test-case-progress-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#8dd3c7', '#ffffb3', '#bebada', '#fb8072', '#80b1d3', '#fdb462'],
  };

  testExecutionColorScheme: Color = {
    name: 'test-execution-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#4daf4a', '#e41a1c', '#377eb8'],
  };
  testExecutionOverTimeData: any[] = [
    {
      name: 'Passed',
      series: [
        { name: '1', value: 3 },
        { name: '2', value: 5 },
        { name: '3', value: 2 },
        { name: '4', value: 6 },
        { name: '5', value: 8 },
        { name: '6', value: 7 },
        { name: '7', value: 10 },
        { name: '8', value: 12 },
        { name: '9', value: 9 },
        { name: '10', value: 11 },
      ],
    },
    {
      name: 'Failed',
      series: [
        { name: '1', value: 1 },
        { name: '2', value: 2 },
        { name: '3', value: 3 },
        { name: '4', value: 3 },
        { name: '5', value: 2 },
        { name: '6', value: 3 },
        { name: '7', value: 4 },
        { name: '8', value: 2 },
        { name: '9', value: 5 },
        { name: '10', value: 3 },
      ],
    },
    {
      name: 'In Progress',
      series: [
        { name: '1', value: 2 },
        { name: '2', value: 3 },
        { name: '3', value: 4 },
        { name: '4', value: 2 },
        { name: '5', value: 3 },
        { name: '6', value: 5 },
        { name: '7', value: 3 },
        { name: '8', value: 2 },
        { name: '9', value: 4 },
        { name: '10', value: 6 },
      ],
    },
  ];

  constructor(private dashboardDataService: DashboardDataService) {}

  ngOnInit() {
    this.subscribeToDataStreams();
    this.loadDashboardData();
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private subscribeToDataStreams() {
    // Subscribe to all data streams from the service
    this.dashboardDataService.testCaseStatus$
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => {
        this.testCaseStatus = data;
      });

    // this.dashboardDataService.testCaseCategory$
    //   .pipe(takeUntil(this.destroy$))
    //   .subscribe((data) => {
    //     this.testCaseCategory = data;
    //   });

    this.dashboardDataService.defectsByPriority$
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => {
        this.defectsByPriorityData = data;
      });

    this.dashboardDataService.storiesAndEpics$
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => {
        this.storiesAndEpicsData = data;
      });
    this.dashboardDataService.testCaseProgressByLabels$
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => {
        this.testCaseProgressByLabelsData = data;

        this.testCaseProgressByLabelsBarData = data.map((item) => ({
          name: item.name,
          value: item.value,
        }));
      });
    this.dashboardDataService.testExecutionStatus$
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => {
        this.testExecutionStatusData = data;
      });

    this.dashboardDataService.testExecution$
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => {
        this.testExecutionData = data;
      });

    this.dashboardDataService.bugPriority$
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => {
        this.bugPriorityData = data;
      });

    this.dashboardDataService.rawBugData$
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => {
        this.rawBugData = data;
      });
    this.dashboardDataService.automationTestStatus$
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => {
        this.automationTestStatus = data;
      });
  }

  private loadDashboardData() {
    // Load all dashboard data from API
    this.dashboardDataService.loadAllDashboardData();
  }
  getTotalTestCasesByLabels(): number {
    return this.testCaseProgressByLabelsData.reduce(
      (sum, item) => sum + item.value,
      0
    );
  }

  // Method to manually refresh the data if needed
  refreshDashboard() {
    this.dashboardDataService.refreshAllData();
  }

  // Keep utility methods
  getTotalTestCases(): number {
    return this.testCaseStatus.reduce((sum, item) => sum + item.value, 0);
  }

  getCategoryCount(): number {
    return this.testCaseCategory.length;
  }

  getTotalDefects(): number {
    return this.rawBugData.total_unresolved_bugs;
  }
}
