package interpreter

import parser._

object TypeCheck {
    type Environment = Map[String, (Ty, Literal)]
    type Definitions = Map[String, FuncDecl]
    //type CheckTy = () => Ty with Product with Serializable

    //TODO use lazy vals so you don't need to check the whole thing?

    // gets you the type of an expr  // TypeOf
    def apply(expr: Expr, env: Environment, dfn: Definitions): Ty = {
        // TODO environment
        expr match {
            case NullLit() => VoidTy
            case IntLit(_) => IntTy
            case DoubleLit(_) => DoubleTy
            case BoolLit(_) => BoolTy

            case Var(s) => env(s) match {
                case (ty, lit) => ty
            } // TODO
            case Eval(Call(s, args)) => {
                val func = dfn(s)
                val param = func match {
                    case FuncDecl(_, _, p, _) => p
                }

                if (param.length != args.length) {
                    throw new Exception("Error: Parameter and Argument count mismatch")
                }

                val pargs = param.zip(args)
                val results = pargs.map { case (VarDecl(ty, _), expr) => ty == TypeCheck(expr, env, dfn) }

                if (results.contains(false)) {
                    // TODO unhappy if pass int into double
                    throw new Exception("Error: Parameters and Argument types did not match")
                }

                func match {
                    case FuncDecl(r, _, _, _) => r
                }
            }

            case NegateOp(e) => check(TypeCheck(e, env, dfn), BoolTy) // TODO
            case NotOp(e) => BoolTy

            case AddOp(e1, e2) => check(numSuperTy(TypeCheck(e1, env, dfn), TypeCheck(e2, env, dfn)), NumTy)
            case SubOp(e1, e2) => check(numSuperTy(TypeCheck(e1, env, dfn), TypeCheck(e2, env, dfn)), NumTy)
            case MultOp(e1, e2) => check(numSuperTy(TypeCheck(e1, env, dfn), TypeCheck(e2, env, dfn)), NumTy)
            case DivOp(e1, e2) => check(numSuperTy(TypeCheck(e1, env, dfn), TypeCheck(e2, env, dfn)), NumTy)
            case ModOp(e1, e2) => check(numSuperTy(TypeCheck(e1, env, dfn), TypeCheck(e2, env, dfn)), NumTy)

            case AndOp(e1, e2) => check(numSuperTy(TypeCheck(e1, env, dfn), TypeCheck(e2, env, dfn)), BoolTy)
            case OrOp(e1, e2) => check(numSuperTy(TypeCheck(e1, env, dfn), TypeCheck(e2, env, dfn)), BoolTy)
            case LtOp(e1, e2) => check(numSuperTy(TypeCheck(e1, env, dfn), TypeCheck(e2, env, dfn)), BoolTy)
            case GtOp(e1, e2) => check(numSuperTy(TypeCheck(e1, env, dfn), TypeCheck(e2, env, dfn)), BoolTy)
            case LeqOp(e1, e2) => check(numSuperTy(TypeCheck(e1, env, dfn), TypeCheck(e2, env, dfn)), BoolTy)
            case GeqOp(e1, e2) => check(numSuperTy(TypeCheck(e1, env, dfn), TypeCheck(e2, env, dfn)), BoolTy)
            case EqOp(e1, e2) => check(numSuperTy(TypeCheck(e1, env, dfn), TypeCheck(e2, env, dfn)), BoolTy)
            case NeqOp(e1, e2) => check(numSuperTy(TypeCheck(e1, env, dfn), TypeCheck(e2, env, dfn)), BoolTy)

            case _ => throw new Exception("Error: Unknown expression for typechecking")
        }
    }

    // returns the type if they are eq otherwise throws error
    def check(check: Ty, expected: Ty): Ty = {
        if (eqTy(check, expected)) {
            check
        } else {
            throw new Exception("Type Error: Got" + check + " and expected " + expected)
        }
    }

    //}
    //expected match {
    //    case NumTy  => if (check == IntTy || check == DoubleTy) { return check }
    //    case _      => if (check == expected) { return check }
    //}

    def numSuperTy(ty1: Ty, ty2: Ty): Ty = {
        (ty1, ty2) match {
            case (IntTy, IntTy) => IntTy
            case (IntTy, DoubleTy) => DoubleTy
            case (DoubleTy, IntTy) => DoubleTy
            case (DoubleTy, DoubleTy) => DoubleTy
            case _ => throw new Exception("Error: Bad supertype")
        }
    }

    def evalCheck(expr: Expr, env: Environment, dfn: Definitions, ty: Ty): Boolean = TypeCheck(expr, env, dfn) == ty

    def eqTy(t1: Ty, t2: Ty): Boolean = {
        t2 match {
            case NumTy => t1 == IntTy || t1 == DoubleTy || t1 == NumTy
            case _ => t1 == t2
        }
    }
}



