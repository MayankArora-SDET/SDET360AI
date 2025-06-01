import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardTabsComponent } from './card-tabs.component';

describe('CardTabsComponent', () => {
  let component: CardTabsComponent;
  let fixture: ComponentFixture<CardTabsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CardTabsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CardTabsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
