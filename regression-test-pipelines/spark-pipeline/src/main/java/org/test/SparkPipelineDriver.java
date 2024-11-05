package org.test;

/*-
 * #%L
 * regressionTestProject::Pipelines::Spark Pipeline
 * %%
 * Copyright (C) 2021 Booz Allen
 * %%
 * All Rights Reserved. You may not copy, reproduce, distribute, publish, display,
 * execute, modify, create derivative works of, transmit, sell or offer for resale,
 * or in any way exploit any part of this solution without Booz Allen Hamilton's
 * express written permission.
 * #L%
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.String;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;
import java.math.RoundingMode;
import java.math.BigDecimal;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import simple.test.record.Person;
import simple.test.record.PersonSchema;

import java.util.concurrent.CompletionStage;
import simple.test.dictionary.*;
import org.apache.commons.lang3.RandomStringUtils;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.spi.CDI;

import org.test.pipeline.PipelineBase;

/**
 * Configures the steps needed to run a Spark-based implementation for SparkPipeline.
 *
 * This pipeline serves the following purpose: ${pipeline.description}.
 *
 * Please **DO** modify with your customizations, as appropriate.
 *
 * Originally generated from: templates/pipeline.driver.impl.java.vm 
 */
public class SparkPipelineDriver extends SparkPipelineBaseDriver {

  private static final Logger logger = LoggerFactory.getLogger(SparkPipelineDriver.class);
  
  public static void main(String[] args) {
    logger.info("STARTED: {} driver", "SparkPipeline");
    SparkPipelineBaseDriver.main(args);

    PipelineBase.getInstance().recordPipelineLineageStartEvent();
    Set<Person> input = new HashSet<>();
    Random rd = new Random();
    Person person;
    for (var i=1; i<11; i++) {
      person = new Person();
      person.setEmployment("Student" + i);
      person.setName("John Smith " + i);
      person.setSsn("111-222-000" + i);
      int age = (i*6) % 11;
      person.setAge(new AgeType(age));
      person.setStringLength5To12(new StringWithValidation(RandomStringUtils.random((i*6) % 18, true, true)));
      person.setLong2000To3000(new LongWithValidation(2000 + rd.nextLong(1050) + 350));
      person.setShort100To200(new ShortWithValidation(Short.valueOf("170", 10)));
      BigDecimal bigDecimal = new BigDecimal(10 + rd.nextDouble(11));
      person.setDecimal10To20(new DecimalWithValidation(bigDecimal.setScale(2, RoundingMode.HALF_UP)));
      person.setDouble20To30(new DoubleWithValidation(20+ rd.nextDouble(12)));
      person.setFloat30To40(new FloatWithValidation(30 + rd.nextFloat(13)));
      try {
        person.validate();
      } catch (Exception e) {
        logger.info("Person: " + person.getName() + "; error: " + e.getMessage());
      }
      input.add(person);
    }

    // Make sure we have at least one valid person
    person = new Person();
    person.setEmployment("Student");
    person.setName("John Smith");
    person.setSsn("111-222-0000");
    person.setAge(new AgeType(10));
    person.setStringLength5To12(new StringWithValidation("abc123xyz"));
    person.setLong2000To3000(new LongWithValidation(2000 + rd.nextLong(1000)));
    person.setShort100To200(new ShortWithValidation(Short.valueOf("170", 10)));
    BigDecimal bigDecimal = new BigDecimal(10 + rd.nextDouble(10));
    person.setDecimal10To20(new DecimalWithValidation(bigDecimal.setScale(2, RoundingMode.HALF_UP)));
    person.setDouble20To30(new DoubleWithValidation(20 + rd.nextDouble(10)));
    person.setFloat30To40(new FloatWithValidation(30 + rd.nextFloat(10)));
    input.add(person);

    final Transform transform = CDI.current().select(Transform.class, new Any.Literal()).get();
    Set<Person> transformResult = transform.executeStep(input);
    PipelineBase.getInstance().recordPipelineLineageCompleteEvent();
  }
}
