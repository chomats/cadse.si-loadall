/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package fr.imag.adele.fede.workspace.si.loadall;

import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import fr.imag.adele.cadse.as.platformide.IPlatformIDE;
import fr.imag.adele.cadse.core.CadseDomain;
import fr.imag.adele.cadse.core.CadseException;
import fr.imag.adele.cadse.core.CadseRuntime;
import fr.imag.adele.cadse.core.LogicalWorkspace;
import fr.imag.adele.cadse.core.WSModelState;
import fr.imag.adele.fede.workspace.as.initmodel.ErrorWhenLoadedModel;
import fr.imag.adele.fede.workspace.as.initmodel.IInitModel;
import fr.imag.adele.fede.workspace.as.persistence.IPersistence;

/**
 * The Class LoadJob.
 * 
 * @author <a href="mailto:stephane.chomat@imag.fr">Stephane Chomat</a>
 */
public class LoadJob {

	public static final String	CADSES_TO_EXECUTE	= "fr.imag.adele.cadse.execute";

	/** The Constant PLUGIN_ID. */
	public static final String	PLUGIN_ID			= "fede.tool.workspace.view";	//$NON-NLS-1$

	/**
	 * Load workspace.
	 * 
	 * @param et
	 *            the et
	 * 
	 * 
	 */
	public static void loadWorkspace(final ILoadAllService et) {
		Thread t = new Thread("load new ws") {
			@Override
			public void run() {
				try {
					loadWorkspaceInThread(et);
				} catch (CadseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t.start();

	}

	/**
	 * Adds the workspace model.
	 * 
	 * @throws ErrorWhenLoadedModel
	 *             the error when loaded model
	 */
	public static void addWorkspaceModel(ILoadAllService eclipseService) throws CadseException, ErrorWhenLoadedModel {
		final fr.imag.adele.fede.workspace.as.initmodel.IInitModel im;
		im = eclipseService.getInitModelService();
		CadseRuntime[] sCadsesNameToLoad = eclipseService.openDialog(false);
		if (sCadsesNameToLoad != null) {
			im.executeCadses(sCadsesNameToLoad);
		}
	}

	/**
	 * Load new ws.
	 * 
	 * @param et
	 *            the et
	 * @param wsPersitence
	 *            the ws persitence
	 * @param im
	 *            the im
	 * @param wsDomain
	 *            the ws domain
	 * @param askToErase
	 *            the ask to erase
	 * 
	 * @throws CadseException
	 *             the melusine exception
	 * @throws ErrorWhenLoadedModel
	 *             the error when loaded model
	 */
	public static void loadNewWS(ILoadAllService et, CadseDomain wsDomain, boolean askToErase) throws CadseException,
			ErrorWhenLoadedModel {
		// wsDomain.endOperation();// release le lock en attendant que le ui est
		// demarrer
		et.getPlatformIDE().waitUI();
		// wsDomain.beginOperation("loadNewWS");
		CadseRuntime[] sCadsesNameToLoad = et.openDialog(false);
		if (sCadsesNameToLoad != null) {

			IPersistence wsPersitence = et.getPersistenceService();
			if (askToErase) {
				wsPersitence.delete();
			}
			IInitModel im = et.getInitModelService();
			// load model
			im.executeCadses(sCadsesNameToLoad);
			wsDomain = et.getCadseDomain();
			// find the current model
			LogicalWorkspace theWorkspaceLogique = wsDomain.getLogicalWorkspace();

			theWorkspaceLogique.setState(WSModelState.LOAD);
			theWorkspaceLogique.setState(WSModelState.RUN);
			try {
				// IWorkbenchWindow win =
				// PlatformUI.getWorkbench().getActiveWorkbenchWindow();

				final IWorkbench workbench = PlatformUI.getWorkbench();
				Runnable r = new Runnable() {
					public void run() {
						IWorkbenchWindow[] win = workbench.getWorkbenchWindows();
						if (win != null && win.length == 1) {
							IWorkbenchPage page;
							try {
								page = workbench.showPerspective("fede.tool.workspace.workspacePerspective", win[0]);
								win[0].setActivePage(page);
							} catch (WorkbenchException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
				};
				final Display currentDisplay = workbench.getDisplay();
				currentDisplay.asyncExec(r);

			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sprintf.
	 * 
	 * @param a
	 *            the a
	 * @param format
	 *            the format
	 * @param args
	 *            the args
	 */
	private static void sprintf(Appendable a, String format, Object... args) {
		Formatter formatter = new Formatter(a);
		formatter.format(Locale.getDefault(), format, args);

	}

	/**
	 * Load workspace in thread.
	 * 
	 * @param et
	 *            the et
	 * 
	 * @throws CadseException
	 *             the melusine exception
	 * @throws ErrorWhenLoadedModel
	 *             the error when loaded model
	 */
	private static void loadWorkspaceInThread(final ILoadAllService et) throws CadseException, ErrorWhenLoadedModel {
		sprintf(System.out, "Begin load : %tT", new Date());

		IPlatformIDE platformIDE = et.getPlatformIDE();
		if (platformIDE == null) {
			throw new ErrorWhenLoadedModel("Can't find the Ide service !!");
		}
		platformIDE.getLocation(true);

		final IPersistence wsPersitence = et.getPersistenceService();

		final IInitModel im = et.getInitModelService();
		if (im == null) {
			throw new ErrorWhenLoadedModel("Can't find the Init model service !!");
		}

		// load all cadse
		CadseRuntime[] cadsePresents = im.loadCadses();

		// find cadse to run
		HashMap<String, CadseRuntime> crByName = new HashMap<String, CadseRuntime>();
		for (CadseRuntime cr : cadsePresents) {
			crByName.put(cr.getName(), cr);
		}

		String[] cadsesNameSaved = wsPersitence.getCadsesName();
		HashSet<CadseRuntime> toRun = new HashSet<CadseRuntime>();

		if (cadsesNameSaved != null) {
			for (String name : cadsesNameSaved) {
				name = name.trim();
				if (name.length() == 0) {
					continue;
				}
				CadseRuntime cr = crByName.get(name);
				if (cr != null) {
					toRun.add(cr);
				}
			}
		}
		// look the properties CADSES_TO_EXECUTE
		String addCadseName = System.getProperty(CADSES_TO_EXECUTE);
		if (addCadseName != null) {
			String[] addCadseNameArray = addCadseName.split(",");

			for (String name : addCadseNameArray) {
				name = name.trim();
				if (name.length() == 0) {
					continue;
				}
				CadseRuntime cr = crByName.get(name);
				if (cr != null) {
					toRun.add(cr);
				} else {
					System.err.println("*** NOT FOUND CADSE " + name + " ***");
				}
			}
		}
		// String type = wsPersitence.getWorkspaceTypeName();
		final CadseDomain wsDomain = et.getCadseDomain();
		// wsDomain.beginOperation("Eclipse.loadWorkspace");
		try {
			if (toRun.size() == 0) {
				loadNewWS(et, wsDomain, false);
			} else {
				// load model
				im.executeCadses(toRun.toArray(new CadseRuntime[toRun.size()]));

				// cadsesNameSaved
				LogicalWorkspace theCurrentModel = wsDomain.getLogicalWorkspace();

				theCurrentModel.setState(WSModelState.LOAD);
				// load persistence
				if (cadsesNameSaved != null) {
					// if exits cadse to load
					wsPersitence.load();
				}
				theCurrentModel.setState(WSModelState.RUN);
			}

		} finally {
			// wsDomain.endOperation();

		}
	}
}