/**
 *  Copyright (c) 2015-2016 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *  
 */
package ts.eclipse.ide.angular2.internal.cli.terminal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;

import ts.eclipse.ide.angular2.internal.cli.jobs.NgGenerateJob;
import ts.eclipse.ide.terminal.interpreter.AbstractCommandInterpreter;

/**
 * "ng generate ..." interpreter to refresh the generated resources.
 *
 */
public class NgGenerateCommandInterpreter extends AbstractCommandInterpreter {

	private static final String CREATE = "create";

	private final Collection<String> fileNames;

	private boolean lastLineCreate;

	public NgGenerateCommandInterpreter(List<String> parameters, String workingDir) {
		super(parameters, workingDir);
		this.fileNames = new ArrayList<String>();
		this.lastLineCreate = false;
	}

	@Override
	public void execute(List<String> parameters, String workingDir) {
		final IContainer[] c = ResourcesPlugin.getWorkspace().getRoot().findContainersForLocation(new Path(workingDir));
		if (c != null && c.length > 0) {
			// Refresh generated files and select it in the Project Explorer.
			NgGenerateJob job = new NgGenerateJob(fileNames, c[0]);
			job.setRule(ResourcesPlugin.getWorkspace().getRoot());
			job.schedule();
		}

	}

	@Override
	public void onTrace(String line) {
		line = line.trim();
		if (lastLineCreate) {
			String filename = line.trim();
			fileNames.add(filename);
			lastLineCreate = false;
		} else {
			lastLineCreate = line.startsWith(CREATE);
		}
	}

}
