package org.kotlin.formatter.scanning

import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.psiUtil.children
import org.kotlin.formatter.BeginToken
import org.kotlin.formatter.ClosingSynchronizedBreakToken
import org.kotlin.formatter.EndToken
import org.kotlin.formatter.LeafNodeToken
import org.kotlin.formatter.State
import org.kotlin.formatter.Token
import org.kotlin.formatter.WhitespaceToken
import org.kotlin.formatter.scanning.nodepattern.nodePattern

internal class IfExpressionScanner(private val kotlinScanner: KotlinScanner) : NodeScanner {
    private val nodePattern = nodePattern {
        exactlyOne {
            nodeOfType(KtTokens.IF_KEYWORD)
            possibleWhitespace()
            nodeOfType(KtTokens.LPAR)
            nodeOfType(KtNodeTypes.CONDITION) andThen { nodes ->
                val tokens = kotlinScanner.scanNodes(nodes, ScannerState.STATEMENT)
                listOf(
                    LeafNodeToken("if ("),
                    BeginToken(length = lengthOfTokens(tokens), state = State.CODE),
                    *tokens.toTypedArray(),
                    EndToken
                )
            }
            nodeOfType(KtTokens.RPAR)
            possibleWhitespace()
            nodeOfType(KtNodeTypes.THEN) andThen { nodes ->
                listOf(
                    LeafNodeToken(") "),
                    *kotlinScanner.scanNodes(nodes, ScannerState.STATEMENT).toTypedArray()
                )
            }
        }
        zeroOrOne {
            possibleWhitespace()
            nodeOfType(KtTokens.ELSE_KEYWORD)
            possibleWhitespace()
            nodeOfType(KtNodeTypes.ELSE) andThen { nodes ->
                listOf(
                    WhitespaceToken(length = 5, content = " "),
                    LeafNodeToken("else "),
                    *kotlinScanner.scanNodes(nodes, ScannerState.STATEMENT).toTypedArray()
                )
            }
        }
        end()
    }

    override fun scan(node: ASTNode, scannerState: ScannerState): List<Token> =
        inBeginEndBlock(nodePattern.matchSequence(node.children().asIterable()), State.CODE)
}
