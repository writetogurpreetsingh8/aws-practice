We have two public EC2 instances: one hosting the web server (Angular) and the other hosting the backend server (Spring Boot).

1) When a user submits a request to add a task, the request will first reach the web server, which will then forward it to the backend server.

2) The backend server will process the request, save the task details into the database, and store the associated task document in an AWS EFS system.

3) In the case of a request to add a new user, the user's avatar will be uploaded to a private S3 bucket. The backend server will then return a URL combining the CloudFront distribution URL with the S3 image path.