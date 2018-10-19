package com.jacsimm

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class StartApp

object StartApp extends App {
  SpringApplication.run(classOf[StartApp])
}
