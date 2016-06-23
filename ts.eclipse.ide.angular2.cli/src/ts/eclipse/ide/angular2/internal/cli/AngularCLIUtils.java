package ts.eclipse.ide.angular2.internal.cli;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ISetSelectionTarget;

import ts.eclipse.ide.angular2.cli.AngularCLIPlugin;

public class AngularCLIUtils {

	private static final String ANGULAR_CLI_JSON = "angular-cli.json";

	public static IStatus refreshProjectAndOpenAngularCLIJson(File projectDir, IProgressMonitor monitor) {
		// Create Eclipse Project
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = projectAlreadyExistsInWorkspace(projectDir, workspaceRoot);
		if (project == null) {
			try {
				project = createOrImportProject(projectDir, workspaceRoot, monitor);
			} catch (Exception e) {
				return new Status(IStatus.ERROR, AngularCLIPlugin.PLUGIN_ID,
						AngularCLIMessages.AbstractProjectCommandInterpreter_error, e);
			}
		}
		return refreshProjectAndOpenAngularCLIJson(project, monitor);
	}

	public static IStatus refreshProjectAndOpenAngularCLIJson(IProject project, IProgressMonitor monitor) {
		try {
			if (monitor.isCanceled()) {
				return new Status(IStatus.CANCEL, AngularCLIPlugin.PLUGIN_ID,
						AngularCLIMessages.AbstractProjectCommandInterpreter_error);
			}
			project.open(IResource.BACKGROUND_REFRESH, monitor);
			if (monitor.isCanceled()) {
				return new Status(IStatus.CANCEL, AngularCLIPlugin.PLUGIN_ID,
						AngularCLIMessages.AbstractProjectCommandInterpreter_error);
			}
			project.refreshLocal(IResource.DEPTH_ZERO, monitor);

			// Open angular-cli.json in an Editor
			IFile angularCliJsonFile = project.getFile(ANGULAR_CLI_JSON);
			if (!angularCliJsonFile.exists()) {
				angularCliJsonFile.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			}
			if (angularCliJsonFile.exists()) {

				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				final IViewPart view = page.findView(IPageLayout.ID_PROJECT_EXPLORER);
				((ISetSelectionTarget) view).selectReveal(new StructuredSelection(angularCliJsonFile));

				IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry()
						.getDefaultEditor(angularCliJsonFile.getName());
				page.openEditor(new FileEditorInput(angularCliJsonFile), desc.getId());
			}
		} catch (CoreException e) {
			return new Status(IStatus.ERROR, AngularCLIPlugin.PLUGIN_ID,
					AngularCLIMessages.AbstractProjectCommandInterpreter_error, e);
		}
		return Status.OK_STATUS;
	}
	// Code copied from
	// org.eclipse.ui.internal.wizards.datatransfer.SmartImportJob

	private static IProject projectAlreadyExistsInWorkspace(File directory, IWorkspaceRoot workspaceRoot) {
		for (IProject project : workspaceRoot.getProjects()) {
			if (project.getLocation().toFile().getAbsoluteFile().equals(directory.getAbsoluteFile())) {
				return project;
			}
		}
		return null;
	}

	private static IProject createOrImportProject(File directory, IWorkspaceRoot workspaceRoot,
			IProgressMonitor progressMonitor) throws Exception {
		IProjectDescription desc = null;
		File expectedProjectDescriptionFile = new File(directory, IProjectDescription.DESCRIPTION_FILE_NAME);
		if (expectedProjectDescriptionFile.exists()) {
			desc = ResourcesPlugin.getWorkspace()
					.loadProjectDescription(new Path(expectedProjectDescriptionFile.getAbsolutePath()));
			String expectedName = desc.getName();
			IProject projectWithSameName = workspaceRoot.getProject(expectedName);
			if (projectWithSameName.exists()) {
				if (projectWithSameName.getLocation().toFile().equals(directory)) {
					// project seems already there
					return projectWithSameName;
				}
				throw new Exception(NLS.bind(
						AngularCLIMessages.AbstractProjectCommandInterpreter_anotherProjectWithSameNameExists_description,
						expectedName));
			}
		} else {
			String projectName = directory.getName();
			if (workspaceRoot.getProject(directory.getName()).exists()) {
				int i = 1;
				do {
					projectName = directory.getName() + '(' + i + ')';
					i++;
				} while (workspaceRoot.getProject(projectName).exists());
			}

			desc = ResourcesPlugin.getWorkspace().newProjectDescription(projectName);
		}
		desc.setLocation(new Path(directory.getAbsolutePath()));
		IProject res = workspaceRoot.getProject(desc.getName());
		res.create(desc, progressMonitor);
		return res;
	}
}
