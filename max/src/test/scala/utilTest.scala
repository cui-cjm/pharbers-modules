//import java.text.SimpleDateFormat
//import java.util.Date
//
//import com.pharbers.panel.phPanelHeadle
//import org.scalatest.FunSuite
//import play.api.libs.json.{JsString, JsValue}
///**
//  * Created by jeorch on 17-10-26.
//  */
//class utilTest extends FunSuite{
//
//  val args: Map[String, List[String]] = Map(
//    "company" -> List("8ee0ca24796f9b7f284d931650edbd4b"),
//    "uid" -> List("08f1517cd192c5d8f9290c46418e08b1"),
//    "cpas" -> List("171215恩华2017年10月检索.xlsx"),
//    "gycxs" -> List("")
//  )
//
//  test("test calcYM") {
//    val a = phPanelHeadle(args).calcYM.asInstanceOf[JsString].value
//    println(a)
//  }
//
//  test("test generate panel file") {
//    val dateformat = new SimpleDateFormat("MM-dd HH:mm:ss")
//    println(s"生成panel测试开始时间" + dateformat.format(new Date()))
//    println()
//
//    def getResult(data: JsValue) = {
//      data.as[Map[String, JsValue]].map { x =>
//        x._1 -> x._2.as[Map[String, JsValue]].map { y =>
//          y._1 -> y._2.as[List[String]]
//        }
//      }
//    }
//
//    val panelHander = phPanelHeadle(args)
//    val result = getResult(panelHander.getPanelFile(List("201708")))
//    println("result = " + result)
//    println()
//    println(s"生成panel测试结束时间" + dateformat.format(new Date()))
//  }
//}
