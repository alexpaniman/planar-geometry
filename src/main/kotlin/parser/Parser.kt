package parser

import lexer.Token
import lexer.TokenType
import objects.PlanarObject
import objects.line.Line
import objects.point.AnyPoint
import objects.triangle.ThreePointsTriangle

class Parser {
    fun parse(tokens: List<Token>): List<PlanarObject> {
        val objects = mutableListOf<PlanarObject>()

        for (index in tokens.indices)
            when(tokens[index].token) {
                TokenType.DEF -> when(tokens[index + 1].symbol) {
                    "triangle" -> {
                        val points = (0..2)
                            .map { AnyPoint() }
                            .toTypedArray()

                        objects.add(
                            ThreePointsTriangle(
                                points[0],
                                points[1],
                                points[2]
                            )
                        )
                    }

                    "line" -> {
                        val points = (0..1)
                            .map { AnyPoint() }
                            .toTypedArray()

                        objects.add(
                            Line(
                                points[0],
                                points[1]
                            )
                        )
                    }
                }

                else -> {}
            }

        return objects
    }
}