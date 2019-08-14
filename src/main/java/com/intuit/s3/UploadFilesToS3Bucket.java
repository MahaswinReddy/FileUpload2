package com.intuit.s3;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.intuit.serviceImpl.StorageServiceImpl;

@Service
public class UploadFilesToS3Bucket {
	@Autowired
	StorageServiceImpl storageServiceImpl;
	//AWS S3 bucket properties
	@Value("${awsAccessKeyId}")		
	private  String awsAccessKeyId;		
	@Value("${awsSecretAccessKey}")		
	private  String awsSecretAccessKey;	
	@Value("${awsEndPoint}")		
	private  String awsEndPoint;
	@Value("${awsBucketName}")		
	private  String awsBucketName;
	@Value("${awsFolderName}")		
	private  String awsFolderName;	
	
	//Local File system Path
	@Value("${upload.path}")
	private String path;
	 @Value("${folder.depth}")
	private int depth;
	//private final static int depth = 1;
	    
			
	public String UploadFilesToS3() {
		
		AWSCredentials credentials = new BasicAWSCredentials(awsAccessKeyId, awsSecretAccessKey);
		// create client configuration
		ClientConfiguration clientConfig = new ClientConfiguration();
		clientConfig.withSignerOverride("S3SignerType");
		// create S3 client
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
				// consider usage of DefaultAWSCredentialsProviderChain class to avoid credentials hardcoding in the code
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				
				  .withClientConfiguration(clientConfig) // any region can be used
				  .withEndpointConfiguration(new
				  AwsClientBuilder.EndpointConfiguration(awsEndPoint,
				  Regions.AP_SOUTH_1.getName())) .withPathStyleAccessEnabled(true)
				 
				.build();
		try {
			//S3 buckets list
//			 System.out.println("Your buckets:");
//			 for (Bucket bucket : s3Client.listBuckets()) {
//			 System.out.printf(" - %s\n", bucket.getName());
//			 }
			 	
			 
			// System.out.printf("Creating folder %s inside S3 bucket %s...\n", awsFolderName, awsBucketName);
			
			 // Create a folder
			 // Create metadata for your folder and set content-length to 0
//			 ObjectMetadata metadata = new ObjectMetadata();
//			 metadata.setContentLength(0);
//			 // Create empty content
//			 InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
//			 // Create a PutObjectRequest passing the folder name suffixed by /
//			 PutObjectRequest putObjectRequest = new PutObjectRequest(awsBucketName, awsFolderName, emptyContent, metadata);
//			 // Send request to S3 to create a folder
//			 PutObjectResult putObjectResult = s3Client.putObject(putObjectRequest);
//						
//			 System.out.printf("Folder %s was successfully created.\n", awsFolderName, awsBucketName);
//			 System.out.printf("putObjectResult %s.\n", putObjectResult.toString());
//			 
			 
			 
			 try {
				Set<Path> setOfPaths = storageServiceImpl.fileDownload();
				for(Path path:setOfPaths) {
				String fileNameOnS3 = path.getFileName().toString();
				String fileNeedtoBeUploaded = path.toString();
				File file = new File(fileNeedtoBeUploaded);
				s3Client.putObject("borabucket", "https://borabucket.s3.ap-south-1.amazonaws.com/testfolder/"+fileNameOnS3, file);
				System.out.printf("File %s successfully uploaded to %s bucket.\n", fileNameOnS3, awsBucketName);
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			 
			} catch (AmazonServiceException ase) {
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
			} catch (AmazonClientException ace) {
			System.out.println("Error Message: " + ace.getMessage());
			
			}
		return "files succuessfully pushed to S3 bucket";
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
