import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AutomationTestingComponent } from './automation-testing.component';

describe('AutomationTestingComponent', () => {
  let component: AutomationTestingComponent;
  let fixture: ComponentFixture<AutomationTestingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AutomationTestingComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AutomationTestingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
