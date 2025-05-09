import { Component, inject } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'no-backend';

  private readonly oidcSecurityService = inject(OidcSecurityService);

  configuration$ = this.oidcSecurityService.getConfiguration();

  userData$ = this.oidcSecurityService.userData$;

  isAuthenticated = false;
  accessToken: any;
  idToken:any;
  ngOnInit(): void {
    console.log("invoking.. oninit...");
    // this.oidcSecurityService.isAuthenticated$.subscribe(
    //   ({ isAuthenticated }) => {
    //     this.isAuthenticated = isAuthenticated;
    //     console.warn('authenticated: ', isAuthenticated);
    //   }
    // );
    this.oidcSecurityService.checkAuth().subscribe(({ isAuthenticated, userData, accessToken,idToken }) => {
      this.isAuthenticated = isAuthenticated;
      console.warn('Auth Check Result:', isAuthenticated);
      console.log('User Data:', userData);
      console.log('Access Token:', accessToken);
      this.accessToken = accessToken;
      this.idToken = idToken;
    });
  }

  login(): void {
    this.oidcSecurityService.authorize();
  }

  logout(): void {
    // Clear session storage
    if (window.sessionStorage) {
      console.log('window.sessionStorage ',window.sessionStorage);
      window.sessionStorage.clear();
    }  
    window.location.href = "https://us-east-1qmldkndwa.auth.us-east-1.amazoncognito.com/logout?client_id=4aul8lfp411crcsnc70ff42kgh&logout_uri=http://localhost:4200";
  }
}
