import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TitleWithBackButtonComponent } from './title-with-back-button.component';

describe('TitleWithBackButtonComponent', () => {
  let component: TitleWithBackButtonComponent;
  let fixture: ComponentFixture<TitleWithBackButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TitleWithBackButtonComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TitleWithBackButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
