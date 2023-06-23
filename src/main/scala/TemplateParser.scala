import model._

import scala.util.parsing.combinator._

object TemplateParser extends JavaTokenParsers with PackratParsers {

  override def skipWhitespace: Boolean = false

  def parse(templateString: String): Either[String, List[AstNode]] = {
    parseAll(combinedParser, templateString) match {
      case Success(result, _) => Right(result)
      case NoSuccess(msg, next) => Left(s"Error parsing template: $msg, at ${next.pos}")
    }
  }

  private lazy val combinedParser: PackratParser[List[AstNode]] = rep(
    block | showVariable | setVariable | ifElse | `if` | text | failure("unexpected input")
  ) ^^ { nodes =>
    println(s"Parsed AST: $nodes")
    nodes
  }

  private lazy val text: PackratParser[Text] = """((?s)(?:(?!\{\{|\{%).)+)""".r ^^ Text

  private lazy val showVariable: PackratParser[ShowVariable] = "{{" ~> optWS(ident) <~ "}}" ^^ ShowVariable

  private lazy val block: PackratParser[Block] = "{{" ~> optWS(expression) <~ "}}" ^^ Block

  private def optWS[T](parser: PackratParser[T]) = opt("""\s+""".r) ~> parser <~ opt("""\s+""".r)
  private def optWS(string: String) = opt("""\s+""".r) ~> string <~ opt("""\s+""".r)

  private lazy val setVariable: PackratParser[SetVariable] = {
    "{%" ~> optWS("set") ~> optWS(ident) ~ "=" ~ optWS(expression) <~ "%}" ^^ {
      case name ~ "=" ~ value => SetVariable(name, value)
    }
  }

  private lazy val expression: PackratParser[Expression] = {
    filters | functions | equals | add
  }

  private lazy val filters = {
    join | customFilter
  }

  private lazy val functions = {
    range
  }

  private lazy val atom: PackratParser[Expression] = {
    array | literal | integer | boolean | identifier | "(" ~> expression <~ ")"
  }

  private lazy val literal: PackratParser[Literal] = singleQuoteLiteral | doubleQuoteLiteral

  private lazy val singleQuoteLiteral: PackratParser[Literal] = ("'" + literalInnerRegex ++ "'").r ^^ { str =>
    Literal(str.replaceAll("'", ""))
  }

  private lazy val doubleQuoteLiteral: PackratParser[Literal] = ("\"" + literalInnerRegex ++ "\"").r ^^ { str =>
    Literal(str.replaceAll("\"", ""))
  }

  private val literalInnerRegex = """([^"\x00-\x1F\x7F\\]|\\[\\'"bfnrt]|\\u[a-fA-F0-9]{4})*"""

  private lazy val integer: PackratParser[Integer] = wholeNumber ^^ { str => Integer(str.toInt) }

  private lazy val boolean: PackratParser[Bool] = ("true" | "false") ^^ {
    case "true" => Bool(true)
    case "false" => Bool(false)
  }

  private lazy val array: PackratParser[Array] = "[" ~> repsep(expression, optWS(",")) <~ "]" ^^ Array

  private lazy val identifier: PackratParser[Identifier] = ident ^^ Identifier

  private lazy val equals: PackratParser[Equals] = optWS(expression) ~ "==" ~ optWS(expression) ^^ {
    case left ~ "==" ~ right => Equals(left, right)
  }

  private lazy val add: PackratParser[Expression] = multiply ~ rep("+" ~> optWS(multiply)) ^^ {
    case left ~ rights => rights.foldLeft(left)((acc, right) => Add(acc, right))
  }

  // todo create precedence1 etc group methods?
  private lazy val multiply: PackratParser[Expression] = atom ~ rep("*" ~> optWS(atom)) ^^ {
    case left ~ rights => rights.foldLeft(left)((acc, right) => Multiply(acc, right))
  }

  private lazy val `if`: PackratParser[If] = {
    ("{%" ~> optWS("if") ~> optWS(expression) <~ "%}") ~ (optWS(combinedParser) <~ "{%" <~ optWS("endif") <~ "%}") ^^ {
        case condition ~ body => If(condition, body)
    }
  }

  private lazy val ifElse: PackratParser[IfElse] = {
    ("{%" ~> optWS("if") ~> optWS(expression) <~ "%}") ~ (optWS(combinedParser) <~ "{%" <~ optWS("else") <~ "%}") ~ (optWS(combinedParser) <~ "{%" <~ optWS("endif") <~ "%}") ^^ {
        case condition ~ ifBody ~ elseBody => IfElse(condition, ifBody, elseBody)
    }
  }

  private lazy val join: PackratParser[Join] = {
    (expression <~ "|" <~ "join") ~ opt("(" ~> optWS(expression) <~ ")") ^^ {
      case array ~ separator => Join(array, separator)
    }
  }

  private lazy val customFilter: PackratParser[CustomFilter] = {
    (expression <~ "|") ~ identifier ~ rep("(" ~> optWS(expression) <~ ")") ^^ {
      case expression ~ filterName ~ argument => CustomFilter(filterName, expression, argument)
    }
  }

  private lazy val range: PackratParser[Range] = {
    "range" ~> "(" ~> optWS(expression) ~ "," ~ optWS(expression) <~ ")" ^^ {
      case from ~ "," ~ to => Range(from, to)
    }
  }
}
