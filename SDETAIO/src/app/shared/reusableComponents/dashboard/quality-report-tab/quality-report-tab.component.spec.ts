import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QualityReportTabComponent } from './quality-report-tab.component';

describe('QualityReportTabComponent', () => {
  let component: QualityReportTabComponent;
  let fixture: ComponentFixture<QualityReportTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QualityReportTabComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(QualityReportTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
