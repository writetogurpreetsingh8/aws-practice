Cloud Native Multi-Tier Architecture Summary – makeitcount.site

1)	Users access the application via the domain makeitcount.site

2)	Amazon Route 53 is used to handle DNS resolution and route the traffic to the appropriate resources

3)	The request first reaches a public Application Load Balancer (ALB) deployed in a public subnet

4)	The public ALB forwards incoming HTTPS requests to a web application layer consisting of:

5)	An Auto Scaling Group managing two private EC2 instances in private subnets

6)	Each EC2 instance runs the web application inside a Docker container, served by NGINX

7)	When the private EC2 web application instances (NGINX) receive a request, they proxy it to an internal (private) ALB

8)	The internal ALB, deployed in private subnets, forwards the request to a backend service layer, which includes:

	8.1) An Auto Scaling Group managing two private EC2 backend instances, also deployed in private subnets

	8.2) Backend instances run the backend service in Docker containers
	8.3) Both backend instance communicate to private RDS

9)	Both web application and backend instances fetch their code or assets from Amazon S3 buckets
10) EventBus trigger the Lambda named easy-task-retrieve on every 1 min, the lambda makes connection
    to secrete-manager to access the db-user,db-url and db-password and connect with the RDS to get the records
    if records retreive successfully than lambda put the result into SQS and finished.

    We have another lambda named process-SQS whenever sqs receives the data it triggers the lambda(process-SQS).
    This lambda the lambda makes connection to secrete-manager to access the db-user,db-url and db-password and connect with the RDS to fire update query in it and finished
	
11) Whenever user upload the task document it will be saved inside EFS

12) Whenever new user will be created its avatar will be saved inside AWS S3 private bucket but on the UI it will be 
    accessed by CloudFront instead of bucket

13) When we upload csv file into AWS S3 bucket a lambda (parse-csv)gets triggered and save the csv data into RDS
    for that lambda makes connection to secrete-manager to access the db-user,db-url and db-password and connect with the RDS to persist the csv data into it and finished	

Any HTTP request is automatically redirected to HTTPS by Public ALB

This architecture ensures secure, scalable, and highly available application deployment across the AWS infrastructure
================================================================================================================================


Cloud Multi-Tier Architecture Components
1)	Virtual Private Cloud (VPC)

	1.1) Create a custom VPC

	1.2) Attach an Internet Gateway (IGW) to enable public internet access for public subnets

2)	Subnets (8 total)

	2.1) Create 8 subnets across multiple Availability Zones (AZs) for high availability

3)	Use:

	3.1) 4 private subnets for the web application servers

	3.2) 4 private subnets for the backend servers

	3.2) Ensure that each subnet is placed in a separate AZ (if available) to maximize fault tolerance

4)	NAT Gateway

	4.1) Deploy one NAT Gateway in any public subnet

	4.2) Update the route tables of all private subnets to route internet-bound traffic through this NAT Gateway

5)	Public Application Load Balancer (ALB)

	5.1) Create a Public ALB deployed across two public subnets

	5.2) Acts as the entry point for all external traffic to the application

	5.3) Forward traffic to web application EC2 instances hosted in two private subnets, managed by an Auto Scaling Group

6)	Internal (Private) Application Load Balancer (ALB)

	6.1) Deploy an Internal ALB across two private subnets

	6.2) Receives traffic from the web application layer

	6.3) Forwards requests to the backend EC2 instances, also deployed across two private subnets

	6.4) Backend instances are managed by a separate Auto Scaling Group

7)	Amazon S3

	7.1) Use Amazon S3 as a centralized repository for storing both frontend (web app) and backend code

	7.2) All EC2 instances (both web and backend layers) will fetch application resources from S3 during startup or deployment
	
8)  RDS 
    
    RDS will be placed inside private subnet

9)  All the 3 lambda will be placed inside VPC private subnet, in that subnet we would require NAT because all the private
    Lambda need to make connection with public AWS servics i.e SQS and Secrete-manager 


    In order to establish 3 lambda and these lambda need access of SQS, secrete-manager and S3 so we need to provide some
    permission to our lambdas

    in order to deploy lambda into vpc below persmissions are required
		ec2:CreateNetworkInterface
		ec2:DescribeNetworkInterfaces
		ec2:DeleteNetworkInterface
	 
	in order to provide SQS access below persmission are required ( read, put and delete)
		sqs:DeleteMessage
		sqs:SendMessage
		sqs:GetQueueAttributes
		sqs:ReceiveMessage	
	 
	in order to provide secrete-manager access below persmission are required 
		secretsmanager:GetResourcePolicy
		secretsmanager:GetSecretValue
		secretsmanager:DescribeSecret
		secretsmanager:ListSecretVersionIds
		secretsmanager:GetRandomPassword
		secretsmanager:ListSecrets
		secretsmanager:BatchGetSecretValue
		
	in order to publish logs into cloud-watch below persmission are required 	
		logs:CreateLogGroup
		logs:CreateLogStream
		logs:PutLogEvents
		
	in order to access/read files from AWS S3 bucket below persmission are required
		s3:GetObject
		kms:Decrypt ( Only needed for encrypted files with SSE-KMS )

This layout follows best practices for high availability, scalability, and secure separation of tiers in a cloud-native AWS environment.


