package com.jacsimm

import com.jacsimm.core.JaccardSimilarityProcessor
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class StartApp

object StartApp extends App {
  SpringApplication.run(classOf[StartApp])
  JaccardSimilarityProcessor.launcher()
}
