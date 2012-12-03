package com.lizi.crawler

object DispatchDemo extends App {

  import dispatch._
  val svc = url("http://www.163.com")
  val country = Http(svc OK as.String)
  for (c <- country)
  println(c)
}