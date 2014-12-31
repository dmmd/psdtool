package edu.nyu.dlts.psd

import java.io.File
import java.lang.StringBuilder
import org.json4s._
import org.json4s.native.JsonMethods._
import org.json4s.DefaultFormats
import scala.io.Source
import scala.collection.immutable.ListMap

trait PsdTool {
 
  import scala.sys.process._

  implicit val formats = DefaultFormats
  case class Layer(label: String, left: Int, top: Int, width: Int, height: Int)
  case class PsdFile(name: String, path: String, width: Int, height: Int, colorSpace: String, layers: Map[String, Layer])
  
  def getPSDFile(file: File): PsdFile = {
    val psdump = getPsdump(file)
    val exifdump = getExif(file)
    parsePSDFile(psdump, exifdump)
  }

  def parsePSDFile(psdump: String, exifdump: String): PsdFile = {
    val psd = parse(psdump)
    val exif = parse(exifdump)
    val psdFile = new PsdFile(
      (psd \ "name").extract[String],
      (psd \ "name").extract[String].split("/").last,
      (psd \ "width").extract[Int],
      (psd \ "height").extract[Int],
      (exif \ "DeviceModel").extract[String],
      getLayers(psd \ "children")
    )
    psdFile
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

  def getExif(file: File): String = {
    val sb = new StringBuilder  
    val exif = ("exiftool -j " + file.getAbsolutePath) lines_! ProcessLogger(line => ())
    exif.foreach{i => sb.append(i)}
    sb.toString
  }

  def getPsdump(file: File): String = {
    val sb = new StringBuilder
    val psdDump = ("psdump -f json " + file.getAbsolutePath) lines_! ProcessLogger(line => ())
    psdDump.foreach{i => sb.append(i)}
    sb.toString
  }

  def comparePSDs(origPSD: File, migratePSD: File): ListMap[String, Boolean] = {
    
    val orig = getPSDFile(origPSD)
    val migrate = getPSDFile(migratePSD)

    val layers = collection.immutable.SortedSet[String]() ++ orig.layers.keySet
    var resultMap = ListMap.empty[String, Boolean]
  
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
      } else { resultMap += (layer + "_exists") -> false }
    }  
     resultMap
  }
}

