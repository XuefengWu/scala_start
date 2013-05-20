package controllers

import org.scalatest.FlatSpec

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.selenium.WebBrowser
import org.scalatest.FlatSpec
import org.openqa.selenium._
import org.openqa.selenium.htmlunit._

class GoogleTest extends FlatSpec with ShouldMatchers with WebBrowser {

  implicit val webDriver: WebDriver = new HtmlUnitDriver
  
  "The Google app home page" should "have the correct title" in {
    go to "http://www.google.com"
    click on "q"
    textField("q").value = "Cheese!"
    submit()
    // Google's search is rendered dynamically with JavaScript.
    pageTitle should be("Cheese! - Google Search")
  }
}
