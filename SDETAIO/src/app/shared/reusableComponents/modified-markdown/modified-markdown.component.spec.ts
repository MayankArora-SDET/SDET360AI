import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModifiedMarkdownComponent } from './modified-markdown.component';

describe('ModifiedMarkdownComponent', () => {
  let component: ModifiedMarkdownComponent;
  let fixture: ComponentFixture<ModifiedMarkdownComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModifiedMarkdownComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ModifiedMarkdownComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
