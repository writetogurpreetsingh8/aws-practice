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

9)	Both web application and backend instances fetch their code or assets from Amazon S3 buckets	

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

This layout follows best practices for high availability, scalability, and secure separation of tiers in a cloud-native AWS environment.


