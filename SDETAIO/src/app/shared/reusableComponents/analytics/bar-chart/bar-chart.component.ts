import {
  Component,
  Input,
  OnInit,
  OnChanges,
  SimpleChanges,
} from '@angular/core';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { CommonModule } from '@angular/common';
import { Color, ScaleType, LegendPosition } from '@swimlane/ngx-charts';

interface ChartDataItem {
  name: string;
  value: number;
}

interface GroupedChartDataItem {
  name: string;
  series: ChartDataItem[];
}

@Component({
  selector: 'app-bar-chart',
  standalone: true,
  imports: [NgxChartsModule, CommonModule],
  templateUrl: './bar-chart.component.html',
  styleUrl: './bar-chart.component.css',
})
export class BarChartComponent implements OnInit, OnChanges {
  @Input() chartData: any[] = [];
  @Input() chartTitle: string = '';
  @Input() xAxisLabel: string = 'Category';
  @Input() yAxisLabel: string = 'Count';
  @Input() testResults: string = '';
  @Input() view: [number, number] = [450, 300];
  @Input() colorScheme: any = {
    name: 'default',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#4472C4', '#FFC000'],
  };
  @Input() showLegend: boolean = true;
  @Input() showTotalTestCases: boolean = true;
  @Input() totalHeading: string = 'Total';

  showXAxis = true;
  showYAxis = true;
  gradient = false;
  showXAxisLabel = true;
  showYAxisLabel = true; 
  totalTestCases: number = 0;

  legendPosition = LegendPosition.Right;

  xAxisTickFormatting = (val: any): string => val;
  yAxisTickFormatting = (val: any): string => val.toString();
  showDataLabels: boolean = true;
  barPadding: number = 20;

  ngOnInit() {
    this.calculateTotalTestCases();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['chartData']) {
      this.calculateTotalTestCases();
    }
  }

  calculateTotalTestCases() {
    if (this.chartData && this.chartData.length > 0) {
      if (this.chartData[0].series) {
        this.totalTestCases = this.chartData.reduce(
          (total: number, group: GroupedChartDataItem) => {
            return (
              total +
              group.series.reduce(
                (sum: number, item: ChartDataItem) => sum + item.value,
                0
              )
            );
          },
          0
        );
      } else {
        this.totalTestCases = this.chartData.reduce(
          (sum: number, item: ChartDataItem) => sum + item.value,
          0
        );
      }
    }
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
