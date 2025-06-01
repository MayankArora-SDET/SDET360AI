import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SessionLibraryComponent } from './session-library.component';

describe('SessionLibraryComponent', () => {
  let component: SessionLibraryComponent;
  let fixture: ComponentFixture<SessionLibraryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SessionLibraryComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SessionLibraryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
