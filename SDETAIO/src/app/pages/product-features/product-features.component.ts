import { Component } from '@angular/core';

import { CardDataService } from '../../core/service/cardData.service';
import { CardTabsComponent } from '../../shared/reusableComponents/card-tabs/card-tabs.component';

@Component({
  selector: 'app-product-features',
  standalone: true,
  imports: [CardTabsComponent],
  templateUrl: './product-features.component.html',
  styleUrl: './product-features.component.css',
})
export class ProductFeaturesComponent {

  constructor(private cardDataService: CardDataService) { }
  cardData = this.cardDataService.productFeaturesCardData;
  tabs = [{ label: "Product Features", data: this.cardDataService.getItemsByCategory("productFeatures", "Business Requirements") }];

}
