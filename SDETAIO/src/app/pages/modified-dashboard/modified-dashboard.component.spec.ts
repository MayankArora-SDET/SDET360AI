import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModifiedDashboardComponent } from './modified-dashboard.component';

describe('ModifiedDashboardComponent', () => {
  let component: ModifiedDashboardComponent;
  let fixture: ComponentFixture<ModifiedDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModifiedDashboardComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ModifiedDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
