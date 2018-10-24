package com.jacsimm.store

import com.jacsimm.configuration.Configuration
import com.jacsimm.model.DocumentsRelation
import com.jacsimm.session.SparkBuilderSession
import org.apache.spark.sql.DataFrame

case class RelationshipKey(docA: String, docB: String)
case class Relationship(relationshipKey: RelationshipKey, indexSimilarity: Float)

object DocumentsRelationshipStore {

  private lazy val spark = getSparkSession()

  def getSparkSession()={
    new SparkBuilderSession()
      .withAppName("spark-store")
      .build()
  }

  def getTopMoreSimilar(top:Int)={
    getAllHistoricalData().orderBy("jaccardIndex")
      .limit(top)
    .map(row => DocumentsRelation(row.getAs[Long]("docA"), row.getAs[Long]("docB"), row.getAs[Float]("jaccardIndex")))
    .collect()
  }

 def storeOrUpdate(dataFrame: DataFrame)= {
    dataFrame.createOrReplaceTempView(Configuration.jaccardSimilarityTmpTable)
  }

  def getAllHistoricalData(): DataFrame = {
    spark.sql(s"select * from ${Configuration.jaccardSimilarityTmpTable}")
  }

  def existHistoricData(): Boolean ={
    spark.sqlContext.tableNames().contains(Configuration.jaccardSimilarityTmpTable)
  }



}