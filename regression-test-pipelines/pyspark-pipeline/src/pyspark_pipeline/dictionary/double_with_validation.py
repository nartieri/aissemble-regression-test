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
from ..generated.dictionary.double_with_validation_base import DoubleWithValidationBase


class DoubleWithValidation(DoubleWithValidationBase):
    """
    Dictionary Type to represent doubleWithValidation from PersonDictionary.

    GENERATED STUB CODE - Please **DO** modify with your customizations, as appropriate.

    Originally generated from: templates/data-delivery-data-records/dictionary.type.impl.py.vm
    """

    def __init__(self, value: float):
        super().__init__(value)
