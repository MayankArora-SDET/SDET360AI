import { Directive, HostListener } from "@angular/core";
@Directive({
    selector: '[appDocumentListener]',
    standalone: true
})
export class AppDocumentListener {
    @HostListener('document:click', ['$event'])
    onDocumentClick() {
        console.log("document clicked")
    }
}