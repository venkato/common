package net.sf.jremoterun.utilities.nonjdk.eclipse.osint;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;

public class OpenFileUtils1 {

	// mapping ext to editor <Ext,EditorId>
	public static Map<String, String> extMap = new HashMap();

	private static final Logger log = Logger.getLogger(OpenFileUtils1.class.getName());

	public static final IFileSystem fileSystem = EFS.getLocalFileSystem();

	private static Exception throwable;

	private static final Object lock = new Object();

	public static void openFileAndSelectInRse(final File file, final String extention) throws Exception {
		throw new UnsupportedOperationException("");
//        Display.getDefault().asyncExec(new Runnable() {
//
//            public void run() {
//                try {
//                    final String name = file.getName();
//                    if (file.isFile() && !name.endsWith(".jar")
//                            && !name.endsWith(".zip")) {
//                        openFileOnly(file, extention);
//                    }
//                    RSEFileSystemUtils.selectFileInRSE(file);
//                    OSIntegrationStartup.workBenchToFront();
//                } catch (final Exception e) {
//                    log.log(Level.WARNING, "openFileAndSelectInRse", e);
//                    throwable = e;
//                }
//            }
//        });
//        synchronized (lock) {
//            if (throwable != null) {
//                final Exception e1 = throwable;
//                throwable = null;
//                log.severe(e1 + "");
//                throw e1;
//            }
//        }

	}

	public static void setException(final Exception throwable) {
		synchronized (lock) {
			if (OpenFileUtils1.throwable != null) {
				OpenFileUtils1.throwable = throwable;
			}

		}
	}

	public static String getExtension(final String filename) {
		final int extensionPos = filename.lastIndexOf('.');
		return filename.substring(extensionPos + 1);
	}

	public static void openFileOnly(final File file, String extention) throws Exception {
		final String extention2 = getExtension(file.getName());
		String edId = extMap.get(extention2);
		if (extention == null && edId != null) {
		} else {
			if (extention == null) {
				extention = file.getName();
			} else {
				if (!extention.contains(".")) {
					extention = "." + extention;
				}
			}
			final IEditorDescriptor editorDescriptor = PlatformUI.getWorkbench().getEditorRegistry()
					.getDefaultEditor(extention);
			if (editorDescriptor == null) {
				edId = OSIntegrationStartup1.txtEditorId;
			} else {
				edId = editorDescriptor.getId();
			}
		}
		openFileOnly2(file, edId);
	}

	public static IFile tryResolveFile(final File file) throws Exception {
		URI uri1 = file.toURI();

		IFile[] findFilesForLocationURI = ResourcesPlugin.getWorkspace().getRoot().findFilesForLocationURI(uri1);
		if (findFilesForLocationURI != null && findFilesForLocationURI.length > 0) {
			return findFilesForLocationURI[0];
		}
		return null;
	}

	public static void openFileOnly2(final File file, final String editorId) throws Exception {
		IFile fileE = tryResolveFile(file);
		if (fileE != null) {
			FileEditorInput fileEditorInput1 = new org.eclipse.ui.part.FileEditorInput(fileE);
			OSIntegrationStartup1.getWorkbenchPage().openEditor(fileEditorInput1, editorId);
		} else {
			final IFileStore fileStore;
			fileStore = fileSystem.fromLocalFile(file);
			final FileStoreEditorInput editorInput = new FileStoreEditorInput(fileStore);
			OSIntegrationStartup1.getWorkbenchPage().openEditor(editorInput, editorId);
		}
		OSIntegrationStartup1.workBenchToFront();

	}

}
