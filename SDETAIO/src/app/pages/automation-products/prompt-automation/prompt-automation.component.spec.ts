import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PromptAutomationComponent } from './prompt-automation.component';

describe('PromptAutomationComponent', () => {
  let component: PromptAutomationComponent;
  let fixture: ComponentFixture<PromptAutomationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PromptAutomationComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PromptAutomationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
