//package parser
//
//import scala.util.parsing.combinator._
//
//
//object Parser extends JavaTokenParsers with PackratParsers {
//    def apply(s: String): ParseResult[FuncDecl] = parseAll(pFuncDecl, s)
//
//    def pFuncDecl: PackratParser[FuncDecl] =
//        pType ~ pName ~ "(" ~ pArgs ~ ")" ~ "{" ~ pBlock ~ "}" ^^
//            { case rType ~ rName ~ "(" ~ rArgs ~ ")" ~ "{" ~ rBlock ~ "}" => FuncDecl(rType, rName, rArgs, rBlock) }
//
//    def pType: PackratParser[Type] = (
//            "int"       ^^^ IntTy()
//        |   "double"    ^^^ DoubleTy()
//        |   "bool"      ^^^ BoolTy()
//        |   "char"      ^^^ CharTy()
//        |   "void"      ^^^ VoidTy()
//    )
//
//    def pName: PackratParser[String] = ident ^^^ String
//
//    def pArgs: PackratParser[List[VarDecl]] = repsep(pVarDecl, ",")
//
//    def pBlock: PackratParser[Block] = "{" ~ rep1sep(pStatement, ";") ~ "}" ^^
//        { case "{" ~ rBlock ~ "}" => Block(rBlock) }
//
//    def pVarDecl: PackratParser[VarDecl] =
//        pType ~ pName ^^ { case rType ~ rName => VarDecl(rType, rName) }
//
//    def pStatement: PackratParser[Statement] = (
//            pIfThenElse
//        |   pCall
//        |   pFor
//        |   pWhile
//        |   pBreak
//        |   pAssign
//        |   pDeclAssign
//        |   pAddAssign
//        |   pSubAssign
//        |   pMultAssign
//        |   pDivAssign
//        |   pModAssign
//    )
//
//    def pIfThenElse: PackratParser[IfThenElse] =
//        "if" ~ "(" ~ pExpr ~ ")" ~ pBlock ~ "else" ~ pBlock ^^
//            { case "if" ~ "(" ~ rExpr ~ ")" ~ rBlockTrue ~ "else" ~ rBlockFalse
//            => IfThenElse(rExpr, rBlockTrue, rBlockFalse) }
//
//    def pExpr: PackratParser[Expr] = (
//            pExpr ~ "+" ~ pTerm ^^ { case rExpr ~ "+" ~ rTerm => AddOp(rExpr, rTerm) }
//        |   pExpr ~ "-" ~ pTerm ^^ { case rExpr ~ "-" ~ rTerm => SubOp(rExpr, rTerm) }
//        |   pTerm
//    )
//
//    def pTerm: PackratParser[Expr] = (
//            pTerm ~ "*" ~ pFact ^^ { case rTerm ~ "*" ~ rFact => MultOp(rTerm, rFact) }
//        |   pTerm ~ "/" ~ pFact ^^ { case rTerm ~ "/" ~ rFact => DivOp(rTerm, rFact) }
//        |   pFact
//    )
//
//    def pFact: PackratParser[Expr] = (
//            pLiteral
//        |   pVar
//        |   "("~>pExpr<~")"
//    )
//
//    def pLiteral: PackratParser[Literal] = (
//            pIntLit
//        |   pDoubleLit
//        |   pBoolLit
//        |   pCharLit
//    )
//
//    def pIntLit: PackratParser[IntLit] = wholeNumber ^^ { s => IntLit(s.toInt) }
//
//    def pDoubleLit: PackratParser[DoubleLit] = decimalNumber ^^ { s => DoubleLit(s.toDouble) }
//
//    def pBoolLit: PackratParser[BoolLit] = (
//            "true" ^^^ BoolLit(true)
//        |   "false" ^^^ BoolLit(false)
//    )
//
//    def pCharLit: PackratParser[CharLit] = "['].[']".r ^^ { case rChar => CharLit(rChar.charAt(1)) }
//
//    def pVar: PackratParser[Var] = pName ^^ { case rName => Var(rName) }
//
//    def pCall: PackratParser[Call]
//
//    def pFor: PackratParser[For]
//
//    def pWhile: PackratParser[While]
//
//    def pBreak: PackratParser[Break]
//
//    def pAssign: PackratParser[Assign]
//
//    def pDeclAssign: PackratParser[DeclAssign]
//
//    def pAddAssign: PackratParser[AddAssign]
//
//    def pSubAssign: PackratParser[SubAssign]
//
//    def pMultAssign: PackratParser[MultAssign]
//
//    def pDivAssign: PackratParser[DivAssign]
//
//    //def pModAssign: PackratParser[ModAssign]
//
//
////    lazy val pStatement: PackratParser[Statement] =
////        (
////            rep1sep(pStatement, ";") ^^ Block
////        |   pIfThenElse )
////
////    def
//}
