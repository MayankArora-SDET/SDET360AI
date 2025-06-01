import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardDataService } from '../../core/service/cardData.service';
import { CardTabsComponent } from '../../shared/reusableComponents/card-tabs/card-tabs.component';
@Component({
  selector: 'app-automation-testing',
  standalone: true,
  imports: [CardTabsComponent, CommonModule],
  templateUrl: './automation-testing.component.html',
  styleUrl: './automation-testing.component.css'
})
export class AutomationTestingComponent {
  constructor(private cardDataService: CardDataService) { }
  cardData = this.cardDataService.automationCategoryCardData;
  tabs = [{ label: "Web Automation", data: this.cardDataService.getItemsByCategory("automationTesting", "Web Automation") }, { label: "API Testing", data: this.cardDataService.getItemsByCategory("automationTesting", "API Testing") }, { label: "Database Testing", data: this.cardDataService.getItemsByCategory("automationTesting", "Database Testing") }];


}
