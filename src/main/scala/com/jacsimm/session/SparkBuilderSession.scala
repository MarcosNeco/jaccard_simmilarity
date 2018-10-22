package com.jacsimm.session

import org.apache.spark.sql.SparkSession

class SparkBuilderSession {

  private var appName = "spark-shell"
  def withAppName(appName: String):SparkBuilderSession = {
    this.appName = appName
    this
  }

  def build() ={
    SparkSession.builder().appName(appName).getOrCreate()
  }

}
