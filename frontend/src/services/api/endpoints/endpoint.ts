export type HttpMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';

export interface Endpoint<Req, Res> {
    route: string;
    method: HttpMethod;
    _req?: Req;
    _res?: Res;
}

export type EndpointReq<T> = T extends Endpoint<infer Q, any> ? Q : never;

export type EndpointRes<T> = T extends Endpoint<any, infer R> ? R : never;