import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Input } from '@angular/core';
@Component({
  selector: 'app-locator-table',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './locator-table.component.html',
  styleUrl: './locator-table.component.css'
})
export class LocatorTableComponent implements OnInit {
  ngOnInit(): void {
    this.locatorArray = Object.entries(this.locatorData);
  }
  columnLabels = ['Locator', 'Value'];
  @Input() locatorData: { [key: string]: string } = {};
  locatorArray: [string, string][] = [];
}
