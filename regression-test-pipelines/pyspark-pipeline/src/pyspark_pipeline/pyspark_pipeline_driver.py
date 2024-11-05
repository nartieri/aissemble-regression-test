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
from pyspark_pipeline.step.ingest import Ingest
from pyspark_pipeline.step.transform import Transform
from threading import Thread
import asyncio
from krausening.logging import LogManager
from pyspark_pipeline.generated.pipeline.pipeline_base import PipelineBase
from pyspark_pipeline.record.person import Person
import random
from regression_test_data_records_python.dictionary.age_type import AgeType
from regression_test_data_records_python.dictionary.decimal_with_validation import (
    DecimalWithValidation,
)
from regression_test_data_records_python.dictionary.double_with_validation import (
    DoubleWithValidation,
)
from regression_test_data_records_python.dictionary.float_with_validation import (
    FloatWithValidation,
)
from regression_test_data_records_python.dictionary.long_with_validation import (
    LongWithValidation,
)
from regression_test_data_records_python.dictionary.short_with_validation import (
    ShortWithValidation,
)
from regression_test_data_records_python.dictionary.string_with_validation import (
    StringWithValidation,
)
from decimal import Decimal, ROUND_HALF_UP

"""
Driver to run the PysparkPipeline.

GENERATED STUB CODE - PLEASE ***DO*** MODIFY

Originally generated from: templates/data-delivery-pyspark/pipeline.driver.py.vm 
"""

logger = LogManager.get_instance().get_logger("PysparkPipeline")


def start_messaging_inbound_step(step):
    """
    Used for starting messaging inbound steps on individual threads to prevent a blocking situation. Override this method to specify your own configurations.
    """
    step_execution = Thread(target=step.execute_step)
    step_execution.start()
    logger.info("Step {} started and waiting for messages".format(type(step).__name__))


if __name__ == "__main__":
    logger.info("STARTED: PysparkPipeline driver")
    PipelineBase().record_pipeline_lineage_start_event()

    # Creating a set to hold Person instances
    input_set = set()
    rd = random.Random()

    # Generating persons
    for i in range(1, 11):
        person = Person()
        person.employment = "Student" + str(i)
        person.name = "John Smith " + str(i)
        person.ssn = "111-222-000" + str(i)
        age = (i * 6) % 11
        person.age = AgeType(age)
        random_string = "".join(
            random.choices(
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", k=(i * 6) % 18
            )
        )
        person.string_Length5_To12 = StringWithValidation(random_string)
        person.long2000_To3000 = LongWithValidation(2000 + rd.randint(0, 1050) + 350)
        person.short100_To200 = ShortWithValidation(170)
        big_decimal = Decimal(10 + rd.random() * 11).quantize(
            Decimal("0.00"), rounding=ROUND_HALF_UP
        )
        person.decimal10_To20 = DecimalWithValidation(big_decimal)
        person.double20_To30 = DoubleWithValidation(20 + rd.random() * 12)
        person.float30_To40 = FloatWithValidation(30 + rd.uniform(0, 13))

        try:
            person.validate()
        except Exception as e:
            print(f"Person: {person.name}; error: {e}")

        input_set.add(person)

    # TODO: Execute steps in desired order and handle any inbound and outbound types
    asyncio.run(Ingest().execute_step())
    transform = Transform().execute_step(None)
    PipelineBase().record_pipeline_lineage_complete_event()
