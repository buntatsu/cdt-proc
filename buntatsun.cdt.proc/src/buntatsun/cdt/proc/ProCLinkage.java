package buntatsun.cdt.proc;

import org.eclipse.cdt.core.dom.ILinkage;

public class ProCLinkage implements ILinkage {
	final static String PROC_LINKAGE_NAME = "Pro*C";
	final static int PROC_LINKAGE_ID = 99;

	@Override
	public String getLinkageName() {
		return PROC_LINKAGE_NAME;
	}

	@Override
	public int getLinkageID() {
		return PROC_LINKAGE_ID;
	}

}
