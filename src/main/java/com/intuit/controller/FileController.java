package com.intuit.controller;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.intuit.model.Data;
import com.intuit.model.UploadFileResponse;
import com.intuit.s3.UploadFilesToS3Bucket;
import com.intuit.serviceImpl.StorageServiceImpl;

@RestController
public class FileController {
	@Autowired
	StorageServiceImpl storageServiceImpl;
	
	@Autowired
	UploadFilesToS3Bucket uploadFilesToS3Bucket;

	@Value("${upload.path}")
	private String path;

	@PostMapping(value = "/uploaFiles", consumes = { "multipart/form-data" })
	public void uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) throws IOException {

		for (MultipartFile file : files) {
			storageServiceImpl.uploadFile(file);
			System.out.println("fielNames: " + file.getOriginalFilename());

		}
		// return JsonUtility.convertJavaObjectToJson(list);

	}

	@PostMapping(value = "/uploaFilesAndData", consumes = { "multipart/form-data" })
	public void uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, @ModelAttribute("data") Data data)
			throws IOException {
		for (MultipartFile file : files) {
			storageServiceImpl.uploadFile(file);
			System.out.println("fielName: " + file.getOriginalFilename() + " " + data.getUrl() + " " + data.getOrder());
		}
	}

	// Return files as Response
	@PostMapping(value = "/upload", consumes = { "multipart/form-data" })
	public ResponseEntity<List> uploadFileAndData(@RequestParam("files") MultipartFile[] files) throws IOException {
		List<String> list = new ArrayList<>();

		for (MultipartFile file : files) {
			storageServiceImpl.uploadFile(file);
			System.out.println("fielNames: " + file.getOriginalFilename());
			list.add(file.getOriginalFilename());

		}
		return new ResponseEntity<>(list, HttpStatus.CREATED);
		// return JsonUtility.convertJavaObjectToJson(list);

	}

	// file Upload With Response Entity
	// Rest url: http://localhost:9090/fileUploadWithResponseEntity
	@PostMapping(value = "/fileUploadWithResponseEntity", consumes = { "multipart/form-data" })
	public ResponseEntity<List<UploadFileResponse>> upload(@RequestParam("files") MultipartFile[] files,
			@ModelAttribute("data") Data data) throws IOException {
		List<String> list = new ArrayList<>();
		List<UploadFileResponse> responseList = new ArrayList<>();
		String fileUri = "";
		if (files != null && files.length > 0) {
			for (MultipartFile file : files) {
				storageServiceImpl.uploadFile(file);

				System.out.println("fielNames: " + file.getOriginalFilename());
				list.add(file.getOriginalFilename());
				list.add(data.getUrl());
				list.add(data.getOrder());
				fileUri = ServletUriComponentsBuilder.fromCurrentContextPath().path(path)
						.path(file.getOriginalFilename()).toUriString();

				list.add(fileUri);
				UploadFileResponse response = new UploadFileResponse(list.toString(), fileUri, file.getContentType(),
						file.getSize());
				responseList.add(response);

			}
			return new ResponseEntity<>(responseList, HttpStatus.CREATED);

		} else {
			return new ResponseEntity<>(responseList, HttpStatus.NOT_FOUND);
		}

	}

	// Test url using query param
	// Simple File detete by passing File full name
	// http://localhost:9090/fileDelete?fileName=Narasimha_Persistent-JulyNew19.doc
	@DeleteMapping(value = "/delete1")
	public ResponseEntity<Boolean>  FileDelete(@RequestParam("fileName") String fileName) throws IOException {

		Boolean status = storageServiceImpl.delete(fileName);

		return new ResponseEntity<>(status, HttpStatus.OK);

	}

	// Test url using path variable
	// http://localhost:9090/fileDeleteAsPParam/Narasimha_Persistent-JulyNew19.doc
	@DeleteMapping(value = "/delete2/{fileName}")
	public ResponseEntity<Boolean> FileDeleteUsingPathVariable(@PathVariable("fileName") String fileName) throws IOException {

		Boolean status = storageServiceImpl.delete(fileName);

		return new ResponseEntity<>(status, HttpStatus.OK);

	}
	
    //Display Files from the folder
	@GetMapping(value = "/displayFiles")
	public ResponseEntity<Set<String>> DisplayFiles() throws IOException {

		Set<String> filesList = storageServiceImpl.displayFiles();

		return new ResponseEntity<>(filesList, HttpStatus.OK);

	}
	@GetMapping(value = "/fileDownload/{fileName}")
	public ResponseEntity<Set<Path>> fileDownload(@PathVariable("fileName") String fileName) throws IOException {

		Set<Path> filesList = storageServiceImpl.fileDownload(fileName);

		return new ResponseEntity<>(filesList, HttpStatus.OK);

	}
	@GetMapping(value = "/displayFilesPath2")
	public ResponseEntity<Set<Path>> fileDownload2() throws IOException {

		Set<Path> filesList = storageServiceImpl.fileDownload();

		return new ResponseEntity<>(filesList, HttpStatus.OK);

	}
	@GetMapping(value = "/displayFilesPath")
	public Set<Path> fileDownload() throws IOException {

		Set<Path> filesList = storageServiceImpl.fileDownload();

		return filesList;

	}
	
	@PostMapping(value = "/uploadTos3")
	public ResponseEntity<String> UploadFilesToS3() {
		String msg = uploadFilesToS3Bucket.UploadFilesToS3();
		return new ResponseEntity<>( msg, HttpStatus.OK);
	}

}
