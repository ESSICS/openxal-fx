/*
 * Copyright 2018 claudiorosati.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.europeanspallationsource.xaos.ui.components;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import se.europeanspallationsource.xaos.ui.TreeItems;
import se.europeanspallationsource.xaos.util.io.DeleteFileVisitor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static se.europeanspallationsource.xaos.ui.components.TreeDirectoryMonitorTest.ChangeSource.EXTERNAL;
import static se.europeanspallationsource.xaos.ui.components.TreeDirectoryMonitorTest.ChangeSource.INTERNAL;


/**
 * @author claudio.rosati@esss.se
 */
@SuppressWarnings( { "ClassWithoutLogger", "UseOfSystemOutOrSystemErr" } )
public class TreeDirectoryMonitorTest extends ApplicationTest {

	@BeforeClass
	public static void setUpClass() {
		System.out.println("---- TreeDirectoryMonitorTest ----------------------------------");
	}

	private Path dir_a;
	private Path dir_a_c;
	private Path dir_b;
	private ExecutorService executor;
	private Path file_a;
	private Path file_a_c;
	private Path file_b1;
	private Path file_b2;
	private TreeDirectoryMonitor<ChangeSource, Path> monitor;
	private Path root;
	private TreeItem<Path> rootItem;
	private TreeView<Path> view;

	@Before
	public void setUp() throws IOException {

		executor = Executors.newSingleThreadExecutor();
		root = Files.createTempDirectory("TDM_");
			dir_a = Files.createTempDirectory(root, "TDM_a_");
				file_a = Files.createTempFile(dir_a, "TDM_a_", ".test");
				dir_a_c = Files.createTempDirectory(dir_a, "TDM_a_c_");
					file_a_c = Files.createTempFile(dir_a_c, "TDM_a_c_", ".test");
			dir_b = Files.createTempDirectory(root, "TDM_b_");
				file_b1 = Files.createTempFile(dir_b, "TDM_b1_", ".test");
				file_b2 = Files.createTempFile(dir_b, "TDM_b2_", ".test");

//		System.out.println(MessageFormat.format(
//			"  Testing 'DirectoryWatcher'\n"
//			+ "    created directories:\n"
//			+ "      {0}\n"
//			+ "      {1}\n"
//			+ "      {2}\n"
//			+ "      {3}\n"
//			+ "    created files:\n"
//			+ "      {4}\n"
//			+ "      {5}\n"
//			+ "      {6}\n"
//			+ "      {7}",
//			root,
//			dir_a,
//			dir_a_c,
//			dir_b,
//			file_a,
//			file_a_c,
//			file_b1,
//			file_b2
//		));
//
	}

	@Override
	public void start( Stage stage ) throws IOException {

		monitor = TreeDirectoryMonitor.build(EXTERNAL);
		rootItem = monitor.model().getRoot();

		rootItem.setExpanded(true);

		view = new TreeView<>(rootItem);

		view.setId("tree");
		view.setShowRoot(false);
		view.setCellFactory(TreeItems.defaultTreePathCellFactory());

		stage.setOnCloseRequest(event -> monitor.dispose());
		stage.setScene(new Scene(view, 800, 500));
		stage.show();

	}

	@After
	public void tearDown() throws TimeoutException, IOException, InterruptedException {
		FxToolkit.cleanupStages();
		Files.walkFileTree(root, new DeleteFileVisitor());
		executor.shutdown();
	}

	/**
	 * Test of addTopLevelDirectory method, of class TreeDirectoryMonitor.
	 *
	 * @throws java.lang.InterruptedException
	 */
	@Test
	public void testAddTopLevelDirectory() throws InterruptedException {

		System.out.println(MessageFormat.format("  Testing ''addTopLevelDirectory'' [on {0}]...", root));

		CountDownLatch latch = new CountDownLatch(1);
		EventHandler<TreeItem.TreeModificationEvent<Path>> eventHandler = event -> {
			Platform.runLater(() -> TreeItems.expandAll(rootItem, true));
			latch.countDown();
		};

		rootItem.addEventHandler(TreeItem.childrenModificationEvent(), eventHandler);
		executor.execute(() -> monitor.addTopLevelDirectory(root));

		if ( !latch.await(1, TimeUnit.MINUTES) ) {
			fail("Root node addition not completed in 1 minute.");
		}

	}

	/**
	 * Test of creating directories.
	 * 
	 * @throws java.lang.InterruptedException
	 * @throws java.io.IOException
	 */
	@Test
	public void testCreateDirectories() throws InterruptedException, IOException {

		System.out.println(MessageFormat.format("  Testing directories creation [on {0}]...", root));

		//	INTERNAL creation.
		Path toBeInternallyCreated1 = FileSystems.getDefault().getPath(dir_a.toString(), "dir_a_y");
		Path toBeInternallyCreated2 = FileSystems.getDefault().getPath(toBeInternallyCreated1.toString(), "dir_a_z");
		CountDownLatch latchInternal = new CountDownLatch(2);
		EventHandler<TreeItem.TreeModificationEvent<Path>> eventHandler = event -> {
			Platform.runLater(() -> { 
				
				TreeItems.expandAll(rootItem, true);

				if ( event.wasAdded()
				  && ( toBeInternallyCreated1.equals(event.getAddedChildren().get(0).getValue())
					|| toBeInternallyCreated2.equals(event.getAddedChildren().get(0).getValue()) ) ) {
					latchInternal.countDown();
				}

			});
		};

		rootItem.addEventHandler(TreeItem.childrenModificationEvent(), eventHandler);
		monitor.addTopLevelDirectory(root);

		executor.execute(() -> {
			try {
				monitor.io().createDirectories(toBeInternallyCreated2, INTERNAL).toCompletableFuture().get();
			} catch ( InterruptedException | ExecutionException ex ) {
				fail(ex.getMessage());
			}
		});

		if ( !latchInternal.await(1, TimeUnit.MINUTES) ) {
			fail("Directory creation not completed in 1 minute.");
		}

		assertTrue(monitor.model().contains(toBeInternallyCreated1));
		assertTrue(monitor.model().contains(toBeInternallyCreated2));
		assertThat(view.getTreeItem(2).getValue()).isEqualTo(toBeInternallyCreated1);
		assertThat(view.getTreeItem(3).getValue()).isEqualTo(toBeInternallyCreated2);

		//	EXTERNAL creation.
		Path toBeExternallyCreated1 = FileSystems.getDefault().getPath(dir_a.toString(), "dir_a_w");
		Path toBeExternallyCreated2 = FileSystems.getDefault().getPath(toBeExternallyCreated1.toString(), "dir_a_x");

		CountDownLatch latchExternal = new CountDownLatch(1);

		monitor.model().creations().subscribeFor(2, u -> {
			if ( !u.getInitiator().equals(EXTERNAL) ) {
				fail("Wrong initiatir: should have been INTERNAL, was " + u.getInitiator());
			} else {
				latchExternal.countDown();
			}
		});

		Files.createDirectories(toBeExternallyCreated2);

		if ( !latchExternal.await(1, TimeUnit.MINUTES) ) {
			fail("Directory creation not completed in 1 minute.");
		}

Thread.sleep(10000);

		assertTrue(monitor.model().contains(toBeExternallyCreated1));
		assertTrue(monitor.model().contains(toBeExternallyCreated2));
		assertThat(view.getTreeItem(2).getValue()).isEqualTo(toBeExternallyCreated1);
		assertThat(view.getTreeItem(3).getValue()).isEqualTo(toBeExternallyCreated2);

	}

	/**
	 * Test of creating a directory.
	 *
	 * @throws java.lang.InterruptedException
	 * @throws java.io.IOException
	 */
	@Test
	public void testCreateDirectory() throws InterruptedException, IOException {

		System.out.println(MessageFormat.format("  Testing directory creation [on {0}]...", root));

		//	INTERNAL creation.
		Path toBeInternallyCreated = FileSystems.getDefault().getPath(dir_a.toString(), "dir_a_z");
		CountDownLatch latchInternal = new CountDownLatch(1);
		EventHandler<TreeItem.TreeModificationEvent<Path>> eventHandler = event -> {
			Platform.runLater(() -> { 

				TreeItems.expandAll(rootItem, true);

				if ( event.wasAdded() && toBeInternallyCreated.equals(event.getAddedChildren().get(0).getValue()) ) {
					latchInternal.countDown();
				}

			});
		};

		rootItem.addEventHandler(TreeItem.childrenModificationEvent(), eventHandler);
		monitor.addTopLevelDirectory(root);

		executor.execute(() -> {
			try {
				monitor.io().createDirectory(toBeInternallyCreated, INTERNAL).toCompletableFuture().get();
			} catch ( InterruptedException | ExecutionException ex ) {
				fail(ex.getMessage());
			}
		});

		if ( !latchInternal.await(1, TimeUnit.MINUTES) ) {
			fail("Directory creation not completed in 1 minute.");
		}

		assertTrue(monitor.model().contains(toBeInternallyCreated));
		assertThat(view.getTreeItem(2).getValue()).isEqualTo(toBeInternallyCreated);

		//	EXTERNAL creation.
		Path toBeExternallyCreated = FileSystems.getDefault().getPath(dir_a.toString(), "dir_a_y");

		CountDownLatch latchExternal = new CountDownLatch(1);

		monitor.model().creations().subscribeForOne(u -> {
			if ( !u.getInitiator().equals(EXTERNAL) ) {
				fail("Wrong initiatir: should have been INTERNAL, was " + u.getInitiator());
			} else {
				latchExternal.countDown();
			}
		});

		Files.createDirectory(toBeExternallyCreated);

		if ( !latchExternal.await(1, TimeUnit.MINUTES) ) {
			fail("Directory creation not completed in 1 minute.");
		}

		assertTrue(monitor.model().contains(toBeExternallyCreated));
		assertThat(view.getTreeItem(2).getValue()).isEqualTo(toBeExternallyCreated);

	}

	@SuppressWarnings( "PackageVisibleInnerClass" )
	enum ChangeSource {
		INTERNAL,
		EXTERNAL
	}

}
