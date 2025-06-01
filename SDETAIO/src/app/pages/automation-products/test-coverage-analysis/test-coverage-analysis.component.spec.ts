import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestCoverageAnalysisComponent } from './test-coverage-analysis.component';

describe('TestCoverageAnalysisComponent', () => {
  let component: TestCoverageAnalysisComponent;
  let fixture: ComponentFixture<TestCoverageAnalysisComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestCoverageAnalysisComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TestCoverageAnalysisComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
