package interpreter

import binarystar.Ship
import parser._

class BSInterpreter(ship:Ship, funcDecls:List[FuncDecl]) {
    type Environment    = collection.mutable.Map[String, (Ty, Int)]
    type Memory         = collection.mutable.Map[Int, Literal]
    type Definitions    = Map[String, FuncDecl]

    // TODO check for func name collision
    val functions:Definitions   = funcDecls.map( x => x.name -> x ).toMap // http://stackoverflow.com/questions/674639/scala-best-way-of-turning-a-collection-into-a-map-by-key

    val globalEnv:Environment   = collection.mutable.Map()
    val mem:Memory              = collection.mutable.Map()
    var nextAddr                = 0 //

    val init = getValidInit
    val main = getValidMain



    def getValidInit:FuncDecl = {
        val init = functions("init")
        if (init.rtnTy == VoidTy && init.param == List()) {
            init
        } else {
            throw new Exception("Error: Bad init() header") // TODO better exceptions
        }
    }

    def getValidMain:FuncDecl = {
        val main = functions("init")
        if (main.rtnTy == VoidTy && main.param == List()) {
            main
        } else {
            throw new Exception("Error: Bad main() header") // TODO better exceptions
        }
    }

    def malloc(lit:Literal):Int = {
        mem += (nextAddr -> lit) // TODO fail on int overflow
        nextAddr += 1
        nextAddr - 1
    }

    def free(i:Int) = {
        mem -= i
    }

    def BSInterpreter:Unit = {
        //functions foreach { case(k,v) => TypeCheck() }
        // TODO static typechecking?

        executeFunc(globalEnv,init)
        // TODO run init() and update globalEnv


    }

    def run:Unit = {
        executeFunc(globalEnv,main)
        // TODO run main()
    }

    //def extractBlock(block: Block): List[Statement]

    def executeFunc(env:Environment, func:FuncDecl):Literal = {
        val statements = (func match { case FuncDecl(_,_,_,body) => body }) match { case Block(x) => x }
        NullLit() // TODO remove
    }

    def executeBlock(env:Environment, block:List[Statement], rtnTy:Ty):Literal = {
        //rtnTy: Type, name: String, param: List[VarDecl], body: Block
//        for (statement <-  unbox ) {//TODO
//
//        }
        NullLit() // TODO
    }

    def executeStatement(env:Environment, statement:Statement, rtnTy:Ty):Literal = {
        if ( checkStatement(env,statement,rtnTy) ) {
            statement match {
                case Block(x) => executeBlock(env, x, rtnTy)
                case IfThenElse(c, t, f) => {
                    val r = eval(c,env)
                    if (r ==  )
                }

            }
        } else {
            throw new Exception("Type Error")
        }
    }

    def eval(expr:Expr,env:Environment):Literal = {
        expr match {
            case NullLit()              => NullLit()
            case IntLit(x)              => IntLit(x)
            case DoubleLit(x)           => DoubleLit(x)
            case BoolLit(x)             => BoolLit(x)
            case CharLit(x)             => CharLit(x)

            case Var(name)              => env(name) match { case (_,x) => x }
            case Eval(Call(name,args))  => {
                val func = functions(name)
                executeFunc(env ++ , )
                mem(env(name)._2)
                //executeFunc()
            }

        }
    }

    def checkStatement(env:Environment, statement:Statement, rtnTy:Ty):Boolean = {
        statement match {
            case VarDecl(_,_)               => true
            case Block(_)                   => true
            case IfThenElse(cond,_,_)       => TypeCheck.evalCheck(cond,env,dfn,BoolTy)
            case Call(name,args)            => {
                val func = dfn(name)
                val param = func match { case FuncDecl(_,_,p,_) => p }

                if (param.length != args.length) {
                    return false
                }

                val pargs = param.zip(args)
                val results = pargs.map{ case (VarDecl(ty,_),expr) => ty == TypeCheck(expr,env,dfn) }

                !results.contains(false)
            }
            case For(_,cond,_,_)            => TypeCheck.evalCheck(cond,env,dfn,BoolTy)
            case While(cond,_)              => TypeCheck.evalCheck(cond,env,dfn,BoolTy)
            case Return(expr)               => TypeCheck.evalCheck(expr,env,dfn,rtnTy)
            case Break()                    => true

            case Assign(name,expr)          => TypeCheck.evalCheck(expr,env,dfn,env(name) match { case (x,_) => x } )
            case DeclAssign(decl,expr)      => TypeCheck.evalCheck(expr,env,dfn,decl match { case VarDecl(x,_) => x } )
            case AddAssign(name,expr)       => TypeCheck.evalCheck(expr,env,dfn,env(name) match { case (x,_) => x } ) // TODO check that name is of NumTy?
            case SubAssign(name,expr)       => TypeCheck.evalCheck(expr,env,dfn,env(name) match { case (x,_) => x } )
            case MultAssign(name,expr)      => TypeCheck.evalCheck(expr,env,dfn,env(name) match { case (x,_) => x } )
            case DivAssign(name,expr)       => TypeCheck.evalCheck(expr,env,dfn,env(name) match { case (x,_) => x } )
            case ModAssign(name,expr)       => TypeCheck.evalCheck(expr,env,dfn,env(name) match { case (x,_) => x } )
            case PreIncOp(name)             => TypeCheck.eqTy(env(name) match { case (x,_) => x }, NumTy)
            case PreDecOp(name)             => TypeCheck.eqTy(env(name) match { case (x,_) => x }, NumTy)
            case PostIncOp(name)            => TypeCheck.eqTy(env(name) match { case (x,_) => x }, NumTy)
            case PostDecOp(name)            => TypeCheck.eqTy(env(name) match { case (x,_) => x }, NumTy)
        }
    }

    def apply(): Unit = {

    }
}
