package binarystar

import interpreter.TypeCheck
import parser._
import testing.{TestFuncMap, TestParser}

object Main extends App {
    println(TypeCheck.check(IntLit(3)))
//    TestParser.run()

//    TestFuncMap.run()

}