package parser

import scala.util.parsing.combinator._

// Today I realized the difference with defining a function in () and {} when u want to use | as an Op

object BSParser extends JavaTokenParsers with PackratParsers {
    //def apply(s: String): ParseResult[FuncDecl] = parseAll(pFuncDecl, s)
    def apply(s: String): ParseResult[List[FuncDecl]] = parseAll(rep(pFuncDecl), s)

    def pFuncDecl: PackratParser[FuncDecl] =
        pType ~ pName ~ pParams ~ pBlock ^^
            { case rType ~ rName ~ rParams ~ rBlock => FuncDecl(rType, rName, rParams, rBlock) }

    def pType: PackratParser[Ty] = (
            "int"       ^^^ IntTy
        |   "double"    ^^^ DoubleTy
        |   "bool"      ^^^ BoolTy
        |   "char"      ^^^ CharTy
        |   "void"      ^^^ VoidTy
    )

    def pName: PackratParser[String] = ident ^^ { rIdent => rIdent }

    def pParams: PackratParser[List[VarDecl]] = "(" ~ repsep(pVarDecl, ",") ~ ")" ^^
        { case "(" ~ rParams ~ ")" => rParams }

    def pBlock: PackratParser[Block] = "{" ~ rep(pStatement) ~ "}" ^^
        { case "{" ~ rBlock ~ "}" => Block(rBlock)}

    def pVarDecl: PackratParser[VarDecl] =
        pType ~ pName ^^ { case rType ~ rName => VarDecl(rType, rName) }

    def pStatement: PackratParser[Statement] = pStatementBlock

    def pStatementBlock: PackratParser[Statement] = ( // TODO better name
            pIfThenElse
        |   pFor
        |   pWhile
        |   pStatementSemicolon
    )

    def pStatementSemicolon: PackratParser[Statement] = pStatementControlFlow <~ ";" // TODO better name

    def pStatementControlFlow: PackratParser[Statement] = ( // TODO better name
            pCall
        |   pBreak
        |   pReturn
        |   pStatementDecl
    )

    def pStatementDecl: PackratParser[Statement] = ( // TODO better name
            pDeclAssign
        |   pVarDecl
        |   pStatementAssign
    )

    def pStatementAssign: PackratParser[Statement] = ( // TODO better name
            pAssign
        |   pAddAssign
        |   pSubAssign
        |   pMultAssign
        |   pDivAssign
        |   pModAssign
        |   pInc            // TODO differentiation between ++i and i++
        |   pDec
    )

    def pReturn: PackratParser[Return] = (
            "return" ~ pExpr ^^ { case "return" ~ rExpr => Return(rExpr) }
        |   "return" ^^^ Return(NullLit())
    )

    def pIfThenElse: PackratParser[IfThenElse] = "if" ~> pThenElse

    def pThenElse: PackratParser[IfThenElse] = (
            "(" ~ pExpr ~ ")" ~ pBlock ~ "else if" ~ pThenElse ^^
                { case "(" ~ rExpr ~ ")" ~ rBlock ~ "else if" ~ rThenElse
                => IfThenElse(rExpr, rBlock, Block(List(rThenElse)))}
        |
            "(" ~ pExpr ~ ")" ~ pBlock ~ "else" ~ pBlock ^^
                { case "(" ~ rExpr ~ ")" ~ rBlockTrue ~ "else" ~ rBlockFalse
                => IfThenElse(rExpr, rBlockTrue, rBlockFalse) }
        |   "(" ~ pExpr ~ ")" ~ pBlock ^^
            { case "(" ~ rExpr ~ ")" ~ rBlockTrue
            => IfThenElse(rExpr, rBlockTrue, Block(List())) }
    )

    def pExpr: PackratParser[Expr] = pBoolOp

    lazy val pBoolOp: PackratParser[Expr] = ( // TODO Order of operations for && and || etc
            pBoolOp ~ ">"  ~ pBoolOp ^^ { case rBoolOp1 ~ ">"  ~ rBoolOp2 => GtOp(rBoolOp1, rBoolOp2) }
        |   pBoolOp ~ "<"  ~ pBoolOp ^^ { case rBoolOp1 ~ "<"  ~ rBoolOp2 => LtOp(rBoolOp1, rBoolOp2) }
        |   pBoolOp ~ ">=" ~ pBoolOp ^^ { case rBoolOp1 ~ ">=" ~ rBoolOp2 => GeqOp(rBoolOp1, rBoolOp2) }
        |   pBoolOp ~ "<=" ~ pBoolOp ^^ { case rBoolOp1 ~ "<=" ~ rBoolOp2 => LeqOp(rBoolOp1, rBoolOp2) }
        |   pBoolOp ~ "==" ~ pBoolOp ^^ { case rBoolOp1 ~ "==" ~ rBoolOp2 => EqOp(rBoolOp1, rBoolOp2) }
        |   pBoolOp ~ "!=" ~ pBoolOp ^^ { case rBoolOp1 ~ "!=" ~ rBoolOp2 => NeqOp(rBoolOp1, rBoolOp2) }
        |   pBoolOp ~ "||" ~ pBoolOp ^^ { case rBoolOp1 ~ "||" ~ rBoolOp2 => OrOp(rBoolOp1, rBoolOp2) }
        |   pBoolOp ~ "&&" ~ pBoolOp ^^ { case rBoolOp1 ~ "&&" ~ rBoolOp2 => AndOp(rBoolOp1, rBoolOp2) }
        |   pPlusMinus
    )

    lazy val pPlusMinus: PackratParser[Expr] = ( // TODO Why is | not happy on the next line?
            pPlusMinus ~ "+" ~ pTerm ^^ { case rExpr ~ "+" ~ rTerm => AddOp(rExpr, rTerm) }
        |   pPlusMinus ~ "-" ~ pTerm ^^ { case rExpr ~ "-" ~ rTerm => SubOp(rExpr, rTerm) }
        |   pTerm
    )

    lazy val pTerm: PackratParser[Expr] = (
            pTerm ~ "*" ~ pFact ^^ { case rTerm ~ "*" ~ rFact => MultOp(rTerm, rFact) }
        |   pTerm ~ "/" ~ pFact ^^ { case rTerm ~ "/" ~ rFact => DivOp(rTerm, rFact) }
        |   pBinOp
    )

    def pBinOp: PackratParser[Expr] = (
            "-" ~ pFact ^^ { case "-" ~ rFact => NegateOp(rFact) } // prevents from parsing -15 as NegateOp(IntLit(15))
        |   "!" ~ pLit  ^^ { case "!" ~ rFact => NotOp(rFact) }
        |   pLit
    )

    def pLit: PackratParser[Expr] = ( // TODO better name
            pLiteral
        |   pFact
    )

    def pFact: PackratParser[Expr] = (
            pEval
        |   pVar
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
            "true"  ^^^ BoolLit(true)
        |   "false" ^^^ BoolLit(false)
    )

    // def pCharLit: PackratParser[CharLit] = "'" ~ ".".r ~ "'" { case "'" ~ rChar ~ "'" => CharLit(rChar) }

    def pVar: PackratParser[Var] = pName ^^ { case rName => Var(rName) }

    def pEval: PackratParser[Eval] = pCall ^^ { case rCall => Eval(rCall) }

    def pCall: PackratParser[Call] = pName ~ "(" ~  pArgs ~ ")" ^^ { case rName ~ "(" ~ rArgs ~ ")" => Call(rName, rArgs) }

    def pArgs: PackratParser[List[Expr]] = repsep(pExpr, ",")

    def pFor: PackratParser[For] =
        "for" ~ "(" ~ pStatementDecl ~ ";" ~ pExpr ~ ";" ~ pStatementAssign ~ ")" ~ pBlock ^^
            { case "for" ~ "(" ~ rStatementDecl ~ ";" ~ rExpr ~ ";" ~ rStatementAssign ~ ")" ~ rBlock => For(rStatementDecl, rExpr, rStatementAssign, rBlock) }

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
        pName ~ "+=" ~ pExpr ^^ { case rName ~ "+=" ~ rExpr => AddAssign(rName, rExpr) }

    def pSubAssign: PackratParser[SubAssign] =
        pName ~ "-=" ~ pExpr ^^ { case rName ~ "-=" ~ rExpr => SubAssign(rName, rExpr) }

    def pMultAssign: PackratParser[MultAssign] =
        pName ~ "*=" ~ pExpr ^^ { case rName ~ "*=" ~ rExpr => MultAssign(rName, rExpr) }

    def pDivAssign: PackratParser[DivAssign] =
        pName ~ "/=" ~ pExpr ^^ { case rName ~ "/=" ~ rExpr => DivAssign(rName, rExpr) }

    def pModAssign: PackratParser[ModAssign] =
        pName ~ "%=" ~ pExpr ^^ { case rName ~ "%=" ~ rExpr => ModAssign(rName, rExpr) }

    def pInc: PackratParser[PostIncOp] =
        pName ~ "++" ^^ { case rName ~ "++" => PostIncOp(rName) }

    def pDec: PackratParser[PostIncOp] =
        pName ~ "--" ^^ { case rName ~ "++" => PostIncOp(rName) }

//    lazy val pStatement: PackratParser[Statement] =
//        (
//            rep1sep(pStatement, ";") ^^ Block
//        |   pIfThenElse )
//
//    def
}
