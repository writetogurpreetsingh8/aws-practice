import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-home',
  standalone:false,
  template: `
    <h1>Welcome, {{ userName | json }}!</h1>
    <button (click)="getUserData()">Get Some User Data</button>
    <button (click)="logout()">Logout</button>
    <pre *ngIf="userData">{{ userData | json}}</pre>
  `
})
export class HomeComponent {

  userName: string | null = null;
  userData: any;

  constructor(
    private oidc: OidcSecurityService,
    private http: HttpClient
  ) {
    // display the user’s name from the ID token
    this.oidc.userData$.subscribe(u => (this.userName = u.userData || null));
  }

  getUserData() {
    this.http
      .get('/data')
      .subscribe((data) => (this.userData = data));
  }

  logout() {
    this.oidc.logoff();  // clears session and sends you to Cognito’s sign-out URL

    if (window.sessionStorage) {
      window.sessionStorage.clear();
    } 
    window.location.href = "https://us-east-1ljsfw43jf.auth.us-east-1.amazoncognito.com/logout?client_id=4go87kqlajnf047fke8emiofd5&logout_uri=http://localhost:4200/";
  }
}
