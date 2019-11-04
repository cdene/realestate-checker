package com.cdeneuve.realestate.infrastructure.source.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamoDBConfig {

    @Value("${aws.dynamodb.region}")
    private String region;


    @Bean
    public AmazonDynamoDB dynamoDBClient() {
        return AmazonDynamoDBClientBuilder.standard()
                .withRegion(region)
                .build();
    }

    @Bean
    public DynamoDB dynamoDB() {
        return new DynamoDB(dynamoDBClient());
    }
}
