import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { LogoutService } from '../logout/logout.service';

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
  accessToken!: string;

  constructor(
    private oidc: OidcSecurityService,
    private http: HttpClient,
    private logoutService:LogoutService
  ) {
    // display the user’s name from the ID token
    this.oidc.userData$.subscribe(u => (this.userName = u.userData || null));
  this.oidc.isAuthenticated$.subscribe((isAuthenticated) => {
    if(isAuthenticated){
      this.oidc.getAccessToken().subscribe((accessToken) => {
        this.accessToken = accessToken;
        console.log('accessToken - ',this.accessToken);
      });
    }
  })
  }

  getUserData() {
    this.http
      .get('/data')
      .subscribe((data) => (this.userData = data));
  }

  logout() {
    this.oidc.logoff();  // clears session and sends you to Cognito’s sign-out URL

    this.http.post('/logout-me-out', {'accessToken':this.accessToken}).subscribe(() => {
      console.log('GlobalSignOut done');
      this.logout1();
    });   
  }

  logout1() {
    this.logoutService.broadcastLogout();
  }
}
