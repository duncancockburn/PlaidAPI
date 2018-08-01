package demo.model;

import com.plaid.client.PlaidClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PlaidConfiguration {

    @Value("${PLAID_CLIENT_ID ?:5b51290f4ca9fb0011c5bffe }")
    private String plaidClientId;

    @Value("${PLAID_SECRET ?: 846f197e0e89aac5d4e8dcf484c484 }")
    private String plaidSecret;

    @Value("${PLAID_PUBLIC_KEY ?: 3b6e5c84bf8feb3dda6cfdd2f9ff72 }")
    private String plaidPublicKey;

    @Value("#{systemProperties['PLAID_ENV'] ?: 'sandbox'}")
    private String plaidEnv;




    @Bean
    public PlaidClient plaidClient() {
        PlaidClient.Builder clientBuilder = PlaidClient.newBuilder()
                .clientIdAndSecret(plaidClientId, plaidSecret)
                .publicKey(plaidPublicKey);


        switch (plaidEnv.toLowerCase()) {
            case "sandbox":
                clientBuilder.sandboxBaseUrl();
                break;
            case "development":
                clientBuilder.developmentBaseUrl();
                break;
            case "production":
                clientBuilder.productionBaseUrl();
                break;
            default:
                clientBuilder.sandboxBaseUrl();
        }

        return clientBuilder.build();
    }
}
