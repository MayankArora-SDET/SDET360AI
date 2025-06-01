import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecordAndPlayComponent } from './record-and-play.component';

describe('RecordAndPlayComponent', () => {
  let component: RecordAndPlayComponent;
  let fixture: ComponentFixture<RecordAndPlayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecordAndPlayComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RecordAndPlayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
