import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TestCaseGenerationComponent } from './test-case-generation.component';

describe('TestCaseGenerationComponent', () => {
  let component: TestCaseGenerationComponent;
  let fixture: ComponentFixture<TestCaseGenerationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TestCaseGenerationComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TestCaseGenerationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
