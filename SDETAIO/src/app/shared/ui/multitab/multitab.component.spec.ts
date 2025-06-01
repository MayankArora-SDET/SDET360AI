import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MultitabComponent } from './multitab.component';

describe('MultitabComponent', () => {
  let component: MultitabComponent;
  let fixture: ComponentFixture<MultitabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MultitabComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MultitabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
