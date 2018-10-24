package com.jacsimm.session

import org.apache.spark.sql.SparkSession

class SparkBuilderSession {

  private var appName = "jaccard-index-app"

  def withAppName(appName: String):SparkBuilderSession = {
    this.appName = appName
    this
  }

  def build() ={
    SparkSession.builder()
      .appName(appName)
      .master("local[*]")
      .getOrCreate()
  }

}
