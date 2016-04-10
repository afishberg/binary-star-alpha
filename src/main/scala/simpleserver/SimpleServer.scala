package simpleserver;

import java.io.ObjectInputStream
import java.net.ServerSocket

import simpleclient.SimplePair

object SimpleServer extends App {
    val port = 2016
    val ss = new ServerSocket(port)

    println("Awaiting connection...")

    val s = ss.accept
    val is = s.getInputStream
    val ois = new ObjectInputStream(is)

    val sp = new SimplePair("HelloWorld", 42)

    if (sp != null) {
        println(sp.key)
    }

    println( ois.readObject() match { case p: SimplePair => p.key } )

    ois.close
    is.close
    s.close
    ss.close
}