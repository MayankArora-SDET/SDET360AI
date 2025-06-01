export interface PieChartData {
  name: string;
  value: number;
}

export interface testCaseVerticalData {
  [key: string]: { name: string; value: number }[]
}
export type testExecutionOverTimeDataInterface = {
  [key: string]: {
    name: string;
    series: { name: string; value: number }[];
  }[];
};
