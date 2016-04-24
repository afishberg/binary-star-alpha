package parser

object TestParser {
    def run(programFilename:String = "prog3.bs") = {
        val outsep = "========================="
        println(outsep)

        val programText = getFileContents(programFilename)
        println(programText)
        println(outsep)

        val program = BSParser(programText)
        println(program)
        println(outsep)
    }

    def getFileContents(filename: String): String = io.Source.fromFile(filename).mkString
}
