import { HttpHandlerFn, HttpRequest } from "@angular/common/http"
import { environment } from "../environments/environment"
import { LoaderService } from "./loader.service.";
import { inject } from "@angular/core";
import { finalize } from "rxjs";

export const baseInterceptor = (req:HttpRequest<any>,next:HttpHandlerFn) => {
        const loaderService = inject(LoaderService);
        const updatedRequest = req.clone({url:`${environment.apiUrl}${req.url}`})
        queueMicrotask(() => loaderService.show());
        return next(updatedRequest).pipe(finalize(() => {
                loaderService.hide();
        }));
}