An Ec2 private instance need to access S3 service, secret-manager, aws ECR (without internet & without NAT gateway)

1) we need to have one vpc endpoint for s3 that will provide only connectivity to private ec2 instance to s3 bucket(IAM role to access s3 is not neccesary but in case you face some issue regarding access than you might need to have it )

2) if we wish to use/configure aws (like aws configure ) while booting the ec2 instance,  we need to supply access_key and 	secret_key in user-data but just passing access_key and secret_key as plain text in user-data is not secure 
so we will use secret-store for that we need to create secret-store and make two entries for access_key and secret_key

3) Since our ec2-instance having no internet connectivity so we need to provide some sort of communicate between ec2-instance
and secret-store so we again need to create one more vpc-endpoint
i.e

To allow private EC2 instances (no public IP, no NAT) to access Secrets Manager, create a VPC endpoint

		Steps to Create VPC Endpoint for Secrets Manager
		A) Go to VPC → Endpoints → Create Endpoint
		B) Service category: AWS services
		C) Service name: Select one that looks like:
			com.amazonaws.<region>.secretsmanager
		D) VPC: Choose your VPC

		E) Subnets: Select all subnets your private EC2s are in
		F) Security group: Attach a security group that allows HTTPS (port 443) from the EC2 instance
		G) Policy: Leave as "Full Access" or restrict to your EC2 role if needed  

4) Now in order to fetch data from secret-manager we don't need any IAM role but if we encounter some issue regading access the secretsmanager we might also need to have IAM role for secret-manager with
policy called ssmanager & attached to ec2-instance, just got to policy UI view and type ssmanager and create policy		
		
now try to get secret-store data by using below commands

aws secretsmanager get-secret-value   --secret-id keys   --query 'SecretString'   --output text 
aws secretsmanager get-secret-value   --secret-id keys   --query 'SecretString'   --output text | jq -r .secret_key

5) After all this we need two more vpc endpoint to make communication with AWS ECR 
	A) one for ECR API com.amazonaws.<region>.ecr.api with service type Interface
	B) second for ECR Docker regitory com.amazonaws.<region>.ecr.dkr service type Interface
	C) Security group: Attach a security group that allows HTTPS (port 443) from the EC2 instance
	
	
6) Since we are not using internet and nat gateway that's why we can neither fetch any public docker images from docker hub
    or build maven project because while building maven project we again need internet to fetch project related depedencies
	so we need to first fetch all the public docker images that we want to use and push them to aws ecr and than 
	build our own project's docker image and push it on aws ecr too(to do all this task we need bastion-host)
	
	
YT link below for secret-manager
https://www.youtube.com/watch?v=9-Xx7-NkQ2E