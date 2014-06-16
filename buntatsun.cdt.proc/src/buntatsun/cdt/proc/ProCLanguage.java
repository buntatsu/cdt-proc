package buntatsun.cdt.proc;

import org.eclipse.cdt.core.dom.ILinkage;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.parser.AbstractCLikeLanguage;
import org.eclipse.cdt.core.dom.parser.IScannerExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.ISourceCodeParser;
import org.eclipse.cdt.core.dom.parser.c.GCCParserExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.c.GCCScannerExtensionConfiguration;
import org.eclipse.cdt.core.dom.parser.c.ICParserExtensionConfiguration;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.parser.ExtendedScannerInfo;
import org.eclipse.cdt.core.parser.FileContent;
import org.eclipse.cdt.core.parser.IParserLogService;
import org.eclipse.cdt.core.parser.IParserSettings;
import org.eclipse.cdt.core.parser.IScanner;
import org.eclipse.cdt.core.parser.IScannerInfo;
import org.eclipse.cdt.core.parser.IncludeFileContentProvider;
import org.eclipse.cdt.core.parser.ParserLanguage;
import org.eclipse.cdt.core.parser.ParserMode;
import org.eclipse.cdt.core.parser.ParserUtil;
import org.eclipse.cdt.internal.core.util.ICancelable;
import org.eclipse.cdt.internal.core.util.ICanceler;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

@SuppressWarnings("restriction")
public class ProCLanguage extends AbstractCLikeLanguage {
	protected static final GCCScannerExtensionConfiguration PROC_SCANNER_EXTENSION
	= ProCScannerExtensionConfiguration.getInstance();

	protected static final GCCParserExtensionConfiguration PROC_PARSER_EXTENSION
	= GCCParserExtensionConfiguration.getInstance();

	public static final String ID = Activator.PLUGIN_ID + ".proc";

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public int getLinkageID() {
		// memo - getLinkageID
		// C_LINKAGE_IDでないと、PreProcessorがincludeを展開してくれない.
		// CPP_LINKAGE_ID でもだめ.
//		return ProCLinkage.PROC_LINKAGE_ID;
//		return ILinkage.CPP_LINKAGE_ID;
		return ILinkage.C_LINKAGE_ID;
	}

	protected ICParserExtensionConfiguration getParserExtensionConfiguration() {
		return PROC_PARSER_EXTENSION;
	}

	@Override
	protected ISourceCodeParser createParser(IScanner scanner,
			ParserMode parserMode, IParserLogService logService, IIndex index) {
		return new ProCSourceParser(scanner, parserMode, logService,
				getParserExtensionConfiguration(), index);
	}

	@Override
	protected IScannerExtensionConfiguration getScannerExtensionConfiguration() {
		return PROC_SCANNER_EXTENSION;
	}


	@Override
	protected IScannerExtensionConfiguration getScannerExtensionConfiguration(IScannerInfo info) {
		return ProCScannerExtensionConfiguration.getInstance(info);
	}

	@Override
	protected ParserLanguage getParserLanguage() {
        return ParserLanguage.C;
	}


	protected IScanner mycreateScanner(
			FileContent content, IScannerInfo scanInfo, IncludeFileContentProvider fcp, IParserLogService log) {
		/*
		 * gccのプロジェクト設定(include path,マクロ定義etc)を元に
		 * ScannerInfoを作成してProCPreprocessorに渡す
		 */
		String afile = content.getFileLocation();
		IResource res = ParserUtil.getResourceForFilename(afile);
		if (res != null) {
			scanInfo =
				LanguageSettingsScannerInfoProvider.getScannerInformation(
						res, new String[]{"org.eclipse.cdt.core.gcc"});
		}
		return new ProCPreprocessor(
				content, scanInfo, getParserLanguage(), log, getScannerExtensionConfiguration(scanInfo), fcp);
	}


	@Override
	public IASTTranslationUnit getASTTranslationUnit(FileContent reader, IScannerInfo scanInfo,
			IncludeFileContentProvider fileCreator, IIndex index, int options, IParserLogService log)
			throws CoreException {

		//final IScanner scanner= createScanner(reader, scanInfo, fileCreator, log);
		final IScanner scanner= mycreateScanner(reader, scanInfo, fileCreator, log);
		scanner.setComputeImageLocations((options & OPTION_NO_IMAGE_LOCATIONS) == 0);
		scanner.setProcessInactiveCode((options & OPTION_PARSE_INACTIVE_CODE) != 0);

		IParserSettings parserSettings= null;
		if (scanInfo instanceof ExtendedScannerInfo) {
			ExtendedScannerInfo extendedScannerInfo = (ExtendedScannerInfo) scanInfo;
			parserSettings = extendedScannerInfo.getParserSettings();
		}
		final ISourceCodeParser parser= createParser(scanner, log, index, false, options, parserSettings);

		// Make it possible to cancel parser by reconciler - http://bugs.eclipse.org/226682
		ICanceler canceler= null;
		if (log instanceof ICanceler) {
			canceler= (ICanceler) log;
			canceler.setCancelable(new ICancelable() {
				@Override
				public void cancel() {
					scanner.cancel();
					parser.cancel();
				}});
		}

		try {
			// Parse
			IASTTranslationUnit ast= parser.parse();
			ast.setIsHeaderUnit((options & OPTION_IS_SOURCE_UNIT) == 0);
			return ast;
		} finally {
			if (canceler != null) {
				canceler.setCancelable(null);
			}
		}
	}
}
