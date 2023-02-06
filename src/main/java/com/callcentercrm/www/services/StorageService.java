package com.callcentercrm.www.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.callcentercrm.www.enums.DocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    public String uploadFile(MultipartFile file, String userId, DocumentType documentType) throws IOException {
        File fileObj = convertMultiPartFileToFile(file);
        //String fileExtension = file.getName().substring(file.getName().lastIndexOf("."));
        String fileName = fileObj.getName();
        s3Client.putObject(new PutObjectRequest(bucketName,fileName,fileObj));

        fileObj.delete();
        return fileName;
    }

    public byte[] downloadFile(String fileName) throws IOException {
        S3Object s3Object = s3Client.getObject(bucketName,fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try{
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        }catch (IOException e){
            e.printStackTrace();
        }
    return null;
    }

    public String deleteFile(String fileName){
        s3Client.deleteObject(bucketName,fileName);
        return fileName + " removed...";
    }

    private File convertMultiPartFileToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(file.getOriginalFilename());
        try(FileOutputStream fos = new FileOutputStream(convertedFile)){
            fos.write(file.getBytes());
        }catch (IOException e){
            e.printStackTrace();
        }
        return convertedFile;
    }
}
