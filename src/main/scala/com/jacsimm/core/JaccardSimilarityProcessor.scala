package com.jacsimm.core

import com.jacsimm.store.DocumentsRelationshipStore
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.FloatType


class JaccardSimilarityProcessor(storageDocuments: DocumentsRelationshipStore){

  def process(documentsViewRdd: DataFrame): DataFrame ={

    val documentsViewDF = documentsViewRdd.toDF()
    val documentsViewAndTotal = documentsViewDF
           .withColumn("totalDocuments", count("document").over(countTotalByDocument))

         //self join to combine all relation users
    val jaccardCalculatedDf = documentsViewAndTotal.as("docViewLeft")
         .join(documentsViewAndTotal.as("docViewRight"), col("docViewLeft.user") === col("docViewRight.user"))
         .filter(col("docViewLeft.document") < col("docViewRight.document"))
         .groupBy(col("docViewLeft.document").as("docA"), col("docViewRight.document").as("docB"))
         .agg(max("docViewLeft.totalDocuments").as("totalDocA"),
              max("docViewRight.totalDocuments").as("totalDocB"),
              count("*").as("totalInCommon"))
         .withColumn("jaccardIndex", jaccardIndex(col("totalInCommon"), col("totalDocA"), col("totalDocB")))
         .select("docA", "docB", "jaccardIndex")

    val previousProcessingDF = storageDocuments.getAllHistoricalData()
    val recalculatedJaccardIndex = jaccardCalculatedDf
            .union(previousProcessingDF)
            .groupBy("docA", "docB")
            .agg(avg("jaccardIndex").cast(FloatType).as("jaccardIndex"))
     recalculatedJaccardIndex
  }

  private val countTotalByDocument = {
    Window.partitionBy("document").orderBy("document")
  }

  val jaccardIndex = udf((userInCommon: Int, totalDocA: Int, totalDocB: Int) =>  {
    val union = (totalDocA + totalDocB) - userInCommon
    (userInCommon.toFloat / union.toFloat)
  })

}


