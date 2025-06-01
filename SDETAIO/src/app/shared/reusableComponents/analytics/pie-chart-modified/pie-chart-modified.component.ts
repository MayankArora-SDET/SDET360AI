 import { Component, Input, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxChartsModule, Color, ScaleType } from '@swimlane/ngx-charts';
import { PieChartData } from '../../../../core/interfaces/pieChartData';

@Component({
  selector: 'app-pie-chart-modified',
  standalone: true,
  imports: [CommonModule, NgxChartsModule],
  templateUrl: './pie-chart-modified.component.html',
  styleUrls: ['./pie-chart-modified.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class PieChartModifiedComponent { 
  @Input() title: string = 'Chart Title';
  @Input() data: PieChartData[] = [];
 
  @Input() colorScheme: Color = {
    name: 'custom',
    selectable: true,
    group: ScaleType.Ordinal,
    domain: ['#3498db', '#2ecc71', '#e74c3c', '#f39c12', '#9b59b6'],
  };
 
  @Input() showLegend: boolean = false;
  @Input() showLabels: boolean = true;
  @Input() isDoughnut: boolean = true;
  @Input() animations: boolean = true;
  @Input() view: [number, number] = [500, 400];
 
  getTotalValue(): number {
    return this.data.reduce((acc, item) => acc + item.value, 0);
  }
 
  getPercentage(item: PieChartData): number {
    const total = this.getTotalValue();
    return total > 0 ? Math.round((item.value / total) * 100) : 0;
  }
 
  labelFormat = (data: any) => {
    const percentage = this.getPercentage({
      name: data.name,
      value: data.value,
    });
    return `${percentage}%`;
  };
}
  