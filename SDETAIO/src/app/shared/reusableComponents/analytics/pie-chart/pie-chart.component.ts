import { AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { Chart ,ChartData,ChartType,registerables} from 'chart.js';
Chart.register(...registerables)
@Component({
  selector: 'app-pie-chart',
  standalone: true,
  imports: [],
  templateUrl: './pie-chart.component.html',
  styleUrl: './pie-chart.component.css'
})
export class PieChartComponent implements AfterViewInit {
  // @ViewChild('myCanvas', { static: false }) canvasRef!: ElementRef<HTMLCanvasElement>;
  chart:any
   constructor(){
   }
    // this.canvasId = this.canvasRef.nativeElement;
@Input() labels!:string[];
@Input() values!:number[];
@Input() chartId!:string;
data:any={}
config:any={}
ngAfterViewInit(){
  console.log(this.labels,"inchild",this.values,this.chartId)
  this.data = {
    labels:this.labels,
    datasets: [{
      label: 'count',
      data:this.values,
      backgroundColor: [
        'rgb(255, 99, 132)',
        'rgb(54, 162, 235)',
        'rgb(255, 205, 86)',
        'rgb(150, 199, 83)',
        'rgb(0, 59, 255)'
        
      ],
      hoverOffset: 4
    }] 
  };
  this.config = {
      type: 'pie',
      data: this.data,
      options: {
        plugins: {
            legend: {
              position: 'bottom', // Positions the legend at the bottom
                display: true,
                title: {
                  display: true, // Enable the title
                  text: this.chartId, // Chart title text
                   position: 'top',
                  font: {
                      size: 16, // Title font size
                      weight: 'bold' // Title font weight
                  },
                  padding: {
                      top: 10,
                  },
                  color: '#333' // Title color
              },
                labels: {
                  usePointStyle: true, // Use point style for legend items
                  pointStyle: 'circle', // Circle shape for legend items
                  padding: 10 ,        // Space between legend items
              
                  font: {
                    size: 10 // Smaller font size also reduces the circle size
                }
                },
              

            },
            width: 200,  // Width of the chart
            height: 200  // Height of the chart
        },
      }
    
    
    };

 this.chart=new Chart(this.chartId,this.config);
}
}
