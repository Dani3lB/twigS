object Main extends App {

  val input = "Hello, {{ name }}!"
  val ast = TemplateParser.parse(input)
  println(ast)

}
