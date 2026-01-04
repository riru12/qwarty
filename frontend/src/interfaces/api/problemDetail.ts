export interface ProblemDetail {
    status: number;
    title: string;
    detail?: string;
    errors?: Array<Record<string, string>>;
    [key: string]: any;
}