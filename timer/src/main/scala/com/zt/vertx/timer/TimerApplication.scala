package com.zt.vertx.timer

import com.zt.scala.ZTApplication
import org.springframework.boot.builder.SpringApplicationBuilder

class TimerApplication extends ZTApplication {

}

object TimerApplication  extends App {
  new SpringApplicationBuilder(classOf[TimerApplication]).web(true).run(args: _*)

}
