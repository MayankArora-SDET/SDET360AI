
export interface testCaseResponseType {

    aiResponse: string
}

export interface ticketDataType {
    [ticketKey: string]: testCaseResponseType;
};

// For double-nested:
export interface epicTicketDataType {
    [ticketKey: string]: ticketDataType
};

export type NestedObject<T> = {
    [K in keyof T]: T[K] extends object ? NestedObject<T[K]> : T[K];
};

export function isSingleNested(data: any): data is ticketDataType {
    return data && Object.values(data).every(val => val && 'aiResponse' in (val as any));
}

export function isDoubleNested(data: any): data is epicTicketDataType {
    return (
        data &&
        Object.values(data).every(val =>
            val &&
            Object.values(val as any).every(inner => inner && 'aiResponse' in (inner as any))
        )
    );
}
