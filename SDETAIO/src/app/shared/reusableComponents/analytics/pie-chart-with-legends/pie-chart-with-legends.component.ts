import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgxChartsModule, Color, ScaleType } from '@swimlane/ngx-charts';
import { PieChartData } from '../../../../core/interfaces/pieChartData';

@Component({
  selector: 'app-pie-chart-with-legends',
  standalone: true,
  imports: [CommonModule, NgxChartsModule],
  templateUrl: './pie-chart-with-legends.component.html',
  styleUrls: ['./pie-chart-with-legends.component.css'],
})
export class PieChartWithLegendsComponent {
  @Input() data: PieChartData[] = [];
  @Input() title: string = '';
  @Input() showLegend: boolean = true;
  @Input() view: [number, number] = [400, 300];
  @Input() colorScheme: any = {
    domain: ['#008080', '#4682B4', '#2E8B57'],
  };

  getColorForItem(name: string): string {
    const index = this.data.findIndex((item) => item.name === name);
    return this.colorScheme.domain[index % this.colorScheme.domain.length];
  }

  getTotalValue(): number {
    return this.data.reduce((acc, item) => acc + item.value, 0);
  }

  getPercentage(value: number): string {
    const total = this.getTotalValue();
    return total > 0 ? Math.round((value / total) * 100).toString() : '0';
  }
}
