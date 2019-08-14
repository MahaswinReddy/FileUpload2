package com.intuit.serviceImpl;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.intuit.exception.StorageException;

@Service
public class StorageServiceImpl {
	

	  @Value("${upload.path}")
	   private String path;
	  @Value("${folder.depth}")
	  private int depth;
	 //private final static int depth = 1;
	    
	   // String path = System.getProperty("upload.path");
	    public void uploadFile(MultipartFile file) {

	        if (file.isEmpty()) {
	            throw new StorageException("Failed to store empty file");
	        }

	        try {
	            String fileName = file.getOriginalFilename();
	          
	            Files.copy(file.getInputStream(), Paths.get(path + fileName),
	                    StandardCopyOption.REPLACE_EXISTING);
	           // .REPLACE_EXISTING
	        } catch (IOException e) {

	            String msg = String.format("Failed to store file", file.getName());

	            throw new StorageException(msg, e);
	        }

	}
	    
	   
	    
	// delete File from directory

	public Boolean delete(String fileName) {

		Path fileUri = Paths.get(path + fileName);

		Boolean FileDeletionStatus = false;
		try {
			FileDeletionStatus = Files.deleteIfExists(fileUri);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return FileDeletionStatus;
	     
	}

	//use java.nio.file.Files#walk to list all the files within
	//a directory to a given depth:listFilesUsingFileWalk
	
	
	public Set<Path> fileDownload() throws IOException {
	    try (
	    	Stream<Path> stream = Files.walk(Paths.get(path), depth)) {
	        return stream
	           .filter(file -> !Files.isDirectory(file))
	          .map(Path::toAbsolutePath)
	          .collect(Collectors.toSet());
	        
	    }
	}
	public Set<Path> fileDownload(String fileName) throws IOException {
	    try (
	    	Stream<Path> stream = Files.walk(Paths.get(path), depth)) {
	        return stream
	           .filter(file -> !Files.isDirectory(file))
	          .map(Path::toAbsolutePath)
	          .collect(Collectors.toSet());
	        
	    }
	
	}
	
	public Set<Path> displayFiles(String fileName) throws IOException {
	    try (
	    	Stream<Path> stream = Files.walk(Paths.get(path), depth)) {
	        return stream
	           .filter(file -> !Files.isDirectory(file))
	          .map(Path::toAbsolutePath)
	          .collect(Collectors.toSet());
	        
	    }
	}
	public Set<String> displayFiles() throws IOException {
	    try (Stream<Path> stream = Files.walk(Paths.get(path), depth)) {
	        return stream
	          .filter(file -> !Files.isDirectory(file))
	          .map(Path::getFileName)
	          .map(Path::toString)
	          .collect(Collectors.toSet());
	    }
	}
	public Set<Path> displayFilesPath() throws IOException {
	    try (
	    	Stream<Path> stream = Files.walk(Paths.get(path), depth)) {
	        return stream
	           .filter(file -> !Files.isDirectory(file))
	          .map(Path::toAbsolutePath)
	          .collect(Collectors.toSet());
	        
	    }
	    
	}
	
	
	
	/*
	 * remember to use try-with-resources so the file handle for dir gets closed properly.

Or, if we want to have more control over what happens with each file visited, we can also supply a visitor implementation
	 * public Set<String> listFilesUsingFileWalkAndVisitor(String dir) throws IOException {
    Set<String> fileList = new HashSet<>();
    Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
          throws IOException {
            if (!Files.isDirectory(file)) {
                fileList.add(file.getFileName().toString());
            }
            return FileVisitResult.CONTINUE;
        }
    });
    return fileList;
}
	 
	 */
}
