import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  NgxChartsModule,
  Color,
  ScaleType,
  LegendPosition,
} from '@swimlane/ngx-charts';
import { curveLinear } from 'd3-shape';
import { LineChartData } from '../../../../core/interfaces/lineChartData';

 

@Component({
  selector: 'app-line-chart',
  standalone: true,
  imports: [CommonModule, NgxChartsModule],
  templateUrl: './line-chart.component.html',
  styleUrl: './line-chart.component.css',
})
export class LineChartComponent implements OnInit {
  @Input() chartTitle: string = 'Test Cases Burnup';
  @Input() chartData: LineChartData[] = [];
  @Input() colorScheme: Color = {
    name: 'burnup-chart-colors',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#1f77b4', '#4daf4a', '#e41a1c', '#666666'],
  };
  @Input() view: [number, number] = [700, 400];
  @Input() xAxisLabel: string = 'Date';
  @Input() yAxisLabel: string = 'Test Cases';
  @Input() showLegend: boolean = true;
  @Input() showXAxisLabel: boolean = true;
  @Input() showYAxisLabel: boolean = true;
  @Input() showGridLines: boolean = true;
  @Input() showXAxis: boolean = true;
  @Input() showYAxis: boolean = true;
  @Input() curve = curveLinear;
  @Input() legendPosition: LegendPosition = LegendPosition.Right;
  @Input() autoScale: boolean = false;
  @Input() roundDomains: boolean = false;
  @Input() showRefLines: boolean = true;
  @Input() referenceLines: any[] = [];
  @Input() idealTrendLine: boolean = true;

  constructor() {}

  ngOnInit(): void {
    console.log('BurnupChart initialized');
  }

  onSelect(event: any): void {
    console.log('Selected Item:', event);
  }

  onActivate(event: any): void {
    console.log('Activation:', event);
  }

  onDeactivate(event: any): void {
    console.log('Deactivation:', event);
  }
  onResize(event: any) {
    if (this.view) {
      const aspectRatio = this.view[1] / this.view[0];
      const width = event.target.innerWidth;
      const calculatedWidth = width > 700 ? 700 : width - 50;
      this.view = [calculatedWidth, calculatedWidth * aspectRatio];
    }
  }
}
