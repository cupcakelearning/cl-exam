#!/usr/bin/env bash

VERSION=$(date +%s)

# the $VERSION variable is provided by Jenkins via the Environment Variables
zip -r app_exam_v_$VERSION.zip .ebextensions/ Dockerrun.aws.json

# upload the ZIP file to the beanstalk bucket
sudo aws s3 cp ./app_exam_v_$VERSION.zip s3://elasticbeanstalk-ap-southeast-1-187517626653/

# creating a new Beanstalk version from the configuration we uploaded to s3
sudo aws elasticbeanstalk create-application-version \
--application-name cupcakeLearning-exam \
--version-label v$VERSION \
--description="New Version number $VERSION" \
--source-bundle S3Bucket="elasticbeanstalk-ap-southeast-1-187517626653",S3Key="app_exam_v_$VERSION.zip" \
--auto-create-application \
--region=ap-southeast-1

# deploying the new version to the given environment
sudo aws elasticbeanstalk update-environment \
--application-name cupcakeLearning-exam \
--environment-name cupcakeLearning-exam \
--version-label v$VERSION \
--region=ap-southeast-1
