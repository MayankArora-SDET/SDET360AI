import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectPopUpComponent } from './project-pop-up.component';

describe('ProjectPopUpComponent', () => {
  let component: ProjectPopUpComponent;
  let fixture: ComponentFixture<ProjectPopUpComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectPopUpComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ProjectPopUpComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
