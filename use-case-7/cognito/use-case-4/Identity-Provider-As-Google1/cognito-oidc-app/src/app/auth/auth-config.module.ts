import { NgModule } from '@angular/core';
import { AuthModule } from 'angular-auth-oidc-client';


@NgModule({
    imports: [AuthModule.forRoot({
        config: {
              authority: 'https://cognito-idp.us-east-1.amazonaws.com/us-east-1_lJsfW43JF',
              redirectUrl: 'http://localhost:4200/',
              postLogoutRedirectUri: 'http://localhost:4200/',
              clientId: '4go87kqlajnf047fke8emiofd5',
              scope: 'email openid phone', // 'openid profile offline_access ' + your scopes
              responseType: 'code',
              silentRenew: true,
              useRefreshToken: true,
              renewTimeBeforeTokenExpiresInSeconds: 30,
              secureRoutes:['/data']
          }
      })],
    exports: [AuthModule],
})
export class AuthConfigModule {}
