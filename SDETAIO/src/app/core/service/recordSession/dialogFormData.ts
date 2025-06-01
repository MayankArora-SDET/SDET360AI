import { Injectable } from "@angular/core";

@Injectable({
    providedIn: 'root'
})
export class dialogFormService {
    htmlActions = [{ name: "Click", value: "click" },
    { name: "Double Click", value: "ondblclick" },
    { name: "Mouse Down", value: "onmouseDown" },
    { name: "Mouse Move", value: "onmouseMove" },
    { name: "Mouse Out", value: "onmouseOut" },
    { name: "Mouse Over", value: "onmouseover" },
    { name: "Mouse Down", value: "onmousedown" },
    ]
    htmlInputTypes = [
        { name: "Button", value: "button" },
        { name: "Checkbox", value: "checkbox" },
        { name: "Color", value: "color" },
        { name: "Date", value: "date" },
        { name: "Datetime-local", value: "datetime-local" },
        { name: "Email", value: "email" },
        { name: "File", value: "file" },
        { name: "Hidden", value: "hidden" },
        { name: "Image", value: "image" },
        { name: "Month", value: "month" },
        { name: "Number", value: "number" },
        { name: "Password", value: "password" },
        { name: "Radio", value: "radio" },
        { name: "Range", value: "range" },
        { name: "Reset", value: "reset" },
        { name: "Search", value: "search" },
        { name: "Submit", value: "submit" },
        { name: "Tel", value: "tel" },
        { name: "Text", value: "text" },
        { name: "Time", value: "time" },
        { name: "URL", value: "url" },
        { name: "Week", value: "week" }
    ];

    gethtmlActions() {
        return this.htmlActions

    }
    gethtmlInputTypes() {
        return this.htmlInputTypes
    }

}