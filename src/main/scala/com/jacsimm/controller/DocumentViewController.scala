package com.jacsimm.controller


import com.jacsimm.Producer.Producer
import com.jacsimm.configuration.Configuration
import com.jacsimm.model.DocumentsRelation
import com.jacsimm.store.DocumentsRelationshipStore
import org.springframework.http.{HttpHeaders, HttpStatus, ResponseEntity}
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._

@Controller
class DocumentViewController {

  @PostMapping(Array(s"/${Configuration.url}/view/{documentId}"))
  @ResponseBody
  def view(@RequestParam(name="user", required = true) userName: String, @PathVariable documentId: Long){
      print("document:"+ documentId + "  user:"+userName)
      Producer.publish(documentId, userName)
      new ResponseEntity[String](userName + " view doc:" + documentId, new HttpHeaders, HttpStatus.ACCEPTED)
  }

  @RequestMapping(path = Array(s"/${Configuration.url}/similar"), method = Array(RequestMethod.GET))
  @ResponseBody
  def getTop10Similar(): java.util.List[DocumentsRelation] ={
     DocumentsRelationshipStore.getInstance().getTop10Similar()
  }

}
