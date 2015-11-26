package buntatsun.cdt.proc.ui.preferences;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.cdt.internal.corext.util.Messages;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import buntatsun.cdt.proc.ui.UIActivator;

@SuppressWarnings("restriction")
public class KeywordTab {
	IPreferenceStore store;
	String prefName;
	String fileName;
	String title;

	int keywordKind;
	ListViewer listViewer;
	Button btnAdd;
	Button btnDelete;
	Button btnImport;
	Button btnExport;

	public KeywordTab(int keywordKind, IPreferenceStore store
			, String prefName, String title, String fileName) {
		this.keywordKind = keywordKind;
		this.store = store;
		this.prefName = prefName;
		this.title = title;
		this.fileName = fileName;
	}

	private void setupList(ListViewer list, String value) {
		list.getList().removeAll();
		StringTokenizer st = new StringTokenizer(value, PreferenceConstants.PREF_STORE_DELIMITER);
		while (st.hasMoreTokens()) {
			String key = st.nextToken();
			if (!key.isEmpty()) {
				list.add(key);
			}
		}
	}

	private void initializeValues() {
		setupList(listViewer, store.getString(prefName));
	}

	public void initializeDefaults() {
		setupList(listViewer, store.getDefaultString(prefName));
	}

	public Control createTabContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(2, false));

		listViewer = new ListViewer(container, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
		listViewer.setSorter(new  ViewerSorter());
		List listKeywords = listViewer.getList();
		GridData gd_listKeywords = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_listKeywords.widthHint = 171;
		gd_listKeywords.heightHint = 298;
		listKeywords.setLayoutData(gd_listKeywords);

		Composite composite = new Composite(container, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_composite.heightHint = 249;
		gd_composite.widthHint = 120;
		composite.setLayoutData(gd_composite);

		btnAdd = new Button(composite, SWT.NONE);
		btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		btnAdd.setText("Add");
		btnAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				add(btnAdd.getShell());
			}
		});

		btnDelete = new Button(composite, SWT.NONE);
		btnDelete.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		btnDelete.setText("Delete");
		btnDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				remove(btnDelete.getShell());
			}
		});

		btnImport = new Button(composite, SWT.NONE);
		GridData gd_btnImport = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_btnImport.widthHint = 104;
		btnImport.setLayoutData(gd_btnImport);
		btnImport.setText("Import");
		btnImport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				importFile(btnImport.getShell());
			}
		});

		btnExport = new Button(composite, SWT.NONE);
		btnExport.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		btnExport.setText("Export");
		btnExport.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				exportFile(btnExport.getShell());
			}
		});

		initializeValues();

		return container;
	}

	private void add(Shell shell) {
		InputDialog id = new InputDialog(shell, "Input " + title, title, "", new IInputValidator() {
			public String isValid(String newText) {
				String s = newText.toUpperCase();
				int index = listViewer.getList().indexOf(s);
				if (index >= 0) {
					listViewer.getList().setSelection(index);
					return "The " + title + " is exists";
				}
				return null;
			}
		});

		id.setBlockOnOpen(true);
		if (id.open() == InputDialog.OK) {
			String s = id.getValue().toUpperCase();
			listViewer.add(s);
			listViewer.getList().setSelection(listViewer.getList().indexOf(s));
		}
	}

	private void remove(Shell shell) {
		IStructuredSelection sel = listViewer.getStructuredSelection();
		listViewer.remove(sel.toArray());
	}

	private void importFile(Shell shell) {
		FileDialog fd= new FileDialog(shell);
		fd.setText("Import " + title);
		fd.setFilterExtensions(new String[] {"*.txt", "*.*"});
		fd.setFileName(fileName);

		String path = fd.open();
		if (path == null) {
			return;
		}

		try {
			File file = new File(path);

			if (!file.exists()) {
				return;
			}

			InputStream is = new BufferedInputStream(new FileInputStream(file));
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			String line;
			Set<String> keywords = new HashSet<String>();
			while ((line = br.readLine()) != null) {
				String k = line.trim().toUpperCase();
				if (!k.isEmpty()) {
					keywords.add(k);
				}
			}
			br.close();

			listViewer.getList().removeAll();
			if (!keywords.isEmpty()) {
				listViewer.add(keywords.toArray());
			}

		} catch (FileNotFoundException e) {
			openReadErrorDialog(shell, e);
		} catch (IOException e) {
			openReadErrorDialog(shell, e);
		}
	}

	private void openReadErrorDialog(Shell shell, Exception e) {
		String message = e.getLocalizedMessage();
		if (message != null) {
			message = Messages.format(PreferenceConstants.MSG_IMPORT_ERROR1, message);
		}
		else {
			message = PreferenceConstants.MSG_IMPORT_ERROR0;
		}
		MessageDialog.openError(shell, "Import " + title, message);
	}

	private void exportFile(Shell shell) {
		FileDialog fd= new FileDialog(shell, SWT.SAVE);
		fd.setText("Export " + title);
		fd.setFilterExtensions(new String[] {"*.txt", "*.*"});
		fd.setFileName(fileName);

		String path = fd.open();
		if (path == null) {
			return;
		}

		File file = new File(path);

		if (file.isHidden()) {
			return;
		}

		if (file.exists() && !file.canWrite()) {
			return;
		}

		if (!file.exists() || confirmOverwrite(shell, file)) {
			OutputStream os = null;
			try {
				os = new BufferedOutputStream(new FileOutputStream(file));
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);

				String[] list = listViewer.getList().getItems();
				for (int i = 0; i < list.length; i++) {
					bw.write(list[i]);
					bw.newLine();
				}
				bw.close();
				os.close();
			} catch (IOException e) {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e2) {
						// ignore
					}
				}
				openWriteErrorDialog(shell);
			}
		}
	}

	private void openWriteErrorDialog(Shell shell) {
		MessageDialog.openError(shell
				, "Export " + title
				, PreferenceConstants.MSG_EXPORT_ERROR);
	}

	private boolean confirmOverwrite(Shell shell, File file) {
		return MessageDialog.openQuestion(shell
				, "Export " + title
				, Messages.format(PreferenceConstants.MSG_EXPORT_OVERWRITE
				, file.getAbsolutePath()));
	}

	public void storeValues() {
		List list = listViewer.getList();
		String items[] = list.getItems();
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < items.length; i++) {
			sb.append(items[i] + PreferenceConstants.PREF_STORE_DELIMITER);
		}

		UIActivator.getDefault().replaceKeyword(keywordKind, list.getItems());

		store.setValue(prefName, sb.toString());
	}
}
