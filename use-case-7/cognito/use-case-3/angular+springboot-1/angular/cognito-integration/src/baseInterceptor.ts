import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';
import { environment } from './environment/environment';

@Injectable()
export class BaseInterceptor implements HttpInterceptor {

        isRefreshing = false;
        constructor(private cookieService: CookieService) {}

        intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
                 
                let url = req.url;

  // if it's NOT already absolute (i.e. doesn't start with http:// or https://), prefix it:
  if (!/^https?:\/\//i.test(url)) {
    // ensure exactly one “/” between host and path:
    const host = environment.apiUrl.replace(/\/$/, '');
    const path = url.replace(/^\//, '');
    url = `${host}/${path}`;
  }

  const updatedRequest = req.clone({ url });


                const accessToken = this.cookieService.get('access_token');
                 // Exclude paths like '/' and '/login'
                const isExcluded = (updatedRequest.url.endsWith('') 
                        || updatedRequest.url.endsWith('/') 
                || updatedRequest.url.endsWith('/login')
                || updatedRequest.url.endsWith('/refresh-token'));

                // if (accessToken && !isExcluded) {
                //         const authReq = updatedRequest.clone({
                //                 headers: updatedRequest.headers.set('Authorization', `Bearer ${accessToken}`)
                //         });
                //         return next.handle(authReq);
                // }
                return next.handle(updatedRequest);
        }        
}