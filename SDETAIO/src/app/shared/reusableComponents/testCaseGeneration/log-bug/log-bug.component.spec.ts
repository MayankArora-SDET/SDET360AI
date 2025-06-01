import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LogBugComponent } from './log-bug.component';

describe('LogBugComponent', () => {
  let component: LogBugComponent;
  let fixture: ComponentFixture<LogBugComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LogBugComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(LogBugComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
