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
package ts.eclipse.ide.angular2.internal.cli.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import ts.eclipse.ide.angular2.cli.NgBlueprint;
import ts.eclipse.ide.angular2.internal.cli.AngularCLIMessages;
import ts.eclipse.ide.angular2.internal.cli.json.AngularCLIJson;

/**
 * Wizard page for Angular2 Class.
 *
 */
public class NewNgClassWizardPage extends NgGenerateBlueprintWizardPage {

	private static final String PAGE_NAME = "ngClass";

	private Button chkSpec;

	protected NewNgClassWizardPage(IContainer folder) {
		super(PAGE_NAME, AngularCLIMessages.NewNgClassWizardPage_title, null, NgBlueprint.CLASS, folder);
		super.setDescription(AngularCLIMessages.NewNgClassWizardPage_description);
	}

	@Override
	public void createParamsControl(Composite parent) {
		super.createParamsControl(parent);
		Font font = parent.getFont();

		// params group
		Composite paramsGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		paramsGroup.setLayout(layout);
		paramsGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
		paramsGroup.setFont(font);

		// Checkbox for spec
		chkSpec = new Button(paramsGroup, SWT.CHECK);
		chkSpec.addListener(SWT.Selection, this);
		chkSpec.setText(AngularCLIMessages.NgGenerateBlueprintWizardPage_generate_spec);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		chkSpec.setLayoutData(data);
	}

	@Override
	protected void initializePage() {
		super.initializePage();
		chkSpec.setSelection(getAngularCLIJson().isSpec(NgBlueprint.CLASS));
	}

	@Override
	protected String[] getGeneratedFilesImpl() {
		AngularCLIJson cliJson = getAngularCLIJson();
		String name = getBlueprintName();
		int cnt = isSpec() ? 2 : 1;
		String[] files = new String[cnt];
		files[0] = cliJson.getTsFileName(name);
		if (isSpec())
			files[1] = cliJson.getSpecFileName(name);
		return files;
	}

	public boolean isSpec() {
		return chkSpec.getSelection();
	}

}
