package act

abstract class Orientation {
  def left: Orientation
  def right: Orientation
}

case object South extends Orientation {
  def left = East
  def right = West
}
case object North extends Orientation {
  def left = West
  def right = East
}
case object West extends Orientation {
  def left = South
  def right = North
}
case object East extends Orientation {
  def left = North
  def right = South
}
object Orientation {
  def valueOf(s: String): Orientation = {
    valueOf(s.charAt(0))
  }
  def valueOf(c: Char): Orientation = {
    c.toUpper match {
      case 'N' => North
      case 'E' => East
      case 'S' => South
      case 'W' => West
    }
  }
}

abstract class Cmd
case object Left extends Cmd
case object Right extends Cmd
case object Move extends Cmd
object Cmd {

  def valueOf(c: Char): Cmd = {
    c.toUpper match {
      case 'L' => Left
      case 'R' => Right
      case 'M' => Move
    }
  }

}

case class Position(x: Int, y: Int, orient: Orientation)
case class Rover(x: Int, y: Int, orient: Orientation) {
  val positions = scala.collection.mutable.Stack[Position](Position(x, y, orient))

  def execute(commands: String) {
    commands.foreach(c => exec(Cmd.valueOf(c)))
  }
  def exec(command: Cmd) {
    val last = command match {
      case Left => turnLeft()
      case Right => turnRight()
      case Move => move()
    }
    positions push last
  }

  def turnLeft() = {
    val last = positions.head
    last.copy(orient = last.orient.left)
  }
  def turnRight() = {
    val last = positions.head
    last.copy(orient = last.orient.right)
  }
  def move() = {
    val last = positions.head
    last.orient match {
      case North => last.copy(y = last.y + 1)
      case East => last.copy(x = last.x + 1)
      case South => last.copy(y = last.y - 1)
      case West => last.copy(x = last.x - 1)
    }
  }

  def current = positions.head
}

object MarsRovers {

  def apply(arg: String) = init(arg)

  def init(arg: String) = {
    val args = arg.split(" ")
    val x = args.head.toInt
    val y = args.tail.head.toInt
    val orient = args.reverse.head.toUpperCase()
    Rover(x, y, Orientation.valueOf(orient))
  }

}