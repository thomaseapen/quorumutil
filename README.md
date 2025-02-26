# quorumutil


import com.esaulpaugh.headlong.abi.Function;
import com.esaulpaugh.headlong.abi.TypeReference;
import com.esaulpaugh.headlong.abi.Tuple;
import com.esaulpaugh.headlong.abi.Address;
import com.esaulpaugh.headlong.util.UInt256;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class UserOpClient {
    private static final String SENDER_PRIVATE_KEY = "YOUR_PRIVATE_KEY";  // Replace with sender's private key
    private static final String TARGET_CONTRACT_ADDRESS = "0xYourTargetContractAddress";  // Replace with your target contract
    private static final String PAYMASTER_CONTRACT_ADDRESS = "0xYourPaymasterAddress";  // Replace with your Paymaster contract
    private static final String ENTRYPOINT_CONTRACT_ADDRESS = "0xYourEntryPointAddress";  // Replace with your EntryPoint contract

    // Prepare UserOp data and send to Ethereum network via HTTP JSON-RPC
    public static void sendUserOpWithPaymaster() throws Exception {
        // Prepare the UserOp data (contract call, arguments, gas, max fees)
        String functionSignature = "someFunction(uint256)";
        BigInteger gasLimit = BigInteger.valueOf(100000);  // Set gas limit for the transaction
        BigInteger maxPriorityFeePerGas = Convert.toWei("2", Convert.Unit.GWEI).toBigInteger();
        BigInteger maxFeePerGas = Convert.toWei("20", Convert.Unit.GWEI).toBigInteger();
        BigInteger functionArgument = BigInteger.valueOf(42);  // Example argument

        // Step 1: Use Headlong to encode the contract function call with arguments
        Function function = new Function(functionSignature, new TypeReference[]{TypeReference.create(BigInteger.class)});
        byte[] encodedData = function.encodeArguments(functionArgument);

        // Print out the encoded data for the function call (this is the data you'll send to Ethereum)
        System.out.println("Encoded Data: " + Arrays.toString(encodedData));

        // Step 2: Manually sign the transaction (i.e., create a transaction payload)
        String nonce = "0"; // Replace with the actual nonce of your account
        BigInteger gasPrice = maxFeePerGas;  // Set the gas price (we'll use max fee per gas)
        BigInteger chainId = BigInteger.valueOf(1);  // Mainnet chain ID

        // Build the transaction payload (simplified version)
        String txData = buildTransactionData(TARGET_CONTRACT_ADDRESS, encodedData, gasLimit, gasPrice, nonce, chainId);

        // Sign the transaction
        String signedTransaction = signTransaction(txData);

        // Step 3: Send the signed transaction to the Ethereum network via JSON-RPC
        sendTransactionViaHttpJsonRpc(signedTransaction);
    }

    // Build the transaction data (simplified for illustration)
    private static String buildTransactionData(String targetAddress, byte[] data, BigInteger gasLimit,
                                               BigInteger gasPrice, String nonce, BigInteger chainId) {
        // Transaction data (to, gas, nonce, data, etc.) formatted into raw transaction format
        String txData = targetAddress + " " + data.length + " " + gasLimit + " " + gasPrice + " " + nonce + " " + chainId;
        return txData;
    }

    // Sign the transaction with the sender's private key
    private static String signTransaction(String txData) throws Exception {
        // Extract ECKeyPair from the sender's private key
        ECKeyPair keyPair = ECKeyPair.create(new BigInteger(SENDER_PRIVATE_KEY, 16));
        byte[] txDataBytes = txData.getBytes(StandardCharsets.UTF_8);

        // Sign the transaction with the private key
        Sign.SignatureData signature = Sign.signMessage(txDataBytes, keyPair);

        // Combine the signed data (raw transaction + signature)
        byte[] signedTransaction = Arrays.copyOf(txDataBytes, txDataBytes.length + signature.getR().length + signature.getS().length + 1);
        System.arraycopy(signature.getR(), 0, signedTransaction, txDataBytes.length, signature.getR().length);
        System.arraycopy(signature.getS(), 0, signedTransaction, txDataBytes.length + signature.getR().length, signature.getS().length);
        signedTransaction[txDataBytes.length + signature.getR().length + signature.getS().length] = signature.getV()[0];

        return new String(signedTransaction);
    }

    // Send the signed transaction to Ethereum using HTTP JSON-RPC
    private static void sendTransactionViaHttpJsonRpc(String signedTransaction) {
        // You would send the signed transaction to an Ethereum node or service like Infura/Alchemy via HTTP POST request
        // Here's an example of what the payload might look like:
        String jsonRpcPayload = "{\n" +
                "  \"jsonrpc\": \"2.0\",\n" +
                "  \"method\": \"eth_sendRawTransaction\",\n" +
                "  \"params\": [\"" + signedTransaction + "\"],\n" +
                "  \"id\": 1\n" +
                "}";

        // Send the HTTP POST request with the signed transaction (this part is simplified)
        System.out.println("Sending raw transaction: " + jsonRpcPayload);

        // You can use an HTTP library (like HttpURLConnection or Apache HttpClient) to send the request to your Ethereum node (Infura, Alchemy, etc.)
    }

    // Main entry point
    public static void main(String[] args) throws Exception {
        sendUserOpWithPaymaster();
    }
}
