###
# #%L
# regressionTestProject::Pipelines::Pyspark Pipeline
# %%
# Copyright (C) 2021 Booz Allen
# %%
# All Rights Reserved. You may not copy, reproduce, distribute, publish, display,
# execute, modify, create derivative works of, transmit, sell or offer for resale,
# or in any way exploit any part of this solution without Booz Allen Hamilton's
# express written permission.
# #L%
###
from ..generated.schema.person_schema_base import PersonSchemaBase


class PersonSchema(PersonSchemaBase):
    """
    PySpark schema for Person.

    This schema serves the following purpose: Person custom record

    GENERATED STUB CODE - Please **DO** modify with your customizations, as appropriate.

    Originally generated from: templates/data-delivery-data-records/pyspark.schema.impl.py.vm
    """

    def __init__(self):
        super().__init__()
