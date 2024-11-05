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
from ..generated.record.person_base import PersonBase


class Person(PersonBase):
    """
    Record to represent Person.

    This record serves the following purpose: Person custom record

    GENERATED STUB CODE - Please **DO** modify with your customizations, as appropriate.

    Originally generated from: templates/data-delivery-data-records/record.impl.py.vm
    """

    def __init__(self):
        """
        Default constructor for this record.
        """
        super().__init__()
