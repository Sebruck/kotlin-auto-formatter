package org.kotlin.formatter.scanning

import org.jetbrains.kotlin.KtNodeTypes
import org.jetbrains.kotlin.com.intellij.lang.ASTNode
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.psiUtil.children
import org.kotlin.formatter.BlockFromMarkerToken
import org.kotlin.formatter.LeafNodeToken
import org.kotlin.formatter.MarkerToken
import org.kotlin.formatter.Token
import org.kotlin.formatter.WhitespaceToken
import org.kotlin.formatter.scanning.nodepattern.nodePattern

/** A [NodeScanner] for `typealias` declarations. */
internal class TypealiasScanner(private val kotlinScanner: KotlinScanner) : NodeScanner {
    private val modifierListScanner =
        ModifierListScanner(kotlinScanner, breakMode = ModifierListScanner.BreakMode.TYPE)
    private val nodePattern =
        nodePattern {
            optionalKDoc(kotlinScanner, modifierListScanner)
            possibleWhitespaceWithComment()
            zeroOrOne {
                nodeOfType(KtNodeTypes.MODIFIER_LIST) thenMapToTokens { nodes ->
                    kotlinScanner.scanNodes(nodes, ScannerState.STATEMENT)
                        .plus(WhitespaceToken(content = " "))
                }
                whitespace()
            } thenMapTokens { tokens -> listOf(MarkerToken).plus(tokens) }
            nodeOfType(KtTokens.TYPE_ALIAS_KEYWORD) thenMapToTokens {
                listOf(LeafNodeToken("typealias"))
            }
            whitespace() thenMapToTokens { listOf(WhitespaceToken(content = " ")) }
            nodeOfType(KtTokens.IDENTIFIER) thenMapToTokens { nodes ->
                listOf(LeafNodeToken(nodes[0].text))
            }
            possibleWhitespace()
            zeroOrOne {
                nodeOfType(KtNodeTypes.TYPE_PARAMETER_LIST) thenMapToTokens { nodes ->
                    kotlinScanner.scanNodes(nodes, ScannerState.STATEMENT)
                }
            }
            possibleWhitespace()
            nodeOfType(KtTokens.EQ) thenMapToTokens { listOf(LeafNodeToken(" =")) }
            possibleWhitespaceWithComment() thenMapTokens { tokens ->
                if (tokens.isEmpty()) {
                    listOf(WhitespaceToken(content = " "))
                } else {
                    tokens
                }
            }
            nodeOfType(KtNodeTypes.TYPE_REFERENCE) thenMapToTokens { nodes ->
                kotlinScanner.scanNodes(nodes, ScannerState.STATEMENT).plus(BlockFromMarkerToken)
            }
            end()
        }

    override fun scan(node: ASTNode, scannerState: ScannerState): List<Token> =
        nodePattern.matchSequence(node.children().asIterable())
}
