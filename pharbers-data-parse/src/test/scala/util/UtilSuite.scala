package util

import com.pharbers.mongodbConnect._data_connection
import com.pharbers.util.excel.phHandleExcelTrait
import com.pharbers.util.excel.impl.phHandleExcelImpl
import org.scalatest.FunSuite

import scala.collection.immutable.Map
import scala.collection.mutable

/**
  * Created by clock on 17-9-7.
  */
class UtilSuite extends FunSuite {
    test("test get count") {
        val file_local = "/home/clock/Downloads/按辉瑞采购清单中的通用名划分6市场others.xlsx"
        val parser: phHandleExcelTrait = new phHandleExcelImpl
        val count = parser.getCount(file_local)
        println(s"sheet number = $count")
    }

    test("read excel to List") {
        val file_local = "/home/clock/Downloads/201611+CPA.xlsx"
        val parser: phHandleExcelTrait = new phHandleExcelImpl
        parser.readToList(file_local).foreach(println)
    }

    test("read excel to db => simple") {
        //默认只导入第一页,不设置字段名,没有默认值,不进行filter
        import phHandleExcelImpl._
        val file_local = "/home/clock/Downloads/按辉瑞采购清单中的通用名划分6市场others.xlsx"
        val parser: phHandleExcelTrait = new phHandleExcelImpl
        parser.readToDB(file_local,"test").foreach(println)
    }
    test("read excel to db2 => specified sheet and filter") {
        import phHandleExcelImpl._
        //导入指定页数,进行filter,不设置字段名,没有默认值
        val file_local = "/home/clock/Downloads/按辉瑞采购清单中的通用名划分6市场others.xlsx"
        val parser: phHandleExcelTrait = new phHandleExcelImpl
        implicit val filterFun: Map[String,String] => Boolean = { tr =>
            tr.get("GYCX反馈通用名") match {
                case None => false
                case Some(s) if s.startsWith("头") => true
                case _ => false
            }
        }
        parser.readToDB(file_local,"test", 2).foreach(println)
    }
    test("read excel to db3 => setField and setDefault") {
        import phHandleExcelImpl._
        val file_local = "/home/clock/Downloads/按辉瑞采购清单中的通用名划分6市场others.xlsx"

        val setFieldMap = Map(
            "GYCX反馈通用名" -> "TEST"
        )

        val setDefaultMap = Map(
            "TEST" -> "$TA"
        )

        val parser: phHandleExcelTrait = new phHandleExcelImpl
        parser.readToDB(file_local, "test", 2, fieldArg = setFieldMap, defaultValueArg = setDefaultMap).foreach(println)
    }
    test("read excel to db4 => post function") {
        import phHandleExcelImpl._
        val file_local = "/home/clock/Downloads/按辉瑞采购清单中的通用名划分6市场others.xlsx"

        val setFieldMap = Map(
            "GYCX反馈通用名" -> "TEST"
        )

        val setDefaultMap = Map(
            "TEST" -> "$TA"
        )

        //新建列
        implicit val postFun: mutable.Map[String,String] => Unit = { tr =>
            tr += "DOIE" -> tr("TA")
        }

        val parser: phHandleExcelTrait = new phHandleExcelImpl
        parser.readToDB(file_local, "test", 2, fieldArg = setFieldMap, defaultValueArg = setDefaultMap).foreach(println)

        _data_connection.getCollection("test").drop
    }

    test("write excel by List") {
        val file_local = "/home/clock/Downloads/按辉瑞采购清单中的通用名划分6市场others.xlsx"
        val output_file = "/home/clock/Downloads/CPA_GYCX_Others_panel.xlsx"
        val parser: phHandleExcelTrait = new phHandleExcelImpl
        println(parser.writeByList(output_file, parser.readToList(file_local)))
    }

}
