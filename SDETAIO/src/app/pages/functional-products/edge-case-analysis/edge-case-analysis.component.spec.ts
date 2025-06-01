import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EdgeCaseAnalysisComponent } from './edge-case-analysis.component';

describe('EdgeCaseAnalysisComponent', () => {
  let component: EdgeCaseAnalysisComponent;
  let fixture: ComponentFixture<EdgeCaseAnalysisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EdgeCaseAnalysisComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EdgeCaseAnalysisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
