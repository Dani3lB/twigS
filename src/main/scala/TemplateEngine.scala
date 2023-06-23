import model._

class TemplateEngine {
  private var variables: Map[String, Any] = Map.empty

  def render(templateString: String, data: Map[String, Any]): Either[String, String] = {
    TemplateParser.parse(templateString) match {
      case Right(ast) =>
        variables = data
        Right(renderAst(ast))
      case Left(msg) => Left(msg)
    }
  }

  protected def renderAst(ast: List[AstNode]): String = {
    ast.map {
      case Text(value) =>
        value
      case ShowVariable(name) =>
        variables.get(name) match {
          case Some(value) => value.toString
          case None => s"{{ $name }}"
        }
      case Block(expression) => convertToTwigSpecification(evaluateExpression(expression))
      case SetVariable(name, expression) =>
        val evaluatedValue = evaluateExpression(expression)
        variables += name -> convertToTwigSpecification(evaluatedValue)
        ""
      case If(condition, body) =>
        if (evaluateExpression(condition).asInstanceOf[Boolean]) renderAst(body) else ""
      case IfElse(condition, ifBody, elseBody) =>
        if (evaluateExpression(condition).asInstanceOf[Boolean]) renderAst(ifBody) else renderAst(elseBody)
    }.mkString
  }

  protected def convertToTwigSpecification(value: Any): Any = {
    value match {
      case bool: Boolean =>
        if (bool) 1 else ""
      case array: List[Any] =>
        "Array"
      case _ =>
        value
    }
  }

  protected def evaluateExpression(expression: Expression): Any = {
    expression match {
      case Literal(value) => value
      case Integer(value) => value
      case Bool(value) => value
      case Array(values) => values.map(evaluateExpression)
      case Identifier(name) => variables.getOrElse(name, "")
      case Equals(l, r) => evaluateExpression(l) == evaluateExpression(r)
      case Add(l, r) => evaluateExpression(l).asInstanceOf[Int] + evaluateExpression(r).asInstanceOf[Int]
      case Multiply(l, r) => evaluateExpression(l).asInstanceOf[Int] * evaluateExpression(r).asInstanceOf[Int]
      case Join(left, separator) =>
        val evaluatedArray = evaluateExpression(left).asInstanceOf[List[Any]]
        val evaluatedSeparator = separator.map(evaluateExpression).getOrElse("")
        evaluatedArray.mkString(evaluatedSeparator.toString)
      case CustomFilter(name, left, parameters) => handleCustomFilter(name, left, parameters)
      case Range(from, to) =>
        val evaluatedFrom = evaluateExpression(from).asInstanceOf[Int]
        val evaluatedTo = evaluateExpression(to).asInstanceOf[Int]
        (evaluatedFrom to evaluatedTo).toList
    }
  }

  protected def handleCustomFilter(name: Identifier, left: Expression, parameters: List[Expression]): Any = ???
}
