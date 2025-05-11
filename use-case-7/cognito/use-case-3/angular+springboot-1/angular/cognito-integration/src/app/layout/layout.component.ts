import { HttpClient } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { OidcSecurityService } from 'angular-auth-oidc-client';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.css']
})
export class LayoutComponent {
  title = 'cognito-integration';

  userData:any;
  isLogout: boolean = false;

  private httpClient = inject(HttpClient);
  private readonly oidcSecurityService = inject(OidcSecurityService);

  getUserData() {
    return this.httpClient.get("/data");
  }

  onClick(){
    this.getUserData().subscribe((result) => {
        this.userData = JSON.stringify(result);
    });  
  }

  logout(){
    // Clear session storage
    if (window.sessionStorage) {
      window.sessionStorage.clear();
    }  
    this.oidcSecurityService.logoff();
    window.location.href = "https://us-east-1oavvzrgxx.auth.us-east-1.amazoncognito.com/logout?client_id=51ba5cpd7ujctsfq694meok2ee&logout_uri=http://localhost:4200/";
  }
}
