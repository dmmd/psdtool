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
}

