#!/usr/bin/env bash

# the $BUILD_NUMBER variable is provided by Jenkins via the Environment Variables
zip -r app_exam_v_$BUILD_NUMBER.zip .ebextensions/ Dockerrun.aws.json

# upload the ZIP file to the beanstalk bucket
sudo aws s3 cp ./app_exam_v_$BUILD_NUMBER.zip s3://elasticbeanstalk-ap-southeast-1-187517626653/

# creating a new Beanstalk version from the configuration we uploaded to s3
sudo aws elasticbeanstalk create-application-version \
--application-name cupcakeLearning-exam \
--version-label v$BUILD_NUMBER \
--description="New Version number $BUILD_NUMBER" \
--source-bundle S3Bucket="elasticbeanstalk-ap-southeast-1-187517626653",S3Key="app_exam_v_$BUILD_NUMBER.zip" \
--auto-create-application \
--region=ap-southeast-1

# deploying the new version to the given environment
sudo aws elasticbeanstalk update-environment \
--application-name cupcakeLearning-exam \
--environment-name cupcakeLearning-exam \
--version-label v$BUILD_NUMBER \
--region=ap-southeast-1
