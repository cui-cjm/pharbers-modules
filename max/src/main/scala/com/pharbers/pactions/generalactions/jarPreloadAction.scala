package com.pharbers.pactions.generalactions

import com.pharbers.pactions.actionbase.{NULLArgs, pActionArgs, pActionTrait}
import com.pharbers.spark.phSparkDriver

object jarPreloadAction {
    def apply() : pActionTrait = new jarPreloadAction()
}

class jarPreloadAction extends pActionTrait { //this : pFileSystem =>

    name = "jar loaded"
    override val defaultArgs : pActionArgs = NULLArgs

    lazy val lst =  ("commons-codec-1.5.jar", "./jar/commons-codec-1.9.jar") ::
                    ("dom4j-1.1.jar", "./jar/dom4j-1.1.jar") ::
                    ("xmlbeans-2.3.0.jar", "./jar/xmlbeans-2.3.0.jar") ::
                    ("poi-3.8.jar", "./jar/poi-3.13.jar") ::
                    ("poi-ooxml-3.8.jar", "./jar/poi-ooxml-3.13.jar") ::
                    ("poi-ooxml-schemas-3.8.jar", "./jar/poi-ooxml-schemas-3.13.jar") ::
                    ("xlsx-streamer-1.0.2.jar", "./jar/xlsx-streamer-1.0.2.jar") ::
                    ("pharbers-max-0.1.jar", "./target/pharbers-max-0.1.jar") :: Nil

    override implicit def progressFunc(progress : Double, flag : String) : Unit = {

    }

    override def perform(args : pActionArgs)(implicit f: (Double, String) => Unit) : pActionArgs = {
        val sc = phSparkDriver().sc
        sc.setLogLevel("ERROR")

        lst.foreach { iter =>
            if (!sc.listJars().contains(iter._1))
                sc.addJar(iter._2)
        }

        NULLArgs
    }
}