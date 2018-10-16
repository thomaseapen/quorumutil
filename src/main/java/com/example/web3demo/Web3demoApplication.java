package com.example.web3demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication

public class Web3demoApplication implements CommandLineRunner {

	
	 @Autowired
	 private BlockExplorer blockExplorer;
	 
	


	
	public static void main(String[] args) {
		//SpringApplication.run(Web3demoApplication.class, args);
		SpringApplication app = new SpringApplication(Web3demoApplication.class);	
		
		app.run(args);		
	}
	
	@Override
    public void run(String... args) throws Exception {
		
		
		blockExplorer.connectToNetwork();
		
		/*
        if (args.length > 0) {
            System.out.println(blockExplorer.getMessage(args[0].toString()));
        } else {
            System.out.println(blockExplorer.getMessage());
        }
        
        
       */
    }

}
