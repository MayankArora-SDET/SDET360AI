import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AutomationDatabaseTestingComponent } from './automation-database-testing.component';

describe('AutomationDatabaseTestingComponent', () => {
  let component: AutomationDatabaseTestingComponent;
  let fixture: ComponentFixture<AutomationDatabaseTestingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AutomationDatabaseTestingComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AutomationDatabaseTestingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
