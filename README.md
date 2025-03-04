package com.test;

import org.web3j.crypto.*;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.Function;
import org.web3j.utils.Numeric;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.Arrays;

public class AlchemyBundler4337WithSignature {
    private static final String ALCHEMY_API_URL = "https://eth-mainnet.alchemyapi.io/v2/YOUR_ALCHEMY_API_KEY";  // Replace with your Alchemy API key

    private static final String BUNDLER_URL = "https://eth-mainnet.alchemyapi.io/v2/YOUR_ALCHEMY_API_KEY";  // Replace with Alchemy's Bundler API URL

    private static final String SENDER_PRIVATE_KEY = "YOUR_PRIVATE_KEY";  // Replace with sender's private key

    public static void main(String[] args) {
        try {
            // Prepare the mint function call data
            String contractAddress = "0xYourContractAddress"; // Replace with actual contract address
            String toAddress = "0xRecipientAddress"; // Replace with the recipient address
            BigInteger mintAmount = BigInteger.valueOf(1000); // Amount to mint (for ERC-20) or tokenId (for ERC-721)

            // Create mint function: For ERC-20 mint(address, uint256)
            Function mintFunction = new Function(
                    "mint", // Name of the function
                    Arrays.asList(
                            new Address(toAddress), // Address to mint to
                            new Uint256(mintAmount) // Amount to mint (or tokenId for ERC-721)
                    ),
                    Arrays.asList() // Output types (not needed for generating callData)
            );

            // Encode function and parameters to generate callData
            String callData = FunctionEncoder.encode(mintFunction);
            System.out.println("Generated callData for minting: " + callData);

            // Prepare the UserOperation data
            JSONObject userOperation = new JSONObject();
            userOperation.put("sender", "0xYourSenderAddress"); // Sender address
            userOperation.put("nonce", "0");  // Nonce (transaction count) for the sender (replace as necessary)
            userOperation.put("initCode", "0x");  // Initialization code (empty for now)
            userOperation.put("callData", callData);  // The callData for the mint function
            userOperation.put("callGasLimit", "100000");  // Gas limit for the call
            userOperation.put("verificationGasLimit", "50000");  // Gas limit for verification
            userOperation.put("preVerificationGas", "21000");  // Pre-verification gas
            userOperation.put("maxFeePerGas", "20000000000");  // Max fee per gas (in Wei)
            userOperation.put("maxPriorityFeePerGas", "1000000000");  // Max priority fee per gas (in Wei)
            userOperation.put("paymaster", "0x");  // No paymaster, set to 0x if not used
            userOperation.put("paymasterData", "0x");  // Paymaster data (if applicable)

            // Generate the UserOperation hash that will be signed
            String userOperationHash = generateUserOperationHash(userOperation);
            System.out.println("Generated UserOperation Hash: " + userOperationHash);

            // Sign the UserOperation hash with the sender's private key
            String signature = signUserOperation(userOperationHash, SENDER_PRIVATE_KEY);
            userOperation.put("signature", signature); // Add the signature to the userOperation

            // Wrap the UserOperation in a JSON array (as the API expects an array of operations)
            JSONArray userOperations = new JSONArray();
            userOperations.put(userOperation);

            // Prepare the request body for the Bundler API
            JSONObject requestBody = new JSONObject();
            requestBody.put("jsonrpc", "2.0");
            requestBody.put("id", 1);
            requestBody.put("method", "eth_sendUserOperation");
            requestBody.put("params", new JSONArray().put(userOperations));

            // Make the HTTP request to the Alchemy Bundler API
            HttpURLConnection connection = (HttpURLConnection) new java.net.URL(ALCHEMY_API_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);
            connection.getOutputStream().write(requestBody.toString().getBytes());

            // Read the response from Alchemy
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Print the response (the transaction hash)
            System.out.println("Response from Alchemy Bundler API: " + response.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to generate the UserOperation hash
    private static String generateUserOperationHash(JSONObject userOperation) {
        // You should hash the relevant fields for the UserOperation (sender, nonce, callData, etc.)
        // For the sake of this example, let's assume we're hashing some of the fields.
        // The real hash computation depends on the ERC-4337 standard and must follow the specified procedure.

        // Concatenate the fields into a string
        String data = userOperation.getString("sender") +
                userOperation.getString("nonce") +
                userOperation.getString("callData") +
                userOperation.getString("callGasLimit") +
                userOperation.getString("verificationGasLimit") +
                userOperation.getString("preVerificationGas") +
                userOperation.getString("maxFeePerGas") +
                userOperation.getString("maxPriorityFeePerGas");

        // Hash the concatenated data (simple SHA-256 hash in this example, but you should follow the ERC-4337 standard)
        return Numeric.toHexString(Sha256Hash.of(data.getBytes()).getBytes());
    }

    // Method to sign the UserOperation hash with the sender's private key
    private static String signUserOperation(String userOperationHash, String privateKey) throws Exception {
        // Load the sender's credentials from the private key
        Credentials credentials = Credentials.create(privateKey);

        // Convert the hash to BigInteger
        BigInteger hashAsBigInteger = new BigInteger(userOperationHash.substring(2), 16);

        // Sign the hash with the sender's private key
        ECKeyPair ecKeyPair = credentials.getEcKeyPair();
        Sign.SignatureData signatureData = Sign.signMessage(hashAsBigInteger.toByteArray(), ecKeyPair);

        // Return the signature in the format expected by ERC-4337
        return Numeric.toHexStringNoPrefix(signatureData.getR()) + Numeric.toHexStringNoPrefix(signatureData.getS()) + Integer.toHexString(signatureData.getV());
    }
}

