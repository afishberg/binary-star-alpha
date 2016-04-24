package binarystar

import parser.Literal

sealed abstract class Hardware(creator: Actor) {
    //def mkEnv():Map[String, () => Any]
}

sealed abstract class Weapon(creator: Actor)    extends Hardware(creator)
sealed abstract class Armor(creator: Actor)     extends Hardware(creator)
sealed abstract class Engine(creator: Actor)    extends Hardware(creator)
sealed abstract class Gadget(creator: Actor)    extends Hardware(creator)

class W_BasicBlaster(creator: Actor) extends Weapon(creator) {
    val DAMAGE = 10
    val MAX_COOLDOWN = 1000
    var cooldown = 0
    val bulletSpriteID = "BasicBlasterBullet"
    val bulletVel = 10

    def mkEnv():Map[String, () => Any] = {
        return Map("fire" -> fire, "can_fire" -> can_fire)
    }

    def fire(): Unit = {
        val bullet = new BasicBullet(creator)
        bullet.setPos(creator.xPos, creator.yPos)
        bullet.dir = creator.dir
        bullet.vel = bulletVel
        bullet.enable()
    }


    def can_fire(): Boolean = {
        return cooldown == 0
    }
}

class A_BasicArmor(creator: Actor) extends Armor(creator)

class E_BasicEngine(creator: Actor) extends Engine(creator) {
    val FUNC = List("set_vel", "get_vel", "get_vel_max", "set_rot", "get_rot", "get_rot_max")
    val MAX_VEL = 10
    val MAX_ROT_VEL = 30
    var vel = 0
    var rotVel = 0
}

class G_BasicRadar(creator: Actor)  extends Gadget(creator) {
    val MAX_RANGE = 100
}

class G_AlertModule(creator: Actor)  extends Gadget(creator) {
    def alert(msg: String): Unit = {
        // TODO create a text draw command for duration
    }
}
