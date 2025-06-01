import { Component } from '@angular/core';
import { CardDataService } from '../../core/service/cardData.service';
import { CommonModule } from '@angular/common';
import { MatTab, MatTabGroup } from '@angular/material/tabs';
import { CardTabsComponent } from '../../shared/reusableComponents/card-tabs/card-tabs.component';
@Component({
  selector: 'app-functional-testing',
  standalone: true,
  imports: [CommonModule, MatTabGroup, MatTab, CardTabsComponent],
  templateUrl: './functional-testing.component.html',
  styleUrl: './functional-testing.component.css'
})
export class FunctionalTestingComponent {
  constructor(private cardDataService: CardDataService) { }
  cardData = this.cardDataService.functionalTestingCategoryData;

  tabs = [{ label: "Web Testing", data: this.cardDataService.getItemsByCategory("functionalTesting", "Web Testing") }, { label: "API Testing", data: this.cardDataService.getItemsByCategory("functionalTesting", "API Testing") }, { label: "Database Testing", data: this.cardDataService.getItemsByCategory("functionalTesting", "Database Testing") }];

} 