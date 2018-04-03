package com.pharbers.panel.nhwa

import com.pharbers.baseModules.PharbersInjectModule
import com.pharbers.common.excel.input.{PhExcelXLSXCommonFormat, PhXlsxThirdSheetFormat}
import com.pharbers.pactions.actionbase.pActionTrait
import com.pharbers.pactions.generalactions.{csv2RddAction, existenceRdd, saveCurrenResultAction, xlsxReadingAction}
import com.pharbers.pactions.jobs.{choiceJob, sequenceJob, sequenceJobWithMap}
import com.pharbers.panel.nhwa.format.PhXlsxCpaFormat

/**
  * 1. read 2017年未出版医院名单.xlsx
  * 2. read universe_麻醉市场_online.xlsx
  * 3. read 匹配表
  * 4. read 补充医院
  * 5. read 通用名市场定义, 读第三页
  */
object PhNhwaPreActions extends sequenceJobWithMap with PharbersInjectModule {

    override val id: String = "panel_config"
    override val configPath: String = "pharbers_config/panel_config.xml"
    override val md = "base_path" :: "client_file_path" ::
        "product_match_file" :: "markets_match_file" :: "universe_file" ::
        "not_published_hosp_file" :: "fill_hos_data_file" ::
        "source_dir" :: "output_dir" :: "cache_dir" :: Nil

    val base_path: String = config.mc.find(p => p._1 == "base_path").get._2.toString
    val client_path: String = config.mc.find(p => p._1 == "client_file_path").get._2.toString

    val product_match_file: String = config.mc.find(p => p._1 == "product_match_file").get._2.toString
    val markets_match_file: String = config.mc.find(p => p._1 == "markets_match_file").get._2.toString
    val universe_file: String = config.mc.find(p => p._1 == "universe_file").get._2.toString
    val not_published_hosp_file: String = config.mc.find(p => p._1 == "not_published_hosp_file").get._2.toString
    val fill_hos_data_file: String = config.mc.find(p => p._1 == "fill_hos_data_file").get._2.toString

    val source_dir: String = config.mc.find(p => p._1 == "source_dir").get._2.toString
    val output_dir: String = config.mc.find(p => p._1 == "output_dir").get._2.toString
    val cache_dir: String = config.mc.find(p => p._1 == "cache_dir").get._2.toString

    val cache_location = cache_dir

    override val actions: List[pActionTrait] =
        new choiceJob {
            val cmd1 = xlsxReadingAction[PhExcelXLSXCommonFormat](not_published_hosp_file, "not_published_hosp_file")
            val cmd10 = saveCurrenResultAction(cache_location + cmd1.name)

            val actions =
                existenceRdd("not_published_hosp_file") ::
                    new sequenceJob {
                        override val actions: List[pActionTrait] = cmd1 :: cmd10 :: Nil
                    } :: csv2RddAction(cache_location + "/not_published_hosp_file") :: Nil
        } ::
        new choiceJob{
            val cmd2 = xlsxReadingAction[PhExcelXLSXCommonFormat](universe_file, "universe_file")
            val cmd20 = saveCurrenResultAction(cache_location + cmd2.name)

            val actions =
                existenceRdd("universe_file") ::
                    new sequenceJob {
                        override val actions: List[pActionTrait] = cmd2 :: cmd20 :: Nil
                    } :: csv2RddAction(cache_location + "/universe_file") :: Nil
        } ::
        new choiceJob{
            val cmd3 = xlsxReadingAction[PhExcelXLSXCommonFormat](product_match_file, "product_match_file")
            val cmd30 = saveCurrenResultAction(cache_location + cmd3.name)

            val actions =
                existenceRdd("product_match_file") ::
                    new sequenceJob {
                        override val actions: List[pActionTrait] = cmd3 :: cmd30 :: Nil
                    } :: csv2RddAction(cache_location + "/product_match_file") :: Nil
        } ::
        new choiceJob{
            val cmd4 = xlsxReadingAction[PhXlsxCpaFormat](fill_hos_data_file, "full_hosp_file")
            val cmd40 = saveCurrenResultAction(cache_location + cmd4.name)

            val actions =
                existenceRdd("full_hosp_file") ::
                    new sequenceJob {
                        override val actions: List[pActionTrait] = cmd4 :: cmd40 :: Nil
                    } :: csv2RddAction(cache_location + "/full_hosp_file") :: Nil
        } ::
        new choiceJob {
            val cmd5 = xlsxReadingAction[PhXlsxThirdSheetFormat](markets_match_file, "markets_match_file")
            val cmd50 = saveCurrenResultAction(cache_location + cmd5.name)

            val actions =
                existenceRdd("markets_match_file") ::
                    new sequenceJob {
                        override val actions: List[pActionTrait] = cmd5 :: cmd50 :: Nil
                    } :: csv2RddAction(cache_location + "/markets_match_file") :: Nil
        } :: Nil
//        new choiceJob{
//            val cmd6 = xlsxReadingAction[PhXlsxCpaFormat](cpa_file, "cpa")
//            val cmd60 = saveCurrenResultAction(cache_location + cmd6.name)
//
//            val actions =
//                existenceRdd("cpa") ::
//                    new sequenceJob {
//                        override val actions: List[pActionTrait] = cmd6 :: cmd60 :: Nil
//                    } :: csv2RddAction(cache_location + "/cpa") :: Nil
//        } ::
//        new choiceJob{
//            val cmd7 = xlsxReadingAction[PhXlsxSecondSheetFormat](cpa_file, "not_arrival_hosp_file")
//            val cmd70 = saveCurrenResultAction(cache_location + cmd7.name)
//
//            val actions =
//                existenceRdd("not_arrival_hosp_file") ::
//                    new sequenceJob {
//                        override val actions: List[pActionTrait] = cmd7 :: cmd70 :: Nil
//                    } :: csv2RddAction(cache_location + "/not_arrival_hosp_file") :: Nil
//        } :: Nil
}
