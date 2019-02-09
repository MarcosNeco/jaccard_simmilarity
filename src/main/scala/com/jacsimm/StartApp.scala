package com.jacsimm

import com.jacsimm.consumer.ConsumerDocView
import org.springframework.boot.{CommandLineRunner, SpringApplication}
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class StartApp

object StartApp extends CommandLineRunner {
  override def run(args: String*): Unit = {
    SpringApplication.run(classOf[StartApp])
    ConsumerDocView.startConsume
  }
}
