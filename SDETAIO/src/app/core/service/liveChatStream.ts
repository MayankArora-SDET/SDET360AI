import { Injectable, OnDestroy, NgZone } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class EventSourceService implements OnDestroy {
    private messageSubject!: BehaviorSubject<string>;
    public messages$!: Observable<string>; // Observable to expose the messages
    private eventSource: EventSource | null = null; // Store the EventSource instance

    constructor(private ngZone: NgZone) { }

    connect(url: string): void {
        this.messageSubject = new BehaviorSubject<string>(''); // Initialize the BehaviorSubject
        this.messages$ = this.messageSubject.asObservable(); // Expose the observable

        if (this.eventSource) {
            this.eventSource.close(); // Close any existing connection before opening a new one
        }

        this.eventSource = new EventSource(url, { withCredentials: true });

        this.eventSource.onopen = () => {
        };
        this.eventSource.onmessage = (event) => {
            const data = event.data;
            this.ngZone.run(() => {
                console.log('SSE Data:', data); // Debugging log
                if (data !== '[END]') {
                    this.messageSubject.next(data);
                } else {
                    this.closeConnection(); // Properly close connection on END signal
                }
            });
        };

        this.eventSource.onerror = (error) => {
            this.ngZone.run(() => {
                this.closeConnection(); // Properly close on error
            });
        };
    }

    closeConnection(): void {
        if (this.eventSource) {
            this.eventSource.close();
            this.eventSource = null;
        }
        this.messageSubject && this.messageSubject.complete(); // Mark observable as complete
    }

    ngOnDestroy(): void {
        this.closeConnection(); // Cleanup when service is destroyed
    }
}
