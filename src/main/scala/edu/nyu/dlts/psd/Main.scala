package edu.nyu.dlts.psd

import java.io.File

object PsdCompare extends App with PsdMapper {
  //val orig = new File ("src/main/resources/orig.json")
  println("psd compare v0.1.0")
  val orig = parsePSDFile(new File("src/main/resources/provo.psd"))
  val convert = parsePSDFile(parsePSDFile(new File("/src/main/resources/alt.psd")))
  //val ss = collection.immutable.SortedSet[String]() ++ psdfile.layers.keySet
  //ss.foreach{i => println(i)}
  println("done")

}