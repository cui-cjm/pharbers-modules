package com.pharbers.bmpattern

import scala.concurrent.duration._
import akka.actor.ActorLogging
import akka.actor.ActorRef
import akka.actor.Props
//import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.JsValue
import akka.actor.Actor
import play.api.libs.json.Json.toJson
import com.pharbers.bmmessages._
//import bmlogic.auth.{AuthModule, msg_AuthCommand}
//import bmlogic.phonecode.{PhoneCodeModule, msg_PhoneCodeCommand}
//import bmlogic.profile.{ProfileModule, msg_ProfileCommand}
//import bmlogic.kidnap.{KidnapModule, msg_KidnapCommand}
//import bmlogic.order.{OrderModule, msg_OrderCommand}
//import bmlogic.timemanager.{TimemanagerModule, msg_TMCommand}
//import bmlogic.collections.{CollectionsModule, msg_CollectionsCommand}
//import bmlogic.emxmpp.{EMModule, msg_EMMessageCommand}
//import bmlogic.dongdaselectedservice.{msg_SelectedServiceCommand, SelectedServiceModule}
//import bmlogic.orderDate.{msg_OrderDateCommand, OrderDateModule}
//import bmlogic.common.placeholder.{msg_PlaceHoldCommand, PlaceHolderModule}

object PipeFilterActor {
	def prop(originSender : ActorRef, msr : MessageRoutes) : Props = {
		Props(new PipeFilterActor(originSender, msr))
	}
}

class PipeFilterActor(originSender : ActorRef, msr : MessageRoutes) extends Actor with ActorLogging {
    implicit val cm = msr.cm
	
	def dispatchImpl(cmd : CommonMessage, module : ModuleTrait) = {
		tmp = Some(true)
		module.dispatchMsg(cmd)(rst) match {
			case (_, Some(err)) => {
				originSender ! error(err)
				cancelActor
			}
			case (Some(r), _) => {
//				println(r)
				rst = Some(r)
				rstReturn
				cancelActor
			}
			case _ => println("never go here")
		}
	}
	
	var tmp : Option[Boolean] = None
	var rst : Option[Map[String, JsValue]] = msr.rst
	var next : ActorRef = null
	def receive = {
		case cmd : CommonMessage => dispatchImpl(cmd, cmd.mt)
//		case cmd : msg_AuthCommand => dispatchImpl(cmd, AuthModule)
//		case cmd : msg_PhoneCodeCommand => dispatchImpl(cmd, PhoneCodeModule)
//		case cmd : msg_ProfileCommand => dispatchImpl(cmd, ProfileModule)
//		case cmd : msg_KidnapCommand => dispatchImpl(cmd, KidnapModule)
//		case cmd : msg_OrderCommand => dispatchImpl(cmd, OrderModule)
//		case cmd : msg_TMCommand => dispatchImpl(cmd, TimemanagerModule)
//		case cmd : msg_CollectionsCommand => dispatchImpl(cmd, CollectionsModule)
//		case cmd : msg_SelectedServiceCommand => dispatchImpl(cmd, SelectedServiceModule)
//		case cmd : msg_EMMessageCommand => dispatchImpl(cmd, EMModule)
//        case cmd : msg_OrderDateCommand => dispatchImpl(cmd, OrderDateModule)
//        case cmd : msg_PlaceHoldCommand => dispatchImpl(cmd, PlaceHolderModule)
//		case cmd : msg_ResultCommand => dispatchImpl(cmd, ResultModule)
//        case cmd : msg_LogCommand => dispatchImpl(cmd, LogModule)
		case cmd : ParallelMessage => {
		    cancelActor
			next = context.actorOf(ScatterGatherActor.prop(originSender, msr), "scat")
			next ! cmd
		}
		case timeout() => {
			originSender ! new timeout
			cancelActor
		}
	 	case x : AnyRef => println(x); ???
	}

    import scala.concurrent.ExecutionContext.Implicits.global
	val timeOutSchdule = context.system.scheduler.scheduleOnce(2000 second, self, new timeout)

	def rstReturn : Unit = tmp match {
		case Some(_) => { rst match {
			case Some(r) => 
				msr.lst match {
					case Nil => {
						originSender ! result(toJson(r))
					}
					case head :: tail => {
						head match {
							case p : ParallelMessage => {
								next = context.actorOf(ScatterGatherActor.prop(originSender, MessageRoutes(tail, rst)), "scat")
								next ! p
							}
							case c : CommonMessage => {
								next = context.actorOf(PipeFilterActor.prop(originSender, MessageRoutes(tail, rst)), "pipe")
								next ! c
							}
						}
					}
					case _ => println("msr error")
				}
			case _ => Unit
		}}
		case _ => println("never go here"); Unit
	}
	
	def cancelActor = {
		timeOutSchdule.cancel
//		context.stop(self) 		// 因为后创建的是前创建的子Actor，当父Actor stop的时候，子Actor 也同时Stop，不能进行传递了
	}
}