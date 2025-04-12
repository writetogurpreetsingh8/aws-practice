#!/bin/bash

# Log everything
exec > /var/log/user-data.log 2>&1

echo "updating system!"

sudo yum update -y

# Fetch secret from Secrets Manager
echo "fetching secret-string from secretsmanager"

SECRET_JSON=$(aws secretsmanager get-secret-value \
  --secret-id keys \
  --query 'SecretString' \
  --output text)

# Extract access and secret keys
ACCESS_KEY=$(jq -r .access_key <<< "$SECRET_JSON")
SECRET_KEY=$(jq -r .secret_key <<< "$SECRET_JSON")

# Get region from instance metadata
REGION=$(curl -s http://169.254.169.254/latest/dynamic/instance-identity/document | jq -r .region)

# Configure AWS CLI
echo "Configure aws cli with secret-string from secretsmanager"
aws configure set aws_access_key_id "$ACCESS_KEY"
aws configure set aws_secret_access_key "$SECRET_KEY"
aws configure set default.region "$REGION"

sudo systemctl enable docker

sudo systemctl start docker

cd /home/ec2-user/
mkdir -p java && cd java

# Add ec2-user to docker group
echo "add ec2-user to docker group"
usermod -aG docker ec2-user

echo "fetching backend code from s3..."
aws s3 cp s3://backend-app-java/ .  --recursive

echo "building image from dockerfile..."
docker build -t backend-app:latest .

echdo "exeuting docker image to make docker process"

docker run -d -p 80:8080 backend-app:latest
