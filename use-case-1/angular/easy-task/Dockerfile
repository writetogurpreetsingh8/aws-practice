FROM node:18-alpine AS builder
ENV APP_HOME /usr/apps
WORKDIR $APP_HOME
COPY  .  $APP_HOME

#install dependencies
RUN npm install

#build project
RUN npm run build --configuration=production

#serve with nginx
FROM nginx:alpine
COPY --from=builder /usr/apps/dist/essentials/browser/  /usr/share/nginx/html/

# Expose port 80
EXPOSE 80

# Start NGINX
CMD ["nginx", "-g", "daemon off;"]