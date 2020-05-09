package com.cdeneuve.realestate.infrastructure.source.mongodb;

import com.mongodb.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
public class MongoDBConfig extends AbstractMongoConfiguration {
    private static final String DB = System.getenv("MONGO_REALESTATE_DB_NAME");
    private static final String PWD = System.getenv("MONGO_REALESTATE_DB_PWD");
    private static final WriteConcern writeConcern = WriteConcern.JOURNALED;

    @Value("${mongodb.host}")
    private String host;
    @Value("${mongodb.port}")
    private Integer port;
    @Value("${mongodb.user}")
    private String user;

    @Override
    public MongoClient mongoClient() {
        ServerAddress address = new ServerAddress(host, port);
        MongoCredential credential = MongoCredential.createScramSha1Credential(user, getDatabaseName(),
                                                                               PWD.toCharArray());
        MongoClientOptions options = MongoClientOptions.builder().writeConcern(writeConcern).build();
        MongoClient mongo = new MongoClient(address, credential, options);
        return mongo;
    }

    @Override
    protected String getDatabaseName() {
        return DB;
    }
}
