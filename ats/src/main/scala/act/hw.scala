package act


import java.io.File
import scala.xml._
import util.DataPrepare
import conf.Conf
import org.apache.commons.io.FileUtils
import scala.Function1

class  Fruit
class Apple extends Fruit
class HongFuShi extends Apple

class Drink
class Juice extends Drink
class AppleJuice extends Juice

object CleanData {
 
  def extractor[T <: Fruit,R <: Drink](v:T,f: T => R):R = {
    f(v)
  }
  
  def appleOp = (s:Apple) => new Juice
  val juice:Juice = extractor(new HongFuShi, appleOp)
  
}
