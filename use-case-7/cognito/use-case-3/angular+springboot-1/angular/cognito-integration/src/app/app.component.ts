import { Component, inject, OnInit } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { first } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  title = 'cognito-integration';

  private readonly oidcSecurityService = inject(OidcSecurityService);
  configuration$ = this.oidcSecurityService.getConfiguration();

  userData$ = this.oidcSecurityService.userData$;

  ngOnInit(): void {
    const url = window.location.href;
    const hasAuthCode = url.includes('?code=') || url.includes('&code=');
    const hasError = url.includes('error=');
    this.oidcSecurityService.checkAuth()
    .pipe(first())
    .subscribe(({isAuthenticated}) => {  
      if(isAuthenticated){
        // remove code & state from the URL bar
      //const url = window.location.origin + window.location.pathname;
      //window.history.replaceState({}, document.title, url);
      }
      if(!isAuthenticated && !hasAuthCode && !hasError){
        //invoke login flow
        this.oidcSecurityService.authorize();
      }
    });
  }
}
