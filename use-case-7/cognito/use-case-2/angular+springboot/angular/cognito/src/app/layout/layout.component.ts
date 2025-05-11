import { HttpClient } from '@angular/common/http';
import { Component, inject } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-layout',
  templateUrl: './layout.component.html',
  styleUrls: ['./layout.component.css']
})
export class LayoutComponent {
  title = 'cognito';
  userData:any;
  isLogout: boolean = false;

  private httpClient = inject(HttpClient);
  private cookieService = inject(CookieService);

  getUserData() {
    return this.httpClient.get("/data");
  }

  onClick(){
    this.getUserData().subscribe((result) => {
        this.userData = JSON.stringify(result);
    });  
  }

  logout(){
    this.httpClient.get("/logout-me-out",{ responseType: 'text' })
    .subscribe((logoutUrl) =>{
            this.cookieService.deleteAll();
            alert("Your are successfully Logout!");
            window.location.href = logoutUrl;
          });
  }
  
  testRefreshToken(){
    let reft = this.cookieService.get('refresh_token');
    console.log('reft ',reft);
    this.httpClient.post('/auth/refresh-token',{ refresh_token: reft } , {withCredentials: true })
    .subscribe(() =>{
        console.log('refresh token request successfully!');
    });
  }
}
