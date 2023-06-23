import model.{Expression, Identifier}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class TemplateEngineSpec extends AnyWordSpec with Matchers {

  "first test" should {
    "pass" in {
      1 shouldBe 1
    }
  }

  val engine = new TemplateEngine()

  "TemplateEngine" should {
    "render template with a variable" in {
      val input = "Hello, {{ name }}!"
      val expectedOutput = "Hello, World!"
      val actualOutput = engine.render(input, Map("name" -> "World"))
      actualOutput shouldBe Right(expectedOutput)
    }

    "render template with multiple variables" in {
      val input = "Hello, {{ name1 }} {{name2}}-{{name3}}{{name4}}!"
      val expectedOutput = "Hello, Firstname Lastname-TheBest!"
      val variables = Map(
        "name1" -> "Firstname",
        "name2" -> "Lastname",
        "name3" -> "The",
        "name4" -> "Best"
      )
      val actualOutput = engine.render(input, variables)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates with variable setting - single quote literal" in {
      val input = "Hello, {% set name = 'World' %}{{ name }}!"
      val expectedOutput = "Hello, World!"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates with variable setting - double quote literal" in {
      val input = "Hello, {% set name = \"World\" %}{{ name }}!"
      val expectedOutput = "Hello, World!"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates with variable setting - identifier" in {
      val input = "Hello, {% set name = parameter %}{{ name }}!"
      val expectedOutput = "Hello, World!"
      val actualOutput = engine.render(input, Map("parameter" -> "World"))
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates with variable setting - integer" in {
      val input = "Hello, {% set name = 3 %}{{ name }}!"
      val expectedOutput = "Hello, 3!"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates - equals false" in {
      val input = "Hello, {{ 3 == 4 }}!"
      val expectedOutput = "Hello, !"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates - equals true" in {
      val input = "Hello, {{ 3 == 3 }}!"
      val expectedOutput = "Hello, 1!"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates - parenthesis parsing" in {
      val input = "Hello, {{ (3 == 3) == (4 == 4) }}!"
      val expectedOutput = "Hello, 1!"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates - parenthesis evaluation" in {
      val input = "Hello, {{ 3 == (3 == 4) == 4 }}!"
      val expectedOutput = "Hello, !"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates - double parenthesis evaluation" in {
      val input = "Hello, {{ true == ((3 == 4) == false) }}!"
      val expectedOutput = "Hello, 1!"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates - add and multiply" in {
      val input = "Hello, {{ 1+2*2 }}!"
      val expectedOutput = "Hello, 5!"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates - proper operator precedence" in {
      val input = "Hello, {{ 2*2+1 }}!"
      val expectedOutput = "Hello, 5!"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates - if true" in {
      val input = "Hello, {% if 3 == 3 %}true{% endif %}!"
      val expectedOutput = "Hello, true!"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates - if false" in {
      val input = "Hello, {% if 3 == 4 %}true{% endif %}!"
      val expectedOutput = "Hello, !"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates - if-else false" in {
      val input = "Hello, {% if 3 == 4 %}true{% else %}false{% endif %}!"
      val expectedOutput = "Hello, false!"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates - if-else true" in {
      val input = "Hello, {% if 3 == 3 %}true{% else %}false{% endif %}!"
      val expectedOutput = "Hello, true!"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates - two level if-else" in {
      val input =
        "Hello, " +
        "{% if 3 == 3 %}" +
          "true" +
          "{% if 3 == 5 %}" +
            "true" +
          "{% else %}" +
            "false" +
          "{% endif %}" +
        "{% else %}" +
          "false" +
        "{% endif %}!"
      val expectedOutput = "Hello, truefalse!"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }
    // todo elif

    "render templates - array" in {
      val input = "Hello, {{ [1, 2, 2+1] }}!"
      val expectedOutput = "Hello, Array!"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates with filters - join" in {
      val input = "Hello, {{ [1, 2, 2+1]|join }}!"
      val expectedOutput = "Hello, 123!"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates with filters - join with separator" in {
      val input = "Hello, {{ [1, 2, 2+1]|join(1+1) }}!"
      val expectedOutput = "Hello, 12223!"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }
    // todo: other filters

    "render templates with a custom filter" in {
      class CustomTemplateEngine extends TemplateEngine {
        override def handleCustomFilter(name: Identifier, left: Expression, parameters: List[Expression]): Any =
          if(name.name == "doubleJoin") {
            val evaluatedArray = evaluateExpression(left).asInstanceOf[List[Any]]
            val evaluatedSeparator = parameters.headOption.map(evaluateExpression).getOrElse("")
            evaluatedArray.mkString(evaluatedSeparator.toString.repeat(2))
          } else {
            throw new RuntimeException(s"Unknown filter: ${name.name}")
          }
      }
      val customEngine = new CustomTemplateEngine
      val input = "Hello, {{ [1, 2, 2+1]|doubleJoin('x') }}!"
      val expectedOutput = "Hello, 1xx2xx3!"
      val actualOutput = customEngine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }

    "render templates with functions - range" in {
      val input = "Hello, {{ range(0,2)|join }}!"
      val expectedOutput = "Hello, 012!"
      val actualOutput = engine.render(input, Map.empty)
      actualOutput shouldBe Right(expectedOutput)
    }
    // todo range for strings
    // todo range step
    // todo: other functions
    // todo: custom function

    // todo: macro
    // todo new line handling
    // todo failure tests: parse error (wrong keyword), no variable
  }
}
