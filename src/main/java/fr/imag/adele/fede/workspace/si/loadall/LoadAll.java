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

import java.util.logging.Logger;

import fr.imag.adele.cadse.as.platformide.IPlatformIDE;
import fr.imag.adele.cadse.core.CadseDomain;
import fr.imag.adele.cadse.core.CadseRuntime;
import fr.imag.adele.cadse.workspace.as.loadservice.LoadService;
import fr.imag.adele.fede.workspace.as.initmodel.IInitModel;
import fr.imag.adele.fede.workspace.as.persistence.IPersistence;

/**
 * @generated
 */
public class LoadAll implements ILoadAllService {

	static ILoadAllService	INSTANCE;
	/**
	 * @generated
	 */
	CadseDomain				workspaceCU;

	/**
	 * @generated
	 */
	IInitModel				loadCadseModelService;
	/**
	 * @generated
	 */
	IPersistence			persistenceService;

	/**
	 * @generated
	 */
	IPlatformIDE		platformIde;
	
	LoadService[]		loadServices;

	public void start() {
		INSTANCE = this;
		Logger mLogger = Logger.getLogger("SI.Workspace.LoadAll");
		mLogger.info("begin load before");
		mLogger.info("start");
		if (getCadseDomain() == null) {
			mLogger.severe("Workspace core is null!!");
		} else {
			mLogger.info("Workspace logique " + getCadseDomain().getLogicalWorkspace());
		}
		mLogger.info("begin load");
		LoadJob.loadWorkspace(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.fede.workspace.si.loadall.ILoadAllService#getPersistenceService()
	 */
	public IPersistence getPersistenceService() {
		return persistenceService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.fede.workspace.si.loadall.ILoadAllService#getInitModelService()
	 */
	public IInitModel getInitModelService() {
		return loadCadseModelService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.fede.workspace.si.loadall.ILoadAllService#getWorkspaceDomain()
	 */
	public CadseDomain getCadseDomain() {
		return workspaceCU;
	}

	//public static ILoadAllService getInstance() {
	//	return INSTANCE;
	//}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.fede.workspace.si.loadall.ILoadAllService#getPlatformEclipseService()
	 */
	public IPlatformIDE getPlatformIDE() {
		return platformIde;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.imag.adele.fede.workspace.si.loadall.ILoadAllService#openDialog(boolean)
	 */
	public CadseRuntime[] openDialog(boolean askToErase) {
		return platformIde.openDialog(askToErase);
	}
	
	
	public LoadService[] getLoadServices() {
		return loadServices;
	}
}
