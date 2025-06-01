import { Component, Input, OnInit, Output } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardDataService } from '../../../../core/service/dashboard.service';
@Component({
  selector: 'app-dashboard-header',
  standalone: true,
  imports: [MatFormFieldModule, MatSelectModule, FormsModule, CommonModule],
  templateUrl: './dashboard-header.component.html',
  styleUrl: './dashboard-header.component.css'
})
export class DashboardHeaderComponent implements OnInit {
  constructor(private dashboardService: DashboardDataService) { }
  ngOnInit() {
    this.dashboardService.verticalSelected$.subscribe((vertical) => {
      this.selectedVertical = vertical;
      console.log("Selected vertical: ", vertical)

    })

  }

  verticalsList: string[] = ["kan", "ray"]
  selectedVertical!: string;
  @Input() title: string = "Dashboard Title"
  @Output() onVerticalSelected: EventEmitter<any> = new EventEmitter<any>();
  onVerticalChange(event: MatSelectChange) {
    this.onVerticalSelected.emit(event.value);
    this.dashboardService.setVerticalSelected(event.value)

  }

}
