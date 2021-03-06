package com.pharbers.panel.util.xml

import java.io.File
import scala.xml.{Node, XML}
import com.pharbers.panel.util.common.phDataHandleTrait

/**
  * Created by clock on 18-1-3.
  */
trait phHanderXmlTrait extends phDataHandleTrait{
    val configPath: String
    lazy val configDir : String = System.getProperty("user.dir")
    lazy val configFile = XML.loadFile(new File(configDir + "/" + configPath))

    protected def getFirstNode(company: String): Node = {
        (configFile \\ company).toList match {
            case head :: _ => head
            case _ => throw new Exception("read XML error, not found " + company)
        }
    }

    protected def getSecondNode(company: String, mkt: String)= {
        (getFirstNode(company) \ mkt).toList match {
            case head :: _ => (head \\ "@value").toString
            case Nil => throw new Exception("read XML error, not found " + mkt)
        }
    }
}