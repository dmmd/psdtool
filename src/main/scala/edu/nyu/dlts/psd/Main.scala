package edu.nyu.dlts.psd

import java.io.File
import org.apache.commons.cli.{Options, GnuParser}

object Main extends App with PsdTool {
  val options = new Options
  options.addOption("t", false, "display current time")
  val parser = new GnuParser()
  val cmd = parser.parse( options, args)

  println("psdtool v0.1.0")
  if(cmd.hasOption("t")){println(System.currentTimeMillis())}
  val origFile = new File("src/main/resources/orig.psd")
  val migrateFile = new File("src/main/resources/migrate.psd")
  val results = comparePSDs(origFile, migrateFile)
  results.foreach{entry => println(entry._1 + ": " + entry._2)}
}