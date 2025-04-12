FROM 539247483490.dkr.ecr.us-east-1.amazonaws.com/easy-task-repo:node AS builder
ENV APP_HOME /usr/apps
WORKDIR $APP_HOME
COPY  .  $APP_HOME

#install dependencies
RUN npm install

#build project
RUN npm run build --configuration=production

#serve with nginx
FROM 539247483490.dkr.ecr.us-east-1.amazonaws.com/easy-task-repo:nginx-alpine
COPY --from=builder /usr/apps/dist/essentials/browser/  /usr/share/nginx/html/

# Expose port 80
EXPOSE 80

# Start NGINX
CMD ["nginx", "-g", "daemon off;"]