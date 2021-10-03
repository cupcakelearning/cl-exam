package com.cupcake.learning.exam.util;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class S3Utils {
    @Value("${s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3Client;

    public S3Utils(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
    }

    public String publishFile(String diagramLink) {
        if (!diagramLink.contains(bucket))
            return "";

        var startBucketPos = diagramLink.indexOf(bucket);
        var endBucketPos = diagramLink.substring(startBucketPos).indexOf("/");
        var fileName = diagramLink.substring(startBucketPos + endBucketPos + 1);

        try {
            var publishedFileName = getPublishedFileName(fileName);

            if (!amazonS3Client.doesObjectExist(bucket, publishedFileName)) {
                amazonS3Client.copyObject(
                        bucket, fileName,
                        bucket, publishedFileName);
            }

            return amazonS3Client.getResourceUrl(bucket, publishedFileName);
        } catch (SdkClientException ignored) {
            return "";
        }
    }

    private String getPublishedFileName(String fileName) throws SdkClientException {
        var rawMetadata = amazonS3Client.getObject(bucket, fileName)
                .getObjectMetadata()
                .getRawMetadata();
        var modifiedDateTime = ((Date) rawMetadata.get("Last-Modified"))
                .toInstant()
                .toString();

        String nameWithDateTime;
        if (fileName.contains(".")) {
            var fileExtIndex = fileName.lastIndexOf('.');
            var preExtension = fileName.substring(0, fileExtIndex);
            var fileExtension = fileName.substring(fileExtIndex);
            nameWithDateTime = preExtension + "_" + modifiedDateTime + fileExtension;
        } else {
            nameWithDateTime = fileName + "_" + modifiedDateTime;
        }

        return String.format("%s/%s", "published", nameWithDateTime);
    }
}
