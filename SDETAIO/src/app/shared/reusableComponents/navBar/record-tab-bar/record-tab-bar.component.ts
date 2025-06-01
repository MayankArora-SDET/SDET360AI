import { CommonModule } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-record-tab-bar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './record-tab-bar.component.html',
  styleUrl: './record-tab-bar.component.css'
})
export class RecordTabBarComponent {
  @Input() tabSections: any


}
