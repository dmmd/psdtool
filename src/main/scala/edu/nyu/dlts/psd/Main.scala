package edu.nyu.dlts.psd

import java.io.{ File, PrintWriter }
import org.apache.commons.cli.{Options, GnuParser}

object Main extends App with PsdTool {
  val options = new Options
  options.addOption("v", false, "print the full report")
  options.addOption("o", true, "path to original file")
  options.addOption("m", true, "path to migrated file")
  options.addOption("f", false, "write file to output")
  
  val parser = new GnuParser()
  val cmd = parser.parse(options, args)

  if(!cmd.hasOption("o") || !cmd.hasOption("m")){println("SYNTAX java -jar -o psdfile [original file] -m [migrated file]"); System.exit(1)}

  val origFile = new File(cmd.getOptionValue("o"))
  val migrateFile = new File(cmd.getOptionValue("m"))
  val results = comparePSDs(origFile, migrateFile)
  var result = true
  var errors = Vector.empty[String]
  results.foreach{entry => 
  	if(entry._2 == false){
  		result = false
  		errors = errors ++ Vector(entry._1)
  	}
  }

  println(result)
}