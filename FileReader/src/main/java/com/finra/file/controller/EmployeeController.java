package com.finra.file.controller;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.finra.file.db.Database;
import com.finra.file.vo.Employee;

@RestController
@RequestMapping("/")
public class EmployeeController {

	@RequestMapping(path = "/uploadfile", method = RequestMethod.POST)
	public String uploadFileToDB(@RequestParam("file") MultipartFile file,
			RedirectAttributes redirectAttributes) {
		final String uploadingdir = System.getProperty("user.dir") ;
		  String name = file.getOriginalFilename();
		if (!file.isEmpty()) {
			try {
				File localFile = new File(uploadingdir + name);
				 file.transferTo(localFile);
				 // read the contents of the file and insert into db
				 //create the table in the db
				 Database.createTable();
				 Scanner scanner = new Scanner(localFile); 
				 Scanner lineScanner = null;
					int index = 0;
					
					
					while (scanner.hasNextLine()) {
						lineScanner = new Scanner(scanner.nextLine());
						lineScanner.useDelimiter(",");
						Employee emp = new Employee();

						while (lineScanner.hasNext()) {
							String data = lineScanner.next();
							if (index == 0){
								emp.setId(Integer.parseInt(data));
							}
							else if (index == 1){
								emp.setName(data);
							}
							else if (index == 2){
								emp.setTitle(data);
							}
							
							index++;
						}
						index = 0;
						
						//insert in to the table
						Database.insertEmployee(emp);
						
					}
					
					scanner.close();
					lineScanner.close();
					// print the contents of the table on to the console
					List<Employee> empList = Database.selectEmployees();
				//save the db contents on to a file
					
					Path downloadDir = Paths.get(new File(uploadingdir).getParent()+"\\download" );
					System.out.println(downloadDir);
			      //checking if the download directory exists or not
			        if (!Files.exists(downloadDir)) {
			            try {
			                Files.createDirectories(downloadDir);
			            } catch (IOException e) {
			                //fail to create directory
			                e.printStackTrace();
			            }
			        }
			        
			    	File dbFile = new File(downloadDir+"\\" + name);  
			    	
			    	BufferedWriter writer = new BufferedWriter(new FileWriter(dbFile));
			       for(Employee emp : empList){
			    	writer.write(emp.getId()+","+emp.getName()+","+emp.getTitle()+"\n");
			       }
			         
			        writer.close();
				 
				return "You successfully uploaded " + name ;
			} catch (Exception e) {
				e.printStackTrace();
				return "You failed to upload " + name + " => " + e.getMessage();
			}
		} else {
			return "You failed to upload " + name
					+ " because the file was empty.";
		}
		
		
		
		
		
	}
}
