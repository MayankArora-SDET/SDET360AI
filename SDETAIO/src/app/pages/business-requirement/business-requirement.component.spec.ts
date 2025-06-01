import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BusinessRequirementComponent } from './business-requirement.component';

describe('BusinessRequirementComponent', () => {
  let component: BusinessRequirementComponent;
  let fixture: ComponentFixture<BusinessRequirementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BusinessRequirementComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BusinessRequirementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
