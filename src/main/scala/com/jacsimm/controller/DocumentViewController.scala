package com.jacsimm.controller


import com.jacsimm.Producer.Producer
import com.jacsimm.model.DocumentsRelation
import com.jacsimm.store.DocumentsRelationshipStore
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._

@Controller
class DocumentViewController {

  @PostMapping(Array("/www.globoplay.com/view/{documentId}"))
  @ResponseBody
  def view(@RequestParam(name="user", required = true) userName: String, @PathVariable documentId: Long){
      print("document:"+ documentId + "  user:"+userName)
      Producer.publish(documentId, userName)
      new ResponseEntity[String](userName + " view doc:" + documentId, new HttpHeaders, HttpStatus.ACCEPTED)
  }

  @RequestMapping(value = Array("/www.globoplay.com/similar"), method = Array(RequestMethod.GET))
  def getTop10Similar(): java.util.List[DocumentsRelation] ={
    DocumentsRelationshipStore.getTop10Similar()
  }

}
