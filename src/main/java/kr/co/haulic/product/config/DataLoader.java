package kr.co.haulic.product.config;

import kr.co.haulic.product.interaction.domain.InteractionType;
import kr.co.haulic.product.interaction.domain.UserProductInteraction;
import kr.co.haulic.product.interaction.domain.UserProductInteractionRepository;
import kr.co.haulic.product.product.domain.Product;
import kr.co.haulic.product.product.domain.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataLoader {

    @Bean
    @Profile("dev")
    public CommandLineRunner loadSampleData(
        ProductRepository productRepository,
        UserProductInteractionRepository interactionRepository
    ) {
        return args -> {
            log.info("Loading sample data...");

            List<Product> products = Arrays.asList(
                Product.builder().name("Laptop").description("High performance laptop")
                    .price(new BigDecimal("1299.99")).categoryId("electronics").build(),
                Product.builder().name("Mouse").description("Wireless mouse")
                    .price(new BigDecimal("29.99")).categoryId("electronics").build(),
                Product.builder().name("Keyboard").description("Mechanical keyboard")
                    .price(new BigDecimal("89.99")).categoryId("electronics").build(),
                Product.builder().name("Monitor").description("4K monitor")
                    .price(new BigDecimal("399.99")).categoryId("electronics").build(),
                Product.builder().name("Headphones").description("Noise cancelling headphones")
                    .price(new BigDecimal("199.99")).categoryId("electronics").build(),
                Product.builder().name("Desk").description("Standing desk")
                    .price(new BigDecimal("499.99")).categoryId("furniture").build(),
                Product.builder().name("Chair").description("Ergonomic office chair")
                    .price(new BigDecimal("349.99")).categoryId("furniture").build(),
                Product.builder().name("Lamp").description("LED desk lamp")
                    .price(new BigDecimal("39.99")).categoryId("furniture").build()
            );

            for (Product product : products) {
                productRepository.save(product);
            }

            log.info("Created {} products", products.size());

            Random random = new Random();
            String[] userIds = {"user1", "user2", "user3", "user4", "user5"};
            InteractionType[] types = {InteractionType.VIEW, InteractionType.CART, InteractionType.PURCHASE};

            for (String userId : userIds) {
                for (int i = 0; i < 5; i++) {
                    Long productId = (long) (random.nextInt(products.size()) + 1);
                    InteractionType type = types[random.nextInt(types.length)];
                    
                    UserProductInteraction interaction = UserProductInteraction.create(userId, productId, type);
                    interactionRepository.save(interaction);
                }
            }

            log.info("Created sample interactions");
            log.info("Sample data loaded successfully!");
        };
    }
}
