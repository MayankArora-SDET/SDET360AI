import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-project-pop-up',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './project-pop-up.component.html',
  styleUrl: './project-pop-up.component.css'
})
export class ProjectPopUpComponent {
  selectedProject = ""

}
