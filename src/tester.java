import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;

public class tester {
	
	public static void main(String[] args) throws IOException{
		String exm = "out13.txt".split("\\.")[0].split("out")[1];
		System.out.println("STRING: "+exm);
		
		
		InputStream inStream = null;
		OutputStream outStream = null;
			
	    	try{
	    		
	    	    File afile =new File("example.txt");
	    	    File bfile =new File("src\\example2.txt");
	    		
	    	    inStream = new FileInputStream(afile);
	    	    outStream = new FileOutputStream(bfile);
	        	
	    	    byte[] buffer = new byte[1024];
	    		
	    	    int length;
	    	    //copy the file content in bytes 
	    	    while ((length = inStream.read(buffer)) > 0){
	    	  
	    	    	outStream.write(buffer, 0, length);
	    	 
	    	    }
	    	 
	    	    inStream.close();
	    	    outStream.close();
	    	    
	    	    //delete the original file
	    	    afile.delete();
	    	    
	    	    System.out.println("File is copied successful!");
	    	    
	    	}catch(IOException e){
	    	    e.printStackTrace();
	    	}
	}
	
	
	
}
