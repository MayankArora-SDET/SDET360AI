import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccessibilityTestingComponent } from './accessibility-testing.component';

describe('AccessibilityTestingComponent', () => {
  let component: AccessibilityTestingComponent;
  let fixture: ComponentFixture<AccessibilityTestingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccessibilityTestingComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AccessibilityTestingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
