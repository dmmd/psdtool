package edu.nyu.dlts.psd

import java.io.File
import java.lang.StringBuilder
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.DefaultFormats
import scala.io.Source


trait PsdMapper {
 
  import scala.sys.process._

  implicit val formats = DefaultFormats
  case class Layer(label: String, left: Int, top: Int, width: Int, height: Int)
  case class PsdFile(name: String, path: String, width: Int, height: Int, layers: Map[String, Layer])
  
  def parsePSDFile(file: File): PsdFile = {
    val sb = new StringBuilder
    val psdDump = ("psdump -f json " + file.getAbsolutePath) lines_! ProcessLogger(line => ())
    psdDump.foreach{i => sb.append(i)}
    getPSDFile(sb.toString) 
  }

  def getPSDFile(psdString: String): PsdFile = {
    val f = parse(psdString)
    val psd = new PsdFile(
      (f \ "name").extract[String],
      (f \ "name").extract[String].split("/").last,
      (f \ "width").extract[Int],
      (f \ "height").extract[Int],
      getLayers(f \ "children")
    )
    psd
  }

  def getLayers(children: JValue): Map[String, Layer] = {
    var map = Map.empty[String, Layer]
    children.extract[List[JValue]].foreach{i =>
      val layer = new Layer(
        (i \ "layer").extract[String],
        (i \ "left").extract[Int],
        (i \ "top").extract[Int],
        (i \ "width").extract[Int],
        (i \ "height").extract[Int]
      )
      map += layer.label -> layer
    }
    map
  }
}

