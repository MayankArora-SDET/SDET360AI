import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecordTabBarComponent } from './record-tab-bar.component';

describe('RecordTabBarComponent', () => {
  let component: RecordTabBarComponent;
  let fixture: ComponentFixture<RecordTabBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecordTabBarComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RecordTabBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
