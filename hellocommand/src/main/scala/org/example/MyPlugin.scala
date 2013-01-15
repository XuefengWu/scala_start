package org.example

import sbt._
import Keys._
object MyPlugin extends Plugin
{
  override lazy val settings = Seq(commands += myCommand,helloAll,failIfTrue,printState))

  lazy val myCommand = 
    Command.single("hello") { (state: State, name: String) =>
      println("Hi, " + name)
      println("How are you?")
      println()
      state
    }

  // A simple, multiple-argument command that prints "Hi" followed by the arguments.
  //   Again, it leaves the current state unchanged.
  def helloAll = Command.args("hello-all", "<name>") { (state, args) =>
    println("Hi " + args.mkString(" "))
    state
  }

  // A command that demonstrates failing or succeeding based on the input
  def failIfTrue = Command.single("fail-if-true") {
    case (state, "true") => state.fail
    case (state, _) => state
  }

  // A command that demonstrates getting information out of State.
  def printState = Command.command("print-state") { state =>
    import state._
    println(definedCommands.size + " registered commands")
    println("commands to run: " + show(remainingCommands))
    println()

    println("original arguments: " + show(configuration.arguments))
    println("base directory: " + configuration.baseDirectory)
    println()

    println("sbt version: " + configuration.provider.id.version)
    println("Scala version (for sbt): " + configuration.provider.scalaProvider.version)
    println()

    val extracted = Project.extract(state)
    import extracted._
    println("Current build: " + currentRef.build)
    println("Current project: " + currentRef.project)
    println("Original setting count: " + session.original.size)
    println("Session setting count: " + session.append.size)

    println(new File(".").list().mkString(", "))
    state
  }

  def show[T](s: Seq[T]) =
    s.map("'" + _ + "'").mkString("[", ", ", "]")
}