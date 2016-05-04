package binarystar

trait Corporeal {
    var xPos = 0: Double
    var yPos = 0: Double
    var dir  = 0: Double

    def setPos(xSet:Double, ySet:Double) = {
        xPos = xSet
        yPos = ySet
    }

    def incPos(dx: Double, dy: Double) = {
        xPos += dx
        yPos += dy
    }
}

trait Movable extends Corporeal { // can move
    def updatePos(): Unit
}

trait Velocity extends Movable {
    var vel = 0: Double

    def updatePos() = {
        val dx = math.cos(math.toRadians(dir)) * vel
        val dy = math.sin(math.toRadians(dir)) * vel

        incPos(dx, dy)
    }

}

trait Acceleration extends Velocity {
    var accel = 0: Double

    override def updatePos() = {
        vel += accel
        super.updatePos()
    }
}

trait Solid extends Corporeal { // can collide
    var dxBox: Int = 0
    var dyBox: Int = 0

    def collision(other: Solid)
}

trait Mortal { // can die
    def isAlive: Boolean
    def death(): Unit
}

trait Health extends Mortal {
    val MAX_HEALTH:Int
    var health:Int = MAX_HEALTH

    def isAlive:Boolean = health <= 0
}

trait Expiration extends Mortal {
    val MAX_DURATION: Int
    var duration:Int = MAX_DURATION

    def isAlive:Boolean = duration > 0
}

trait Allegiance extends Solid { // has ally
    var allegiance:Int = 0

    def isAllied(other: Allegiance):Boolean = this.allegiance == other.allegiance
}

trait Explosive extends Expiration with Solid { // blows up when it collides with a Health
    var damage: Int

    def collision(other: Solid) = {
        if (other.isInstanceOf[Health])
            collision(other.asInstanceOf[Health])
    }

    def collision(other: Health) = {
        other.health -= damage
        death()
    }
}

trait FFExplosive extends Explosive with Allegiance {
    override def collision(other: Health) = {
        if (other.isInstanceOf[Allegiance] && !isAllied(other.asInstanceOf[Allegiance]))
            super.collision(other)
    }
}
