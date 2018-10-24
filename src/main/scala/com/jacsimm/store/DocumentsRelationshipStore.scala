package com.jacsimm.store

import com.jacsimm.configuration.Configuration
import com.jacsimm.model.DocumentsRelation
import com.jacsimm.session.SparkBuilderSession
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.types._
import scala.collection.JavaConverters._
case class RelationshipKey(docA: String, docB: String)
case class Relationship(relationshipKey: RelationshipKey, indexSimilarity: Float)

object DocumentsRelationshipStore {

  private  val spark = getSparkSession()

  private val schema = StructType(Array(StructField("docA", LongType),
                                        StructField("docB", LongType),
                                        StructField("jaccardIndex", FloatType)))

  private var topSimilar = Array[DocumentsRelation]()

  def storeOrUpdate(dataFrame: DataFrame)= {
    dataFrame.createOrReplaceTempView(Configuration.jaccardSimilarityTmpTable)
    refreshTop10()
  }

  def getAllHistoricalData(): DataFrame = {
    if(existHistoricData())
      spark.sql(s"select * from ${Configuration.jaccardSimilarityTmpTable}")
    else
      spark.createDataFrame(spark.sparkContext.emptyRDD[Row], schema)
  }

  def getTop10Similar() = {
      topSimilar.clone().toList.asJava
  }

  def existHistoricData(): Boolean ={
    spark.sqlContext.tableNames().contains(Configuration.jaccardSimilarityTmpTable)
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