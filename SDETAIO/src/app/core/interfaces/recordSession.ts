export type testCaseEventDataType = {
  sequenceNumber: any;
  eventId: string,
  absoluteXPath: string;
  action: string;
  relationalXPath: string;
  relativeXPath: string;
  type: string;
  value: string;
  assertion: boolean;
  assertionStatus: boolean;
  autohealed: boolean;
  createdAt?: number[];
  readableCreatedAt?: string;
};

export type formElementsType = {
  eventId: string,
  relativeXPath: string | undefined,
  action: string | undefined,
  type: string | undefined,
  value: string | undefined,
  absoluteXPath: string | undefined,
  relationalXPath: string | undefined,
  assertion: boolean | undefined
}