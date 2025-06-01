import { Component, Input, OnInit } from '@angular/core';
import { MatTabGroup, MatTab } from '@angular/material/tabs';
import { CommonModule } from '@angular/common';
import { cardDatatype } from '../../../core/interfaces/global';
import { Router } from '@angular/router';
@Component({
  selector: 'app-card-tabs',
  standalone: true,
  imports: [CommonModule, MatTabGroup, MatTab],
  templateUrl: './card-tabs.component.html',
  styleUrl: './card-tabs.component.css'
})
export class CardTabsComponent implements OnInit {
  // tabs = [{ label: "label1", content: "content" }, { label: "label2", content: "content2" }]
  @Input() tabs!: { label: string, data: cardDatatype[] }[];
  @Input() selectedIndex: number = 0;
  constructor(private router: Router) { }

  ngOnInit() {
    // Initialize selectedIndex if needed
    console.log(this.tabs);
  }
  onCardClick(card: any) {
    if (card.id == "testExecution") {
      window.open(card.linkTo, '_blank');
    } else {
      this.router.navigate([card.linkTo]);
    }

  }
}
