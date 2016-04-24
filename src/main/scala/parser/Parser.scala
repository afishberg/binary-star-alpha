package parser

import scala.util.parsing.combinator._


object BSParser extends JavaTokenParsers with PackratParsers {
    //def apply(s: String): ParseResult[FuncDecl] = parseAll(pFuncDecl, s)
    def apply(s: String): ParseResult[List[AST]] = parseAll(rep(pFuncDecl), s)

    def pFuncDecl: PackratParser[FuncDecl] =
        pType ~ pName ~ pParams ~ pBlock ^^
            { case rType ~ rName ~ rParams ~ rBlock => FuncDecl(rType, rName, rParams, rBlock) }

    def pType: PackratParser[Type] = (
        "int"       ^^^ IntTy()
            |   "double"    ^^^ DoubleTy()
            |   "bool"      ^^^ BoolTy()
            |   "char"      ^^^ CharTy()
            |   "void"      ^^^ VoidTy()
        )

    def pName: PackratParser[String] = ident ^^ { rIdent => rIdent }

    def pParams: PackratParser[List[VarDecl]] = "(" ~ repsep(pVarDecl, ",") ~ ")" ^^
        { case "(" ~ rParams ~ ")" => rParams }

    def pBlock: PackratParser[Block] = "{" ~ rep(pStatement) ~ "}" ^^
        { case "{" ~ rBlock ~ "}" => Block(rBlock)}

    def pVarDecl: PackratParser[VarDecl] =
        pType ~ pName ^^ { case rType ~ rName => VarDecl(rType, rName) }

    def pStatement: PackratParser[Statement] = (
        pIfThenElse
            |   pCall <~ ";"
            |   pFor
            |   pWhile
            |   pBreak <~ ";"
            |   pAssign <~ ";"
            |   pDeclAssign <~ ";"
            |   pVarDecl <~ ";"
            |   pAddAssign <~ ";"
            |   pSubAssign <~ ";"
            |   pMultAssign <~ ";"
            |   pDivAssign <~ ";"
            |   pModAssign <~ ";"
            |   pReturn <~ ";"
        )

    def pReturn: PackratParser[Return] = {
        "return" ~ pExpr ^^ { case "return" ~ rExpr => Return(rExpr) } | // TODO get | on next line?
            "return" ^^^ Return(NullLit())
    }

    def pIfThenElse: PackratParser[IfThenElse] =
        "if" ~ "(" ~ pExpr ~ ")" ~ pBlock ~ "else" ~ pBlock ^^
            { case "if" ~ "(" ~ rExpr ~ ")" ~ rBlockTrue ~ "else" ~ rBlockFalse
            => IfThenElse(rExpr, rBlockTrue, rBlockFalse) }

    lazy val pExpr: PackratParser[Expr] = (
        pExpr ~ "+" ~ pTerm ^^ { case rExpr ~ "+" ~ rTerm => AddOp(rExpr, rTerm) }
            |   pExpr ~ "-" ~ pTerm ^^ { case rExpr ~ "-" ~ rTerm => SubOp(rExpr, rTerm) }
            |   pTerm
        )

    lazy val pTerm: PackratParser[Expr] = (
        pTerm ~ "*" ~ pFact ^^ { case rTerm ~ "*" ~ rFact => MultOp(rTerm, rFact) }
            |   pTerm ~ "/" ~ pFact ^^ { case rTerm ~ "/" ~ rFact => DivOp(rTerm, rFact) }
            |   pFact
        )

    def pFact: PackratParser[Expr] = (
        pEval
            |   pVar
            |   pLiteral
            |   "("~>pExpr<~")"
        )

    def pLiteral: PackratParser[Literal] = (
        pIntLit
            |   pDoubleLit
            |   pBoolLit
        //|   pCharLit
        )

    def pIntLit: PackratParser[IntLit] = wholeNumber ^^ { s => IntLit(s.toInt) }

    def pDoubleLit: PackratParser[DoubleLit] = decimalNumber ^^ { s => DoubleLit(s.toDouble) }

    def pBoolLit: PackratParser[BoolLit] = (
        "true" ^^^ BoolLit(true)
            |   "false" ^^^ BoolLit(false)
        )

    //def pCharLit: PackratParser[CharLit] = "['].[']".r ^^ { case rChar => CharLit(rChar.charAt(1)) }

    def pVar: PackratParser[Var] = pName ^^ { case rName => Var(rName) }

    def pEval: PackratParser[Eval] = pCall ^^ { case rCall => Eval(rCall) }

    def pCall: PackratParser[Call] = pName ~ "(" ~  pArgs ~ ")" ^^ { case rName ~ "(" ~ rArgs ~ ")" => Call(rName, rArgs) }

    def pArgs: PackratParser[List[Expr]] = repsep(pExpr, ",")

    def pFor: PackratParser[For] =
        "for" ~ "(" ~ pStatement ~ ";" ~ pExpr ~ ";" ~ pStatement ~ ")" ~ "{" ~ pBlock ~ "}" ^^
            { case "for" ~ "(" ~ rStatement1 ~ ";" ~ rExpr ~ ";" ~ rStatement2 ~ ")" ~ "{" ~ rBlock ~ "}" => For(rStatement1, rExpr, rStatement2, rBlock) }

    def pWhile: PackratParser[While] =
        "while" ~ "(" ~ pExpr ~ ")" ~ "{" ~ pBlock ~ "}" ^^
            { case "while" ~ "(" ~ rExpr ~ ")" ~ "{" ~ rBlock ~ "}" => While(rExpr, rBlock) }

    def pBreak: PackratParser[Break] =
        "break" ^^^ Break()

    def pAssign: PackratParser[Assign] =
        pName ~ "=" ~ pExpr ^^ { case rName ~ "=" ~ rExpr => Assign(rName, rExpr) } // TODO make pVar?

    def pDeclAssign: PackratParser[DeclAssign] =
        pVarDecl ~ "=" ~ pExpr ^^ { case rVarDecl ~ "=" ~ rExpr => DeclAssign(rVarDecl, rExpr) }

    def pAddAssign: PackratParser[AddAssign] =
        pName ~ "+=" ~ pExpr ^^ { case rName ~ "=" ~ rExpr => AddAssign(rName, rExpr) }

    def pSubAssign: PackratParser[SubAssign] =
        pName ~ "-=" ~ pExpr ^^ { case rName ~ "-=" ~ rExpr => SubAssign(rName, rExpr) }

    def pMultAssign: PackratParser[MultAssign] =
        pName ~ "*=" ~ pExpr ^^ { case rName ~ "*=" ~ rExpr => MultAssign(rName, rExpr) }

    def pDivAssign: PackratParser[DivAssign] =
        pName ~ "/=" ~ pExpr ^^ { case rName ~ "=" ~ rExpr => DivAssign(rName, rExpr) }

    def pModAssign: PackratParser[ModAssign] =
        pName ~ "%=" ~ pExpr ^^ { case rName ~ "=" ~ rExpr => ModAssign(rName, rExpr) }


    //    lazy val pStatement: PackratParser[Statement] =
    //        (
    //            rep1sep(pStatement, ";") ^^ Block
    //        |   pIfThenElse )
    //
    //    def
}
