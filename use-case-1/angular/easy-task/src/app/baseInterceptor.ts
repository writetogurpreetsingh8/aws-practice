import { HttpHandlerFn, HttpRequest } from "@angular/common/http"
import { environment } from "../environments/environment"

export const baseInterceptor = (req:HttpRequest<any>,next:HttpHandlerFn) => {
        const updatedRequest = req.clone({url:`${environment.apiUrl}${req.url}`})
        return next(updatedRequest);
}