import { Component, OnInit } from '@angular/core';
import { PieChartComponent } from '../../shared/reusableComponents/analytics/pie-chart/pie-chart.component';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment.development';
import { AnalyticsService } from '../../core/service/analytics.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [PieChartComponent, CommonModule],
  templateUrl: './analytics.component.html',
  styleUrl: './analytics.component.css'
})
export class AnalyticsComponent implements OnInit {
  statusCounts: { [key: string]: number } = {};
  testCaseResultCounts: { [key: string]: number } = {};
  bugCounts: { [key: string]: number } = {};
  executionStatusCounts: { [key: string]: number } = {};
  renderChart = false;
  constructor(private http: HttpClient, private analyticsService: AnalyticsService, public router: Router) { }
  ngOnInit(): void {
    this.analyticsService.getAnalyticsData().subscribe({
      next: () => {
        this.statusCounts = this.analyticsService.statusCounts || {}
        this.testCaseResultCounts = this.analyticsService.testCaseResultCounts
        this.bugCounts = this.analyticsService.bugCounts
        this.executionStatusCounts = this.analyticsService.executionStatusCounts
        this.renderChart = true
        console.log(this.getObjectKeys(this.statusCounts), "in init")
      }, error: err => console.log(err)
    })
  }



  getObjectKeys(obj: { [key: string]: number }): string[] {
    return Object.keys(obj);
  }

  getObjectValues(obj: { [key: string]: number }): number[] {
    return Object.values(obj);
  }

}
