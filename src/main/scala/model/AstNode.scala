package model

sealed trait AstNode
case class Text(value: String) extends AstNode
case class ShowVariable(name: String) extends AstNode
case class Block(block: Expression) extends AstNode
case class SetVariable(name: String, value: Expression) extends AstNode

sealed trait Expression extends AstNode
case class Literal(value: String) extends Expression
case class Integer(value: Int) extends Expression
// todo float
case class Bool(value: Boolean) extends Expression
case class Array(values: List[Expression]) extends Expression
case class Identifier(name: String) extends Expression

case class Equals(left: Expression, right: Expression) extends Expression
case class Add(left: Expression, right: Expression) extends Expression
case class Multiply(left: Expression, right: Expression) extends Expression
// todo or,and,subtract,divide

final case class If(condition: Expression, body: List[AstNode]) extends AstNode
final case class IfElse(condition: Expression, ifBody: List[AstNode], elseBody: List[AstNode]) extends AstNode

sealed trait Filter extends Expression
case class Join(left: Expression, separator: Option[Expression]) extends Filter
case class CustomFilter(name: Identifier, left: Expression, parameters: List[Expression]) extends Filter

sealed trait Function extends Expression
case class Range(from: Expression, to: Expression) extends Function

// todo macro
// todo field access