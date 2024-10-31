###
# #%L
# regressionTestProject::Pipelines::Pyspark Pipeline
# %%
# Copyright (C) 2021 Booz Allen
# %%
# All Rights Reserved. You may not copy, reproduce, distribute, publish, display,
# execute, modify, create derivative works of, transmit, sell or offer for resale,
# or in any way exploit any part of this solution without Booz Allen Hamilton’s
# express written permission.
# #L%
###
from ..generated.step.transform_base import TransformBase
from krausening.logging import LogManager
from aissemble_core_metadata.metadata_model import MetadataModel
from ..record.person import Person
from typing import Set
from aissemble_core_filestore.file_store_factory import FileStoreFactory


class Transform(TransformBase):
    """
    Performs the business logic for Transform.

    GENERATED STUB CODE - PLEASE ***DO*** MODIFY

    Originally generated from: templates/data-delivery-pyspark/synchronous.processor.impl.py.vm
    """

    logger = LogManager.get_instance().get_logger("Transform")
    file_stores = {}

    def __init__(self):
        """
        TODO: Configure file store(s)
        In order for the factory to set up your file store, you will need to set a couple of environment
        variables through whichever deployment tool(s) you are using, and in the environment.py file for your tests.
        For more information: https://boozallen.github.io/aissemble/current/file-storage-details.html
        """
        super().__init__("synchronous", self.get_data_action_descriptive_label())
        self.file_stores["s3TestModelOne"] = FileStoreFactory.create_file_store(
            "s3TestModelOne"
        )

    def get_data_action_descriptive_label(self) -> str:
        """
        Provides a descriptive label for the action that can be used for logging (e.g., provenance details).
        """
        # TODO: replace with descriptive label
        return "Transform"

    def execute_step_impl(self, inbound: Set[Person]) -> Set[Person]:
        """
        This method performs the business logic of this step.
        """
        # TODO: Add your business logic here for this step!
        Transform.logger.warn(
            "Implement execute_step_impl(..) or remove this pipeline step!"
        )

        return None
