package com.jacsimm

import org.apache.spark.sql.types.{FloatType, LongType, StructField, StructType}

object Schemas {
  def schemaJaccardCalculated = StructType(Array(StructField("docA", LongType),
    StructField("docB", LongType),
    StructField("jaccardIndex", FloatType)))
}
