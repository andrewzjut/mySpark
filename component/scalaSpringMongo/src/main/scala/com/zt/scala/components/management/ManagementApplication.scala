package com.zt.scala.components.management

import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.cloud.client.SpringCloudApplication
import org.springframework.context.annotation.ComponentScan

@SpringCloudApplication
@ComponentScan(basePackages = Array("com.zt.scala","com.tairanchina.csp"))
class ManagementApplication {
}

object ManagementApplication extends App {
  new SpringApplicationBuilder(classOf[ManagementApplication]).web(true).run(args: _*)
}