package com.jacsimm.store

import com.jacsimm.Schemas
import com.jacsimm.configuration.Configuration
import com.jacsimm.model.DocumentsRelation
import com.jacsimm.session.SparkBuilderSession
import org.apache.spark.sql.{DataFrame, Row}

import scala.collection.JavaConverters._

case class RelationshipKey(docA: String, docB: String)
case class Relationship(relationshipKey: RelationshipKey, indexSimilarity: Float)

object DocumentsRelationshipStore{
  private val instance = new DocumentsRelationshipStore()
  def getInstance() = instance
}


class DocumentsRelationshipStore  {

  private val spark = getSparkSession()

  private var topSimilar = Array[DocumentsRelation]()

  def storeOrUpdate(dataFrame: DataFrame)= {
    createOrRecreateHistoricalTable(dataFrame)
    refreshTop10()
  }

  def getAllHistoricalData(): DataFrame = {
    spark.sql(s"select * from global_temp.${Configuration.jaccardSimilarityTmpTable}")
  }

  def getTop10Similar(): java.util.List[DocumentsRelation] = {
    topSimilar.clone().toList.asJava
  }

  def createEmptyHistoricalData()={
    val emptyDataFrame = spark.createDataFrame(spark.sparkContext.emptyRDD[Row], Schemas.schemaJaccardCalculated)
    createOrRecreateHistoricalTable(emptyDataFrame)
  }

  def createOrRecreateHistoricalTable(dataFrame: DataFrame): Unit ={
    dataFrame.createOrReplaceGlobalTempView(Configuration.jaccardSimilarityTmpTable)
  }

  private def getSparkSession()={
    new SparkBuilderSession()
      .withAppName("spark-store")
      .build()
  }

  private def getTopMoreSimilar(top:Int)={
    import spark.implicits._

    getAllHistoricalData()
      .orderBy("jaccardIndex")
      .limit(top)
      .map(row => DocumentsRelation(row.getAs[Long]("docA"),
                                    row.getAs[Long]("docB"),
                                    row.getAs[Float]("jaccardIndex"))).collect()
  }

  private def refreshTop10(): Unit ={
    this.topSimilar = this.getTopMoreSimilar(10)
  }

}