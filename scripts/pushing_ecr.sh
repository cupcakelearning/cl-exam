# login to ECR which sets a auth token to the ENV
$(aws ecr get-login --registry-ids 187517626653 --no-include-email --region ap-southeast-1)

# build the container the way you always build it
docker build -t cl/exam:latest -f "./Dockerfile" .

# get your latest image's ID
IMAGE_ID=$(docker images -q cl/exam:latest)
ECR_URL=187517626653.dkr.ecr.ap-southeast-1.amazonaws.com/cl-exam

# tag it
docker tag $IMAGE_ID $ECR_URL:latest

# push it
docker push $ECR_URL:latest