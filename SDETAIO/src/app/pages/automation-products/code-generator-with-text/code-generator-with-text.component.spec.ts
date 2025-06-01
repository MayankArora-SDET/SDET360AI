import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CodeGeneratorWithTextComponent } from './code-generator-with-text.component';

describe('CodeGeneratorWithTextComponent', () => {
  let component: CodeGeneratorWithTextComponent;
  let fixture: ComponentFixture<CodeGeneratorWithTextComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CodeGeneratorWithTextComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CodeGeneratorWithTextComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
