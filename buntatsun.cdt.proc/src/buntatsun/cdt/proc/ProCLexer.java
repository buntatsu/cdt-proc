package buntatsun.cdt.proc;

import org.eclipse.cdt.core.parser.IGCCToken;
import org.eclipse.cdt.core.parser.IToken;
import org.eclipse.cdt.core.parser.OffsetLimitReachedException;
import org.eclipse.cdt.internal.core.parser.scanner.AbstractCharArray;
import org.eclipse.cdt.internal.core.parser.scanner.ILexerLog;
import org.eclipse.cdt.internal.core.parser.scanner.Lexer;
import org.eclipse.cdt.internal.core.parser.scanner.Token;

@SuppressWarnings("restriction")
public class ProCLexer extends Lexer {

	public ProCLexer(AbstractCharArray input, int start, int end,
			LexerOptions options, ILexerLog log, Object source) {
		super(input, start, end, options, log, source);
	}

	public ProCLexer(AbstractCharArray input, LexerOptions options,
			ILexerLog log, Object source) {
		super(input, options, log, source);
	}

	public ProCLexer(char[] input, LexerOptions options, ILexerLog log,
			Object source) {
		super(input, options, log, source);
	}

	public boolean isInsideProCBlock = false;

	@Override
	protected Token fetchToken() throws OffsetLimitReachedException {
		while (true) {
			final int start= fOffset;
			final int c= fCharPhase3;
			final int d= nextCharPhase3();
			switch (c) {
			case END_OF_INPUT:
				return newToken(IToken.tEND_OF_INPUT, start);
			case '\n':
				fInsideIncludeDirective= false;
				return newToken(Lexer.tNEWLINE, start);
			case ' ':
			case '\t':
			case 0xb:  // vertical tab
			case '\f':
			case '\r':
				continue;

			case 'L':
				switch (d) {
				case 'R':
					if (fOptions.fSupportRawStringLiterals) {
						markPhase3();
						if (nextCharPhase3() == '"') {
							nextCharPhase3();
							return rawStringLiteral(start, 3, IToken.tLSTRING);
						}
						restorePhase3();
					}
					break;
				case '"':
					nextCharPhase3();
					return stringLiteral(start, 2, IToken.tLSTRING);
				case '\'':
					nextCharPhase3();
					return charLiteral(start, IToken.tLCHAR);
				}
				return identifier(start, 1);

			case 'u':
			case 'U':
				if (fOptions.fSupportUTFLiterals) {
					switch (d) {
					case 'R':
						if (fOptions.fSupportRawStringLiterals) {
							markPhase3();
							if (nextCharPhase3() == '"') {
								nextCharPhase3();
								return rawStringLiteral(start, 3, c == 'u' ? IToken.tUTF16STRING : IToken.tUTF32STRING);
							}
							restorePhase3();
						}
						break;
					case '"':
						nextCharPhase3();
						return stringLiteral(start, 2, c == 'u' ? IToken.tUTF16STRING : IToken.tUTF32STRING);
					case '\'':
						nextCharPhase3();
						return charLiteral(start, c == 'u' ? IToken.tUTF16CHAR : IToken.tUTF32CHAR);
					case '8':
						if (c == 'u') {
							markPhase3();
							switch (nextCharPhase3()) {
							case 'R':
								if (fOptions.fSupportRawStringLiterals && nextCharPhase3() == '"') {
									nextCharPhase3();
									return rawStringLiteral(start, 4, IToken.tSTRING);
								}
								break;
							case '"':
								nextCharPhase3();
								return stringLiteral(start, 3, IToken.tSTRING);
							}
							restorePhase3();
						}
						break;
					}
				}
				return identifier(start, 1);

			case 'R':
				if (fOptions.fSupportRawStringLiterals && d == '"') {
					nextCharPhase3();
					return rawStringLiteral(start, 2, IToken.tSTRING);
				}
				return identifier(start, 1);

			case '"':
				if (fInsideIncludeDirective) {
					return headerName(start, true);
				}
				return stringLiteral(start, 1, IToken.tSTRING);

			case '\'':
				return charLiteral(start, IToken.tCHAR);

			case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': case 'h': case 'i':
			case 'j': case 'k': case 'l': case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
			case 's': case 't': 		  case 'v': case 'w': case 'x': case 'y': case 'z':
			case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I':
			case 'J': case 'K': 		  case 'M': case 'N': case 'O': case 'P': case 'Q':
			case 'S': case 'T': 		  case 'V': case 'W': case 'X': case 'Y': case 'Z':
			case '_':
				return identifier(start, 1);

			case '$':
				if (fOptions.fSupportDollarInIdentifiers) {
					return identifier(start, 1);
				}
				break;
			case '@':
				if (fOptions.fSupportAtSignInIdentifiers) {
					return identifier(start, 1);
				}
				break;

			case '\\':
				switch (d) {
				case 'u': case 'U':
					nextCharPhase3();
					return identifier(start, 2);
				}
				return newToken(tOTHER_CHARACTER, start, 1);

			case '0': case '1': case '2': case '3': case '4':
			case '5': case '6': case '7': case '8': case '9':
				return number(start, 1, false);

			case '.':
				switch (d) {
				case '0': case '1': case '2': case '3': case '4':
				case '5': case '6': case '7': case '8': case '9':
					nextCharPhase3();
					return number(start, 2, true);

				case '.':
					markPhase3();
					if (nextCharPhase3() == '.') {
						nextCharPhase3();
						return newToken(IToken.tELLIPSIS, start);
					}
					restorePhase3();
					break;

				case '*':
					nextCharPhase3();
					return newToken(IToken.tDOTSTAR, start);
				}
				return newToken(IToken.tDOT, start);

			case '#':
				if (d == '#') {
					nextCharPhase3();
					return newToken(IToken.tPOUNDPOUND, start);
				}
				return newToken(IToken.tPOUND, start);

			case '{':
				return newToken(IToken.tLBRACE, start);
			case '}':
				return newToken(IToken.tRBRACE, start);
			case '[':
				return newToken(IToken.tLBRACKET, start);
			case ']':
				return newToken(IToken.tRBRACKET, start);
			case '(':
				return newToken(IToken.tLPAREN, start);
			case ')':
				return newToken(IToken.tRPAREN, start);
			case ';':
				return newToken(IToken.tSEMI, start);

			case ':':
				switch (d) {
				case ':':
					nextCharPhase3();
					return newToken(IToken.tCOLONCOLON, start);
				case '>':
					nextCharPhase3();
					return newDigraphToken(IToken.tRBRACKET, start);
				case '=':
					if (isInsideProCBlock) {
						/*
						 *	Pro*C := assign
						 */
						nextCharPhase3();
						return newToken(IProCToken.tASSIGN, start);
					}
				}
				return newToken(IToken.tCOLON, start);

			case '?':
				return newToken(IToken.tQUESTION, start);

			case '+':
				switch (d) {
				case '+':
					nextCharPhase3();
					return newToken(IToken.tINCR, start);
				case '=':
					nextCharPhase3();
					return newToken(IToken.tPLUSASSIGN, start);
				}
				return newToken(IToken.tPLUS, start);

			case '-':
				switch (d) {
				case '>':
					int e= nextCharPhase3();
					if (e == '*') {
						nextCharPhase3();
						return newToken(IToken.tARROWSTAR, start);
					}
					return newToken(IToken.tARROW, start);

				case '-':
					if (isInsideProCBlock) {
						/*
						 *	Pro*C -- single line comment
						 */
						nextCharPhase3();
						lineComment(start);
						continue;
					}

					nextCharPhase3();
					return newToken(IToken.tDECR, start);
				case '=':
					nextCharPhase3();
					return newToken(IToken.tMINUSASSIGN, start);
				}
				return newToken(IToken.tMINUS, start);

			case '*':
				if (d == '=') {
					nextCharPhase3();
					return newToken(IToken.tSTARASSIGN, start);
				}
				return newToken(IToken.tSTAR, start);

			case '/':
				switch (d) {
				case '=':
					nextCharPhase3();
					return newToken(IToken.tDIVASSIGN, start);
				case '/':
					nextCharPhase3();
					lineComment(start);
					continue;
				case '*':
					blockComment(start, '*');
					continue;
				case '%':
					if (fOptions.fSupportSlashPercentComments) {
						blockComment(start, '%');
						continue;
					}
					break;
				}
				return newToken(IToken.tDIV, start);

			case '%':
				switch (d) {
				case '=':
					nextCharPhase3();
					return newToken(IToken.tMODASSIGN, start);
				case '>':
					nextCharPhase3();
					return newDigraphToken(IToken.tRBRACE, start);
				case ':':
					final int e= nextCharPhase3();
					if (e == '%') {
						markPhase3();
						if (nextCharPhase3() == ':') {
							nextCharPhase3();
							return newDigraphToken(IToken.tPOUNDPOUND, start);
						}
						restorePhase3();
					}
					return newDigraphToken(IToken.tPOUND, start);
				}
				return newToken(IToken.tMOD, start);

			case '^':
				if (d == '=') {
					nextCharPhase3();
					return newToken(IToken.tXORASSIGN, start);
				}
				return newToken(IToken.tXOR, start);

			case '&':
				switch (d) {
				case '&':
					nextCharPhase3();
					return newToken(IToken.tAND, start);
				case '=':
					nextCharPhase3();
					return newToken(IToken.tAMPERASSIGN, start);
				}
				return newToken(IToken.tAMPER, start);

			case '|':
				switch (d) {
				case '|':
					nextCharPhase3();
					return newToken(IToken.tOR, start);
				case '=':
					nextCharPhase3();
					return newToken(IToken.tBITORASSIGN, start);
				}
				return newToken(IToken.tBITOR, start);

			case '~':
				return newToken(IToken.tBITCOMPLEMENT, start);

			case '!':
				if (d == '=') {
					nextCharPhase3();
					return newToken(IToken.tNOTEQUAL, start);
				}
				return newToken(IToken.tNOT, start);

			case '=':
				if (d == '=') {
					nextCharPhase3();
					return newToken(IToken.tEQUAL, start);
				}
				return newToken(IToken.tASSIGN, start);

			case '<':
				if (fInsideIncludeDirective) {
					return headerName(start, false);
				}

				switch (d) {
				case '=':
					nextCharPhase3();
					return newToken(IToken.tLTEQUAL, start);
				case '<':
					final int e= nextCharPhase3();
					if (e == '=') {
						nextCharPhase3();
						return newToken(IToken.tSHIFTLASSIGN, start);
					}
					return newToken(IToken.tSHIFTL, start);
				case '?':
					if (fOptions.fSupportMinAndMax) {
						nextCharPhase3();
						return newToken(IGCCToken.tMIN, start);
					}
					break;
				case ':':
					// 2.5-3
					markPhase3();
					if (nextCharPhase3() != ':') {
						return newDigraphToken(IToken.tLBRACKET, start);
					}
					switch (nextCharPhase3()) {
					case ':': case '>':
						restorePhase3();
						nextCharPhase3();
						return newDigraphToken(IToken.tLBRACKET, start);
					}
					restorePhase3();
					break;
				case '%':
					nextCharPhase3();
					return newDigraphToken(IToken.tLBRACE, start);
				}
				return newToken(IToken.tLT, start);

			case '>':
				switch (d) {
				case '=':
					nextCharPhase3();
					return newToken(IToken.tGTEQUAL, start);
				case '>':
					final int e= nextCharPhase3();
					if (e == '=') {
						nextCharPhase3();
						return newToken(IToken.tSHIFTRASSIGN, start);
					}
					return newToken(IToken.tSHIFTR, start);
				case '?':
					if (fOptions.fSupportMinAndMax) {
						nextCharPhase3();
						return newToken(IGCCToken.tMAX, start);
					}
					break;
				}
				return newToken(IToken.tGT, start);

			case ',':
				return newToken(IToken.tCOMMA, start);

			default:
				// in case we have some other letter to start an identifier
				if (Character.isUnicodeIdentifierStart((char) c)) {
					return identifier(start, 1);
				}
				break;
			}
			// handles for instance @
			return newToken(tOTHER_CHARACTER, start, 1);
		}
	}

	@Override
	protected Token identifier(int start, int length) {
		int tokenKind= IToken.tIDENTIFIER;
		boolean isPartOfIdentifier= true;
		int c= fCharPhase3;
		while (true) {
			switch (c) {
			case 'x': case 'X':
				if (length == 1) {
					/*
					 *	Pro*C check "EXEC"
					 */
					final int prev_c = fInput.get(fOffset - 1); // previous char
					switch (prev_c) {
					case 'e': case 'E':
						boolean doRestore = true;
						markPhase3();
						switch (nextCharPhase3()) {
						case 'e': case 'E':
							switch (nextCharPhase3()) {
							case 'c': case 'C':
								doRestore = false;
								length += 3;
								final int nc = nextCharPhase3();
								if (('a' <= nc && nc <= 'z') || ('A' <= nc && nc <= 'Z')
										|| ('0' <= nc && nc <= '9') || nc == '_') {
									;
								}
								else {
									tokenKind = IProCToken.tEXEC;
									isPartOfIdentifier = false;
								}
								break;
							}
							break;
						}
						if (doRestore) {
							restorePhase3();
						}
						break;
					}
				}
				break;

			case 'n': case 'N':
				if (length == 1 && isInsideProCBlock) {
					/*
					 * Pro*C check "END-EXEC"
					 */
					final int prev_c = fInput.get(fOffset - 1); // previous char
					switch (prev_c) {
					case 'e': case 'E':
						boolean doRestore = true;
						markPhase3();
						switch (nextCharPhase3()) {
						case 'd': case 'D':
							switch (nextCharPhase3()) {
							case '-':
								switch (nextCharPhase3()) {
								case 'e': case 'E':
									switch (nextCharPhase3()) {
									case 'x': case 'X':
										switch (nextCharPhase3()) {
										case 'e': case 'E':
											switch (nextCharPhase3()) {
											case 'c': case 'C':
												doRestore = false;
												length += 7;
												final int nc = nextCharPhase3();
												if (('a' <= nc && nc <= 'z') || ('A' <= nc && nc <= 'Z')
														|| ('0' <= nc && nc <= '9') || nc == '_') {
													;
												}
												else {
													isPartOfIdentifier = false;
												}
												break;
											}
											break;
										}
										break;
									}
									break;
								}
								break;
							}
							break;
						}
						if (doRestore) {
							restorePhase3();
						}
						break;
					}
				}
				break;

			case 'a': case 'b': case 'c': case 'd': case 'e': case 'f': case 'g': case 'h': case 'i':
			case 'j': case 'k': case 'l': case 'm': /* n */   case 'o': case 'p': case 'q': case 'r':
			case 's': case 't': case 'u': case 'v': case 'w': /* x */	case 'y': case 'z':
			case 'A': case 'B': case 'C': case 'D': case 'E': case 'F': case 'G': case 'H': case 'I':
			case 'J': case 'K': case 'L': case 'M': /* N */   case 'O': case 'P': case 'Q': case 'R':
			case 'S': case 'T': case 'U': case 'V': case 'W': /* X */	case 'Y': case 'Z':
			case '_':
			case '0': case '1': case '2': case '3': case '4':
			case '5': case '6': case '7': case '8': case '9':
				break;

			case '\\': // universal character name
				markPhase3();
				switch (nextCharPhase3()) {
				case 'u': case 'U':
					length++;
					break;
				default:
					restorePhase3();
					isPartOfIdentifier= false;
					break;
				}
				break;

			case END_OF_INPUT:
				if (fSupportContentAssist) {
					tokenKind= IToken.tCOMPLETION;
				}
				isPartOfIdentifier= false;
				break;
			case ' ': case '\t': case 0xb: case '\f': case '\r': case '\n':
				isPartOfIdentifier= false;
				break;

			case '$':
				isPartOfIdentifier= fOptions.fSupportDollarInIdentifiers;
				break;
			case '@':
				isPartOfIdentifier= fOptions.fSupportAtSignInIdentifiers;
				break;

			case '.':
				/*
				 * Pro * C INCLUDE during processing is not to separate the token "."
				 */
				if (!fInsideIncludeDirective) {
					isPartOfIdentifier= false;
				}
				break;
			case '{': case '}': case '[': case ']': case '#': case '(': case ')': case '<': case '>':
			case '%': case ':': case ';': /* . */	case '?': case '*': case '+': case '-': case '/':
			case '^': case '&': case '|': case '~': case '!': case '=': case ',': case '"': case '\'':
				isPartOfIdentifier= false;
				break;

			default:
				isPartOfIdentifier= Character.isUnicodeIdentifierPart((char) c);
				break;
			}

			if (!isPartOfIdentifier) {
				break;
			}

			length++;
			c= nextCharPhase3();
		}

		if (tokenKind == IToken.tIDENTIFIER && fInsideIncludeDirective) {
			/*
			 * Pro*C INCLUDE header
			 */
			tokenKind = tQUOTE_HEADER_NAME;
		}

		return newToken(tokenKind, start, length);
	}
}
