package demo.services;

import java.io.IOException;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import demo.mapper.LoyaltyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    DealsService dealsService;

    @Autowired
    MerchantService merchantService;

    @Autowired
    UserService userService;

    @Autowired
    LoyaltyMapper mapper;

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//          Default Email Structure - Update FROM for Default setting
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

        // Replace sender@example.com with your "From" address.
        // This address must be verified with Amazon SES.
        static String FROM = "sender@example.com";

        // Replace recipient@example.com with a "To" address. If your account
        // is still in the sandbox, this address must be verified.
        static String TO = "recipient@example.com";

        // The configuration set to use for this email. If you do not want to use a
        // configuration set, comment the following variable and the
        // .withConfigurationSetName(CONFIGSET); argument below.


//        static final String CONFIGSET = "ConfigSet";

        // The subject line for the email.
        static String SUBJECT = "Loyalty Bonus";

        // The HTML body for the email.
        static String HTMLBODY = "<h1> Congratulations! You earned a Reward! </h1>"

                // Add the link to the customer loyalty point
                + "<p>This email is a notification to tell you that you have "
                + "reached your loyalty points to earn a free cup of coffee on your next visit.</p>"
                +"<p>Please click below to check out your loyalty points and your next purchase of your free cup of coffee!" +
                "<p> <a href='https://aws.amazon.com/sdk-for-java/'> Checkout your loyalty points here! </a> <p>";


        // The email body for recipients with non-HTML email clients.
        static String TEXTBODY = "You have reached your points to get a free coffee! ";

        // this is the method we want to call
        public static void sendMail(String to, String from, String subject, String body){
            TO = to;
            FROM = from;
            SUBJECT = subject;
            HTMLBODY = body; // update to TEXTBODY if non-HTML
            send();
        }

//        IS THIS NEEDED ANYMORE??
//
//        this is default override, will send and receive to Duncan by default
//        public static void sendMail(){
//                send();
//        }
//

        public static void send() {

            try {
                AmazonSimpleEmailService client =
                        AmazonSimpleEmailServiceClientBuilder.standard()
                                // Replace US_WEST_2 with the AWS Region you're using for
                                // Amazon SES.
                                .withRegion(Regions.US_WEST_2).build();
                SendEmailRequest request = new SendEmailRequest()
                        .withDestination(
                                new Destination().withToAddresses(TO))
                        .withMessage(new Message()
                                .withBody(new Body()
                                        .withHtml(new Content()
                                                .withCharset("UTF-8").withData(HTMLBODY))
                                        .withText(new Content()
                                                .withCharset("UTF-8").withData(TEXTBODY)))
                                .withSubject(new Content()
                                        .withCharset("UTF-8").withData(SUBJECT)))
                        .withSource(FROM);
                        // Comment or remove the next line if you are not using a
                        // configuration set
//                        .withConfigurationSetName(CONFIGSET);
                client.sendEmail(request);
                System.out.println("Email sent!");
            } catch (Exception ex) {
                System.out.println("The email was not sent. Error message: "
                        + ex.getMessage());
            }
        }

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//          Builds Email Structure - uses Loyalty ID
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    // sends email using loyalty ID only
    public void sendRewardEmail(int id) {
            // To, From, Subject, Body
        EmailService.sendMail(
                userEmailAddress(id), // To:
                FROM, // From: Default
                "You have a Reward from " +merchantName(id)+ "!! ", // Subject:
                dealInstructions(id)); // HTML Body:
    }


    // Finds email < User ID < Loyalty ID
    public String userEmailAddress(int id){
        int user_id = mapper.userIdByLoyaltyID(id);
        return userService.userEmail(user_id);
    }

    // Finds deal instructions < Deal ID < Loyalty ID
    public String dealInstructions(int id) {
        int dealID = mapper.getDealsID(id);
        return dealsService.getDealInstructions(dealID);
    }

    // Finds Merchant Name < Merchant ID < Loyalty ID
    public String merchantName(int id) {
        int merchantID = mapper.merchantIDbyLoyaltyID(id);
        return merchantService.merchantNameById(merchantID);
    }
}
