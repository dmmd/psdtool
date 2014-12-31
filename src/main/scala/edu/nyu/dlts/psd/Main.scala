package edu.nyu.dlts.psd

import java.io.File
import scala.collection.immutable.ListMap

object PsdCompare extends App with PsdMapper {
  println("psdtool v0.1.0")

  val orig = getPSDFile(new File("src/main/resources/orig.psd"))
  val migrate = getPSDFile(new File("src/main/resources/migrate.psd"))
  
  val layers = collection.immutable.SortedSet[String]() ++ orig.layers.keySet
  var resultMap = ListMap.empty[String, Boolean]
  //check the dimensions
  if(orig.width != migrate.width){resultMap += "doc_width" -> false} else resultMap += "doc_width" -> true
  if(orig.height != migrate.height){resultMap += "doc_height" -> false} else resultMap += "doc_height" -> true 
  if(orig.colorSpace != migrate.colorSpace){resultMap += "doc_colorspace" -> false} else resultMap += "doc_colorspace" -> true 

  //check the layers
  layers.foreach{ layer =>
  	if(migrate.layers.contains(layer)){
  		resultMap += (layer + "_exists") -> true
  		val migratedLayer = migrate.layers(layer)
  		val origLayer = orig.layers(layer)
  		if(origLayer.width != migratedLayer.width){resultMap += (layer + "_width") -> false} else resultMap += (layer + "_width") -> true
  		if(origLayer.height != migratedLayer.height){resultMap += (layer + "_height") -> false} else resultMap += (layer + "_height") -> true
  		if(origLayer.left != migratedLayer.left){resultMap += (layer + "_x") -> false} else resultMap += (layer + "_x") -> true
  		if(origLayer.top != migratedLayer.top){resultMap += (layer + "_y") -> false} else resultMap += (layer + "_y") -> true
  	} else {
  	  resultMap += (layer + "_exists") -> false
  	}
  }

  //scan the results  
  //val results = collection.immutable.SortedSet[String]() ++ resultMap.keySet
  resultMap.foreach{entry => println(entry._1 + ": " + entry._2)}
  
}