import { NgModule } from '@angular/core';
import { AuthModule } from 'angular-auth-oidc-client';


@NgModule({
    imports: [AuthModule.forRoot({
        config: {
              authority: 'https://cognito-idp.us-east-1.amazonaws.com/us-east-1_OAVVzRGxx',
              redirectUrl: 'http://localhost:4200/',
              postLogoutRedirectUri: 'http://localhost:4200/',
              clientId: '51ba5cpd7ujctsfq694meok2ee',
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
