package com.com.jacsimm.core

import com.holdenkarau.spark.testing.DataFrameSuiteBase
import com.jacsimm.Schemas
import com.jacsimm.core.JaccardSimilarityProcessor
import com.jacsimm.model.{DocumentView, DocumentsRelation}
import com.jacsimm.store._
import org.apache.spark.sql.Row
import org.mockito.Mockito._
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar

class JaccardSimilarityProcessorTest extends FunSuite with
                      MockitoSugar with DataFrameSuiteBase{

  private lazy val emptyDocViewDF = spark.createDataFrame(sc.emptyRDD[Row], Schemas.schemaJaccardCalculated)

  private lazy val historicalData = spark.createDataFrame(List(DocumentsRelation(30, 40, 0.8f)))

  test("valid two docs with total correlation"){
    import sqlContext.implicits._
    val doc1 = DocumentView(30, "marcos")
    val doc2 = DocumentView(40, "marcos")
    val documentsViewRDD = sc.parallelize(List(doc1, doc2)).toDF()

    val storageDocumentsMocked = mock[DocumentsRelationshipStore]
    when (storageDocumentsMocked getAllHistoricalData) thenReturn(emptyDocViewDF)

    val jaccardProcessor = new JaccardSimilarityProcessor(storageDocumentsMocked)
    val jaccardCalculated = jaccardProcessor.process(documentsViewRDD)

    val firstRow = jaccardCalculated.first()
    val jaccardIndex = firstRow.getAs[Float]("jaccardIndex")
    val docA = firstRow.getAs[Long]("docA")
    val docB = firstRow.getAs[Long]("docB")
    assertResult(1.0F) (jaccardIndex)
    assertResult(30) (docA)
    assertResult(40)(docB)
  }

  test("valid two docs with 0.5 correlation"){
    import sqlContext.implicits._
    val doc1 = DocumentView(30, "marcos")
    val doc2 = DocumentView(30, "felipe")
    val doc3 = DocumentView(40, "marcos")
    val doc4 = DocumentView(40, "joao")
    val documentsViewRDD = sc.parallelize(List(doc1, doc2, doc3, doc4)).toDF()

    val storageDocumentsMocked = mock[DocumentsRelationshipStore]
    when (storageDocumentsMocked getAllHistoricalData) thenReturn(emptyDocViewDF)

    val jaccardProcessor = new JaccardSimilarityProcessor(storageDocumentsMocked)
    val jaccardCalculated = jaccardProcessor.process(documentsViewRDD)

    val firstRow = jaccardCalculated.first()
    val jaccardIndex = scale(firstRow.getAs[Float]("jaccardIndex"), 2)
    val docA = firstRow.getAs[Long]("docA")
    val docB = firstRow.getAs[Long]("docB")
    assertResult(0.33F) (jaccardIndex)
    assertResult(30) (docA)
    assertResult(40)(docB)
  }


  test("valid two docs with historical already save and calcule the media"){
    import sqlContext.implicits._
    val doc1 = DocumentView(30, "marcos")
    val doc2 = DocumentView(30, "felipe")
    val doc3 = DocumentView(40, "marcos")
    val doc4 = DocumentView(40, "joao")
    val documentsViewRDD = sc.parallelize(List(doc1, doc2, doc3, doc4)).toDF()

    val storageDocumentsMocked = mock[DocumentsRelationshipStore]

    when (storageDocumentsMocked getAllHistoricalData) thenReturn (historicalData)

    val jaccardProcessor = new JaccardSimilarityProcessor(storageDocumentsMocked)
    val jaccardCalculated = jaccardProcessor.process(documentsViewRDD)

    val firstRow = jaccardCalculated.first()
    val jaccardIndex = scale(firstRow.getAs[Float]("jaccardIndex"), 2)
    val docA = firstRow.getAs[Long]("docA")
    val docB = firstRow.getAs[Long]("docB")
    assertResult(0.57F) (jaccardIndex)
    assertResult(30) (docA)
    assertResult(40)(docB)
  }

  private def scale(value:Float, size:Int)={
    BigDecimal(value).setScale(size, BigDecimal.RoundingMode.HALF_UP).toFloat
  }

}