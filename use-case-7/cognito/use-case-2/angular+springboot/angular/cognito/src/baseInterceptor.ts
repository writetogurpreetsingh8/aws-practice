import { inject, Injectable } from '@angular/core';
import {
        HttpClient,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import { catchError, Observable, switchMap, throwError } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';
import { environment } from './environment/environment';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

        isRefreshing = false;
         private httpClient = inject(HttpClient);
        constructor(private cookieService: CookieService) {}

        intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

                const updatedRequest = req.clone({url:`${environment.apiUrl}${req.url}`})

                const accessToken = this.cookieService.get('access_token');
                 // Exclude paths like '/' and '/login'
                const isExcluded = (updatedRequest.url.endsWith('/') 
                || updatedRequest.url.endsWith('/login')
                || updatedRequest.url.endsWith('/refresh-token'));

                if (accessToken && !isExcluded) {
                        const authReq = updatedRequest.clone({
                                headers: updatedRequest.headers.set('Authorization', `Bearer ${accessToken}`)
                        });
                        return next.handle(authReq).pipe(
                                catchError(err => {
                                  if (err.status === 401 && !this.isRefreshing) {
                                    this.isRefreshing = true;
                                     console.log('got 401 unauthorized error');   
                                    const refreshToken = this.cookieService.get('refresh_token');
                                    console.log('old refresh token ',refreshToken);
                                    return this.httpClient.post('/auth/refresh-token', { refresh_token: refreshToken }
                                      ,{withCredentials: true }).pipe(
                                      switchMap(() => {
                                        this.isRefreshing = false;
                                        console.log('new refresh token ',this.cookieService.get('refresh_token'));

                                        const retryReq = authReq.clone({
                                          headers: req.headers.set('Authorization', `Bearer ${this.cookieService.get('access_token')}`)
                                        });
                                        console.log('retry same request with new refresh token');
                                        return next.handle(retryReq);
                                      }),
                                      catchError(refreshErr => {
                                        this.isRefreshing = false;
                                        return throwError(() => refreshErr);
                                      })
                                    );
                                  }
                                  return throwError(() => err);
                                })
                              );;
                }
                return next.handle(updatedRequest);
        }        
}