package binarystar

import scala.collection.mutable.ListBuffer

abstract class Drawable extends Corporeal {
    var spriteID:String = "Unknown"
    var scale:Int = 1

    def enable() = {
        Drawer.drawQueue += this
    }

    def enable(duration:Int) = {
        // TODO implement duration
    }

    def disable() = {
        Drawer.drawQueue -= this
    }
}

object Drawer {
    val drawQueue = new ListBuffer[Drawable]

    def mkDrawOrders(): List[DrawOrder] = { // TODO finish
        return List(new DrawOrder("Unknown", 0, 0))
    }
}

case class DrawOrder(spriteID: String, xPos: Int, yPos: Int) // Need to convert Double Pos into Int Pos
