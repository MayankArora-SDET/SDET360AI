import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
    name: "prettyJson",
    pure: true,
    standalone: true,
})
export class PrettyJsonPipe implements PipeTransform {
    transform(value: any): string {
        try {
            const json = typeof value === "object" ? value : JSON.parse(value);
            return `${JSON.stringify(json, null, 2)}`;
        } catch (e) {
            return `{ "error": "Invalid JSON" }`;
        }
    }



}
