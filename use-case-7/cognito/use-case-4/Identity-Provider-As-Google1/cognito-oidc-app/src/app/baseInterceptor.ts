import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class BaseInterceptor implements HttpInterceptor {

        intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
                 
                let url = req.url;
                console.log('1 url is ',url);
  // if it's NOT already absolute (i.e. doesn't start with http:// or https://), prefix it:
  if (!/^https?:\/\//i.test(url)) {
    // ensure exactly one “/” between host and path:
    const host = 'http://localhost:8080'.replace(/\/$/, '');
    const path = url.replace(/^\//, '');
    url = `${host}/${path}`;
    console.log('2 url is ',url);
  }

        const updatedRequest = req.clone({ url });
        console.log('updatedRequest ',updatedRequest);
        return next.handle(updatedRequest);
        }        
}