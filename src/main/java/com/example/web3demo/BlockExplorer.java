package com.example.web3demo;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.codegen.TruffleJsonFunctionWrapperGenerator;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.quorum.Quorum;



@Service
public class BlockExplorer {
	
	@Value("${name:unknown}")
    private String name;

    public String getMessage() {
        return getMessage(name);
    }

    public String getMessage(String name) {
        return "Hello " + name;
    }
    
    public void connectToNetwork() {
    	
    	String txHash = "0x8a24eb91858408eabb980a19257964d7ce3644f5c8c02a49d0be0ad177f70df0";
    	
    	Quorum web3 = Quorum.build(new HttpService("http://13.233.78.97:22000"));
    	//System.out.println(web3.ethGetTransactionReceipt("0xac0b83bf6bb66cb182bf52c8cda280a45c2bbed3bfaa77a5390160e2f4d5b712"));
    	
    	EthGetTransactionReceipt txReceipt = null;

    	try {
    	    txReceipt = web3.ethGetTransactionReceipt(txHash).sendAsync().get();
    	    System.out.println(txReceipt.getResult().getContractAddress());
    	    System.out.println(txReceipt.getResult().getBlockHash());
        	EthBlock block = web3.ethGetBlockByHash(txReceipt.getResult().getBlockHash(), true).sendAsync().get();
        	
        	long timestamp = block.getResult().getTimestamp().longValue();
        	System.out.println(new Date(timestamp*1000));
        	System.out.println(System.currentTimeMillis());
        	 
        
        	//System.out.println(new Date((block.getResult().getTimestamp()).longValue()*1000));
        	//System.out.println(new Date(new Long(7646079494518380872));
        	
        	
    	  }
    	  catch (ExecutionException | InterruptedException e) {
    	    e.printStackTrace();
    	  }
    	web3.shutdown();
    	
    	   	
    
    }
  

	

}
