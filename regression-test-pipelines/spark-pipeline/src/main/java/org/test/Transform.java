package org.test;

/*-
 * #%L
 * regressionTestProject::Pipelines::Spark Pipeline
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * All Rights Reserved. You may not copy, reproduce, distribute, publish, display, 
 * execute, modify, create derivative works of, transmit, sell or offer for resale, 
 * or in any way exploit any part of this solution without Booz Allen Hamilton’s 
 * express written permission.
 * #L%
 */

import java.util.Set;
import simple.test.record.Person;
import simple.test.record.PersonSchema;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Map;

import com.boozallen.aissemble.core.metadata.MetadataModel;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import com.boozallen.aissemble.data.encryption.policy.EncryptionPolicy;
import com.boozallen.aissemble.data.encryption.policy.EncryptionPolicyManager;
import com.boozallen.aissemble.data.encryption.AiopsEncrypt;
import com.boozallen.aissemble.data.encryption.SimpleAesEncrypt;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.functions;

import org.apache.spark.sql.api.java.UDF2;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.lit;

/**
 * Performs the business logic for Transform.
 *
 * Because this class is {@link ApplicationScoped}, exactly one managed singleton instance will exist
 * in any deployment.
 *
 * GENERATED STUB CODE - PLEASE ***DO*** MODIFY
 *
 * Originally generated from: templates/data-delivery-spark/synchronous.processor.impl.java.vm
 */
@ApplicationScoped
public class Transform extends TransformBase {

    private static final Logger logger = LoggerFactory.getLogger(Transform.class);

    public Transform(){
        super("synchronous",getDataActionDescriptiveLabel());
    }

    /**
    * Provides a descriptive label for the action that can be used for logging (e.g., provenance details).
    *
    * @return descriptive label
    */
    private static String getDataActionDescriptiveLabel(){
        // TODO: replace with descriptive label
        return"Transform";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Set<Person> executeStepImpl(Set<Person> inbound) {
        // TODO: Add your business logic here for this step!
        logger.info("Saving People");

        PersonSchema personSchema = new PersonSchema();
        List<Row> rows = inbound.stream().map(PersonSchema::asRow).collect(Collectors.toList());
        Dataset<Row> dataset = sparkSession.createDataFrame(rows, personSchema.getStructType());
        saveDataset(dataset, "People");

        logger.info("Completed saving People");

        try {
            logger.info("Pushing file to S3 Local");
            pushFileToS3(); // Pushes file to FileStore (LocalStack S3)

            logger.info("Fetching file from S3 Local");
            fetchFileFromS3Local(); // Fetches file from FileStore (LocalStack S3)
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Implement executeStepImpl(..) or remove this pipeline step!");
        }

        return null;
    }

    protected void pushFileToS3() {
        // Bucket name
        String container = "test-filestore-bucket";
        String location = "testFileStore.txt";
        Path localPath = Paths.get("src/main/resources/files/testFileStore.txt");

        try {
            this.s3TestModelOneStore.store(container,location, localPath); // Pushed file to FileStore (LocalStack S3)
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	protected void fetchFileFromS3Local() throws IOException  {
        String container = "test-filestore-bucket";
        String location = "testFileStore.txt";
        Path localPath = Paths.get("/opt/spark/work-dir/fetch_TestFileStore.txt");

        createDirectoryIfNotExists(localPath.getParent()); // Ensure the directory exists or create it
        this.s3TestModelOneStore.fetch(container, location, localPath); // Fetches file from FileStore (LocalStack S3)
    }

    protected static void createDirectoryIfNotExists(Path directoryPath) throws IOException {
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
    }

    @Override
    protected MetadataModel createProvenanceMetadata(String resource,String subject,String action){
        // TODO: Add any additional provenance-related metadata here
        return new MetadataModel(resource,subject,action,Instant.now());
    }

    protected Set<Person> checkAndApplyEncryptionPolicy(Set<Person> inbound) {
        // Check Encryption Policy
        Set<Person> datasetWithEncryptionPolicyApplied = inbound;
        EncryptionPolicyManager encryptionPolicyManager = EncryptionPolicyManager.getInstance();
        String filePath = encryptionPolicyManager.getPoliciesLocation();

        if(Files.isDirectory(Paths.get(filePath))) {
            Map<String, EncryptionPolicy> policies = encryptionPolicyManager.getEncryptPolicies();

            if(!policies.isEmpty()) {
                for(EncryptionPolicy encryptionPolicy: policies.values()) {
                    if(stepPhase.equalsIgnoreCase(encryptionPolicy.getEncryptPhase())){
                        List<String> encryptFields = encryptionPolicy.getEncryptFields();
                        List<Row> rows = datasetWithEncryptionPolicyApplied.stream()
                                .map(PersonSchema::asRow)
                                .collect(Collectors.toList());
                        PersonSchema schema = new PersonSchema();
                        Dataset<Row> ds = sparkSession.createDataFrame(rows, schema.getStructType());

                        logger.info("=============== before validation ===================");
                        ds.show(false);

                        logger.info("=============== after validation ===================");
                        ds = schema.validateDataFrame(ds);
                        ds.show(false);
                        List<String> datasetFields = Arrays.asList(ds.columns());
                        List<String> fieldIntersection = encryptFields.stream()
                                .filter(datasetFields::contains)
                                .collect(Collectors.toList());

                        // Registered UDF calls prefer String over Enum
                        String encryptAlgorithm = encryptionPolicy.getEncryptAlgorithm().toString();

                        for(String encryptField: fieldIntersection) {
                            ds = ds.withColumn(encryptField,
                                    functions.callUDF( "encryptUDF", col(encryptField), lit(encryptAlgorithm)));
                        }
                        logger.info("=============== after encryption ===================");
                        ds.show(false);

                        for(String encryptField: fieldIntersection) {
                            ds = ds.withColumn(encryptField,
                                    functions.callUDF( "decryptUDF", col(encryptField), lit(encryptAlgorithm)));
                        }
                        logger.info("=============== after decryption ===================");
                        ds.show(false);
                    }
                }
            }
        }
        return datasetWithEncryptionPolicyApplied;
    }

    protected UDF2<String, String, String> decryptUDF () {
        return (plainText, encryptAlgorithm) -> {
            if (plainText != null) {
                // Default algorithm is AES
                AiopsEncrypt aiopsEncrypt = new SimpleAesEncrypt();

                return aiopsEncrypt.decryptValue(plainText);
            } else {
                return "";
            }
        };
    }
}
