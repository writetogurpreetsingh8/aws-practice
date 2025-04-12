#!/bin/bash

echo "Cleaning up AWS CLI credentials..."
#sometime ~/.aws/credentials stores the credentials/token , so its better so delete this on every run
rm -f /home/ec2-user/.aws/credentials

echo "getting token..."
# Get region from instance metadata
TOKEN=$(curl -X PUT "http://169.254.169.254/latest/api/token" \
  -H "X-aws-ec2-metadata-token-ttl-seconds: 21600")

REGION=$(curl -s -H "X-aws-ec2-metadata-token: $TOKEN" \
  http://169.254.169.254/latest/dynamic/instance-identity/document | jq -r .region)

echo "getting region...$REGION"

# Fetch secret from Secrets Manager
echo "fetching secret-string from secretsmanager"

SECRET_JSON=$(aws secretsmanager get-secret-value \
  --secret-id keys \
  --region "$REGION" \
  --query 'SecretString' \
  --output text)

# Extract access and secret keys
ACCESS_KEY=$(jq -r .access_key <<< "$SECRET_JSON")
SECRET_KEY=$(jq -r .secret_key <<< "$SECRET_JSON")

# Configure AWS CLI
echo "Configure aws cli with secret-string from secretsmanager"
aws configure set aws_access_key_id "$ACCESS_KEY"
aws configure set aws_secret_access_key "$SECRET_KEY"
aws configure set default.region "$REGION"

sudo mkdir -p /home/ec2-user/java
sudo chown -R ec2-user:ec2-user /home/ec2-user/java
cd /home/ec2-user/java

echo "enable and start docker"
sudo systemctl enable docker
sudo systemctl start docker

# Add ec2-user to docker group
echo "add ec2-user to docker group"
usermod -aG docker ec2-user

echo "fetching backend code from s3..."
aws s3 cp s3://backend-app-java/ .  --recursive

echo "login into aws ecr"
aws ecr get-login-password --region "$REGION" | sudo docker login --username AWS --password-stdin 539247483490.dkr.ecr.us-east-1.amazonaws.com

echo "fetching mave image from aws ecr"
sudo docker pull 539247483490.dkr.ecr."$REGION".amazonaws.com/easy-task-repo:mave

echo "fetching jre image from aws ecr"
sudo docker pull 539247483490.dkr.ecr."$REGION".amazonaws.com/easy-task-repo:eclipse-temurin-21-jre-alpine

echo "building image from dockerfile..."
sudo docker build -t backend-app:latest .
sudo docker tag backend-app:latest 539247483490.dkr.ecr."$REGION".amazonaws.com/easy-task-repo:backend-app
sudo docker push 539247483490.dkr.ecr."$REGION".amazonaws.com/easy-task-repo:backend-app

echdo "exeuting docker image to make docker process"

docker run -d -p 80:8080 539247483490.dkr.ecr."$REGION".amazonaws.com/easy-task-repo:backend-app

