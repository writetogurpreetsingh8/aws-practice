import { Component, OnInit } from '@angular/core';
import { Event as RouterEvent, NavigationEnd, Router } from '@angular/router';
import { OidcSecurityService } from 'angular-auth-oidc-client';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  standalone:false
})
export class AppComponent implements OnInit{
  title = 'cognito-oidc-app';

  authCheckComplete = false;

  constructor(private oidc: OidcSecurityService, private router: Router) {}
  ngOnInit() {
    console.log('this.router.url ',this.router.url);
    // Listen to navigation events
    this.router.events
    .pipe(
      filter((event: RouterEvent): event is NavigationEnd => event instanceof NavigationEnd)
    )
      .subscribe((event: NavigationEnd) => {
        console.log('Router URL after navigation:', event.urlAfterRedirects);

        if (event.urlAfterRedirects.startsWith('/logged-out')) {
          console.log('On /logged-out page, skipping auth check.');
          this.authCheckComplete = true;
          return;
        }

    // On every app load, check for “code” in the URL; if present, exchange it for tokens.
    this.oidc.checkAuth().subscribe(({ isAuthenticated }) => {

      console.log('Authenticated:', isAuthenticated);
      if (!isAuthenticated) {
        // 👇 Redirect to Cognito login if no tokens found
        this.oidc.authorize(
        //   undefined,{
        //   customParams: {
        //     identity_provider: 'Google',
        //     prompt: 'login'
        //   }
        // }
      );
        this.authCheckComplete = false;
      }
      else {
        this.authCheckComplete = true;
      }
    });
  });
  }
}
