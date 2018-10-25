package com.jacsimm.model

import org.springframework.boot.autoconfigure.domain.EntityScan

import scala.beans.BeanProperty


case class DocumentsRelation(@BeanProperty val documentA : Long,
                             @BeanProperty val documentB : Long,
                             @BeanProperty val jaccardIndex : Float)
