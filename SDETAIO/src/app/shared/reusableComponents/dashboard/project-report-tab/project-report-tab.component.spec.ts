import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectReportTabComponent } from './project-report-tab.component';

describe('ProjectReportTabComponent', () => {
  let component: ProjectReportTabComponent;
  let fixture: ComponentFixture<ProjectReportTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectReportTabComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ProjectReportTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
