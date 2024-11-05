package org.test;

/*-
 * #%L
 * regressionTestProject::Pipelines::Async Spark Pipeline
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
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;

import java.util.concurrent.CompletionStage;


import org.test.pipeline.PipelineBase;

/**
 * Configures the steps needed to run a Spark-based implementation for AsyncSparkPipeline.
 *
 * This pipeline serves the following purpose: ${pipeline.description}.
 *
 * Please **DO** modify with your customizations, as appropriate.
 *
 * Originally generated from: templates/pipeline.driver.impl.java.vm 
 */
public class AsyncSparkPipelineDriver extends AsyncSparkPipelineBaseDriver {

  private static final Logger logger = LoggerFactory.getLogger(AsyncSparkPipelineDriver.class);
  
  public static void main(String[] args) {
    logger.info("STARTED: {} driver", "AsyncSparkPipeline");
    AsyncSparkPipelineBaseDriver.main(args);

    PipelineBase.getInstance().recordPipelineLineageStartEvent();

    PipelineBase.getInstance().recordPipelineLineageCompleteEvent();
  }
}
