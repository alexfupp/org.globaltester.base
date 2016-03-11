package org.globaltester.base.ui;

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;
import org.globaltester.logging.logger.GtErrorLogger;

public class GtUiHelper {

	private static String presetDialogResult = null;

	public static void openInEditor(IFile file) {

		try {
			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();

			IDE.openEditor(page, file);
		} catch (PartInitException e) {
			// log Exception to eclipse log
			GtErrorLogger.log(Activator.PLUGIN_ID, e);

			// users most probably will ignore this behavior and open editor
			// manually, so do not open annoying dialog
		}
	}

	/**
	 * Extracts all elements of the selection that are of type defined by
	 * paramter type.
	 * 
	 * @param iSel
	 *            Selection to search in
	 * @param type
	 *            type of expected elements
	 * @return List containing all elements of given type, this might be empty
	 *         but won't be null
	 */
	@SuppressWarnings("unchecked")
	public static <T extends IResource> LinkedList<T> getSelectedIResources(
			ISelection iSel, Class<T> type) {
		LinkedList<T> selectedResources = new LinkedList<T>();

		// check type of selection
		if (iSel instanceof IStructuredSelection) {
			Iterator<?> selectionIter = ((IStructuredSelection) iSel)
					.iterator();
			while (selectionIter.hasNext()) {
				Object curElem = (Object) selectionIter.next();
				if (type.isInstance(curElem)) {
					selectedResources.add((T) curElem);
				}
			}
		}

		return selectedResources;
	}

	/**
	 * Searches all wizard registries to find a wizard matching the given ID and
	 * opens it.
	 * 
	 * @param string
	 * @throws CoreException
	 */
	public static void openWizard(String wizardId) throws CoreException {
		IWizardDescriptor descriptor = findNewWizard(wizardId);
		if (descriptor == null) {
			descriptor = findImportWizard(wizardId);
		}
		if (descriptor == null) {
			descriptor = findExportWizard(wizardId);
		}

		openWizard(descriptor);

	}

	private static IWizardDescriptor findNewWizard(String wizardId) {
		IWizardRegistry registry = PlatformUI.getWorkbench()
				.getNewWizardRegistry();
		return registry.findWizard(wizardId);
	}

	private static IWizardDescriptor findImportWizard(String wizardId) {
		IWizardRegistry registry = PlatformUI.getWorkbench()
				.getImportWizardRegistry();
		return registry.findWizard(wizardId);
	}

	private static IWizardDescriptor findExportWizard(String wizardId) {
		IWizardRegistry registry = PlatformUI.getWorkbench()
				.getExportWizardRegistry();
		return registry.findWizard(wizardId);
	}

	private static void openWizard(IWizardDescriptor descriptor)
			throws CoreException {
		if (descriptor != null) {
			IWizard wizard = descriptor.createWizard();
			WizardDialog wd = new WizardDialog(PlatformUI.getWorkbench()
					.getDisplay().getActiveShell(), wizard);
			wd.setTitle(wizard.getWindowTitle());
			wd.setBlockOnOpen(true);
			wd.open();
		}
	}

	public static void openErrorDialog(Shell shell, String msg) {
		MessageBox dialog = new MessageBox(shell, SWT.APPLICATION_MODAL | SWT.ICON_ERROR);
		dialog.setMessage(msg);
		dialog.open();
		
	}

	/**
	 * This method allows to open a native dialog in a way that is compatible to
	 * SWTBot UI-testing.
	 * 
	 * @param shell
	 * @param options
	 * @return
	 */
	public static String openFileDialog(Shell shell, DialogOptions options){
		if (presetDialogResult  == null){
			FileDialog dialog = new FileDialog(shell);
			dialog.setFileName(options.getFileName());
			dialog.setFilterExtensions(options.getFilterExtensions());
			dialog.setFilterIndex(options.getFilterIndex());
			dialog.setFilterNames(options.getFilterNames());
			dialog.setFilterPath(options.getFilterPath());
			dialog.setOverwrite(options.getOverwrite());
			if (options.getText() != null){
				dialog.setText(options.getText());	
			}
			return dialog.open();
		}
		return resetAndReturn();
	}

	/**
	 * This method allows to open a native dialog in a way that is compatible to
	 * SWTBot UI-testing.
	 * 
	 * @param shell
	 * @param options
	 * @return
	 */
	public static String openDirectoryDialog(Shell shell, DialogOptions options) {
		if (presetDialogResult  == null){
			DirectoryDialog dialog = new DirectoryDialog(shell);
			dialog.setFilterPath(options.getFilterPath());
			if (options.getText() != null){
				dialog.setText(options.getText());	
			}
			dialog.setMessage(options.getMessage());
			return dialog.open();
		}
		return resetAndReturn();
	}
	
	/**
	 * While testing this method is used to set a value that will be used
	 * instead of a dialogs result.
	 * 
	 * @param result
	 */
	public static void setDialogResultForTesting(String result){
		presetDialogResult = result;
	}
	
	private static String resetAndReturn(){
		String result = presetDialogResult;
		presetDialogResult = null;
		return result;
	}
}
