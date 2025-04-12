An Ec2 private instance need to access S3 service(without internet)

1) we need to have one vpc endpoint for s3 that will provide only connectivity to private ec2 instance to s3 bucket( but doesn't provide the authority to access s3 for that we will need IAM role)

2) we also need to have IAM role with IAM policy to access s2 and attached to ec2 instance

3) if we wish to use/configure aws (like aws configure ) while booting the ec2 instance,  we need to supply access_key and 	secret_key in user-data but just passing access_key and secret_key as plain text in user-data is not secure 
so we will use secret-store for that we need to create secret-store and make two entries for access_key and secret_key

4) Since our ec2-instance having no internet connectivity so we need to provide some sort of communicate between ec2-instance
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

5) Now in order to fetch data from secret-manager we will also need to have IAM role for secret-manager with
policy called ssmanager attached to ec2-instance, just got to policy UI view and type ssmanager and create policy		
		
now try to get secret-store data by using below commands

aws secretsmanager get-secret-value   --secret-id keys   --query 'SecretString'   --output text 
aws secretsmanager get-secret-value   --secret-id keys   --query 'SecretString'   --output text | jq -r .secret_key

Full Script User-Data Script

#!/bin/bash

# Fetch secret from Secrets Manager
SECRET_JSON=$(aws secretsmanager get-secret-value \
  --secret-id keys \
  --query 'SecretString' \
  --output text)

# Extract access and secret keys
ACCESS_KEY=$(echo "$SECRET_JSON" | jq -r .access_key)
SECRET_KEY=$(echo "$SECRET_JSON" | jq -r .secret_key)

# Configure AWS CLI
aws configure set aws_access_key_id "$ACCESS_KEY"
aws configure set aws_secret_access_key "$SECRET_KEY"
aws configure set default.region us-east-1


YT link below
https://www.youtube.com/watch?v=9-Xx7-NkQ2E