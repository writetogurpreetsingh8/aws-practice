import { Component, OnInit } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  standalone:false
})
export class AppComponent implements OnInit{
  title = 'cognito-oidc-app';

  authCheckComplete = false;

  constructor(private oidc: OidcSecurityService) {}

  ngOnInit() {
    // On every app load, check for “code” in the URL; if present, exchange it for tokens.
    this.oidc.checkAuth().subscribe(({ isAuthenticated }) => {

      console.log('Authenticated:', isAuthenticated);
      if (!isAuthenticated) {
        // 👇 Redirect to Cognito login if no tokens found
        this.oidc.authorize();
        this.authCheckComplete = false;
      }
      else {
        this.authCheckComplete = true;
      }
    });
  }
}
