package com.example.e4.rcp.todo.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.example.e4.rcp.todo.events.MyEventConstants;
import com.example.e4.rcp.todo.model.ITodoService;
import com.example.e4.rcp.todo.model.Todo;

public class ManagerPart {

	private Button btnNewButton;
	private TableViewer viewer;

	@Inject
	private UISynchronize sync;

	@Inject
	private ESelectionService selectionService;

	@Inject
	private ITodoService todoService;

	@PostConstruct
	public void createControls(Composite parent, final MWindow window) {
		parent.setLayout(new GridLayout(1, false));

		btnNewButton = new Button(parent, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				todoService.getTodos(viewer::setInput);
			}
		});

		btnNewButton.setText("Load Data");

		Text search = new Text(parent, SWT.SEARCH | SWT.CANCEL
				| SWT.ICON_SEARCH);
		search.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false,
				1, 1));
		search.setMessage("Filter");
		search.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (e.detail == SWT.CANCEL) {
					Text text = (Text) e.getSource();
					text.setText("");
					//
				}
			}
			// MORE...
		});

		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = viewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(ArrayContentProvider.getInstance());
		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);

		column.getColumn().setWidth(100);
		column.getColumn().setText("Summary");
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Todo todo = (Todo) element;
				return todo.getSummary();
			}

		});

		column.setEditingSupport(new EditingSupport(viewer) {

			@Override
			protected void setValue(Object element, Object value) {
				Todo todo = (Todo) element;
				todo.setSummary(String.valueOf(value));
				viewer.refresh();
			}

			@Override
			protected Object getValue(Object element) {
				Todo todo = (Todo) element;
				return todo.getSummary();
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return new TextCellEditor(viewer.getTable(), SWT.NONE);
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		column = new TableViewerColumn(viewer, SWT.NONE);

		column.getColumn().setWidth(100);
		column.getColumn().setText("Description");
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				Todo todo = (Todo) element;
				return todo.getDescription();
			}
		});

		viewer.addSelectionChangedListener(event -> {
			IStructuredSelection selection = viewer
					.getStructuredSelection();
			selectionService.setSelection(selection.getFirstElement());
		});

	}

	@Inject
	@Optional
	private void getNotified(
			@UIEventTopic(MyEventConstants.TOPIC_TODO_ALLTOPICS) String topic) {
		if (viewer != null) {
			todoService.getTodos(viewer::setInput);
		}
	}

	@Focus
	private void setFocus() {
		btnNewButton.setFocus();
	}

}
