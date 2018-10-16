package com.example.web3demo;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.web3j.codegen.TruffleJsonFunctionWrapperGenerator;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.quorum.Quorum;
import org.web3j.quorum.methods.response.QuorumNodeInfo;

public class Web3Util {

	public static void main(String[] args) {

		Web3Util.generateContractArtifacts();
		//Web3Util.getTransactionTime();
	}

	public static void generateContractArtifacts() {

		String[] contractsArgs = { "generate", "--javaTypes",
				"C:\\truffle\\project1\\build\\contracts\\SettlementContract.json", "-o",
				"C:\\java-workspaces\\web3demo\\src\\main\\java", "-p", "com.example.web3demo.contracts", };

		try {
			TruffleJsonFunctionWrapperGenerator.run(contractsArgs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void getTransactionTime() {

		String txHash = "0xd80d646948f27ae307e083c5932b45fb43efc7ea85c5a10e3062cd222f506515";

		Quorum quorum = Quorum.build(new HttpService("http://13.233.78.97:22000"));
		
		EthGetTransactionReceipt txReceipt = null;

		try {
			txReceipt = quorum.ethGetTransactionReceipt(txHash).sendAsync().get();
			EthBlock block = quorum.ethGetBlockByHash(txReceipt.getResult().getBlockHash(), true).sendAsync().get();
			//QuorumNodeInfo nodeInfo = quorum.quorumNodeInfo().sendAsync().get();
			
			long timestamp = block.getResult().getTimestamp().longValue();
			System.out.println(new Date(timestamp*1000));
		

		} catch (ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
		quorum.shutdown();

	}


}
