package binarystar

abstract class Actor extends Drawable

abstract class Ship extends Actor with Health with Solid with Allegiance with Acceleration {
    val weapon:  Weapon
    val armor:   Armor
    val engine:  Engine
    val gadgets: List[Gadget]


}

class BasicShip extends Ship {
    spriteID = "BasicShip"
    val MAX_HEALTH = 100

    val weapon  = new W_BasicBlaster(this)
    val armor   = new A_BasicArmor(this)
    val engine  = new E_BasicEngine(this)
    val gadgets = List(new G_BasicRadar(this), new G_AlertModule(this))

    def collision(other:Solid):Unit = return

    def death() = disable()
}

abstract class Bullet(creator: Actor) extends Actor

class BasicBullet(creator: Actor) extends Bullet(creator) with Velocity with FFExplosive {
    spriteID = "BasicBullet"
    var damage = 10
    val MAX_DURATION = 10

    def death() = disable() // TODO figure out a better structure for this
}
