package com.jacsimm.store

import scala.collection.mutable

case class RelationshipKey(docA: String, docB: String)
case class Relationship(relationshipKey: RelationshipKey, indexSimilarity: Float)

object DocumentsRelationshipStore {

  private val documentsRelation = new mutable.HashMap[RelationshipKey, Relationship]()

  def put(documentRelationship: Relationship)={
    documentsRelation.put(documentRelationship.relationshipKey, documentRelationship)
  }

  def getTopMoreSimilar(top:Int)={

  }

  def addAll(documentsRelationship: Array[Relationship], overwritten:Boolean)={
    if(overwritten){
      documentsRelation.clear()
    }
    addAllAndRecalculateIndex(documentsRelationship)
  }

  def addAllAndRecalculateIndex(documentsRelationship: Array[Relationship])= {
    documentsRelationship.foreach(doc =>{
      if(documentsRelation.contains(doc.relationshipKey)){
        val docRelationAlreadySave = documentsRelation.get(doc.relationshipKey)
        val avgIndexSimilarity = (docRelationAlreadySave.get.indexSimilarity + doc.indexSimilarity) / 2
        val recalculated = Relationship(doc.relationshipKey, avgIndexSimilarity)
        documentsRelation.update(recalculated.relationshipKey, recalculated)
      }else{
        documentsRelation.put(doc.relationshipKey, doc)
      }
    })
  }

  def getAllSaved(): mutable.HashMap[RelationshipKey, Relationship] = {
    documentsRelation.clone()
  }

}