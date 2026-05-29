package backend.controller;

import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostMapping("/create-checkout-session")
    public Map<String, String> createCheckoutSession(
            @RequestBody Map<String, Object> data
    ) throws Exception {

        // SET STRIPE KEY
        Stripe.apiKey = stripeSecretKey;

        double amount = Double.parseDouble(
                data.get("amount").toString()
        );

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)

                        .setSuccessUrl("http://localhost:3000/payment-success")
                        .setCancelUrl("http://localhost:3000/cart")

                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setQuantity(1L)

                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("lkr")

                                                        .setUnitAmount(
                                                                (long) (amount * 100)
                                                        )

                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("FreshCart Order")
                                                                        .build()
                                                        )

                                                        .build()
                                        )

                                        .build()
                        )

                        .build();

        Session session = Session.create(params);

        return Map.of("url", session.getUrl());
    }
}