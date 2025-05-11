import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LayoutComponent } from './layout/layout.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { AuthInterceptor }        from 'angular-auth-oidc-client';
import { CookieService } from 'ngx-cookie-service';
import { BaseInterceptor } from 'src/baseInterceptor';
import { AuthConfigModule } from './auth/auth-config.module';
import { LogoutComponent } from './logout/logout.component';

@NgModule({
  declarations: [
    AppComponent,
    LayoutComponent,
    LogoutComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    AuthConfigModule
  ],
  providers: [
    CookieService, 
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    {
    provide: HTTP_INTERCEPTORS,
    useClass: BaseInterceptor,
    multi: true
  }
],
  bootstrap: [AppComponent]
})
export class AppModule { }
