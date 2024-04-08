
export interface CronAttribute {
    expression: string;
    errorMessage: String | null;
}

export interface CronSchedule {
    minute: CronAttribute;
    hour: CronAttribute;
    dayOfMonth: CronAttribute;
    month: CronAttribute;
    dayOfWeek: CronAttribute;
    scheduleExplanation: String;
}
