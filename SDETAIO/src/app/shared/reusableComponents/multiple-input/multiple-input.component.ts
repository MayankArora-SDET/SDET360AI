import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input } from '@angular/core';
import { Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AlertService } from '../../../core/service/alert.service';
@Component({
  selector: 'app-multiple-input',
  standalone: true,
  imports: [FormsModule, CommonModule],
  providers: [AlertService],
  templateUrl: './multiple-input.component.html',
  styleUrl: './multiple-input.component.css',
})
export class MultipleInputComponent {
  @Output() dataEmitter = new EventEmitter<string[]>();
  constructor(private alertService: AlertService) { }
  @Input() ticketList: string[] = [];
  ticketData = '';
  ticketEntered = '';
  inputEnterClick() {
    if (this.ticketEntered != '') {
      if (this.ticketList.includes(this.ticketEntered)) {
        this.alertService.openAlert({
          message: 'Ticket already present',
          messageType: 'warning',
        });
      } else {
        this.ticketList.push(this.ticketEntered);
        this.dataEmitter.emit(this.ticketList);
        this.ticketEntered = '';
      }
    }
  }
  crossClick(ticket: string) {
    this.ticketList = this.ticketList.filter((item) => item != ticket);
    this.dataEmitter.emit(this.ticketList);
  }
  clearTickets() {
    this.ticketList = [];
    this.dataEmitter.emit(this.ticketList);
  }
}
