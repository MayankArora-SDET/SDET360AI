import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PerformanceTestingComponent } from './performance-testing.component';

describe('PerformanceTestingComponent', () => {
  let component: PerformanceTestingComponent;
  let fixture: ComponentFixture<PerformanceTestingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PerformanceTestingComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PerformanceTestingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
