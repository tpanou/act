package org.teiath.web.vm.act;

import org.apache.log4j.Logger;
import org.teiath.data.domain.act.Event;
import org.teiath.data.domain.act.EventCategory;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.EventSearchCriteria;
import org.teiath.service.act.SearchEventsService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.web.reports.common.Report;
import org.teiath.web.reports.common.ReportToolkit;
import org.teiath.web.reports.common.ReportType;
import org.teiath.web.session.ZKSession;
import org.teiath.web.util.MessageBuilder;
import org.teiath.web.util.PageURL;
import org.teiath.web.vm.BaseVM;
import org.teiath.web.vm.user.values.EventCategoryRenderer;
import org.zkoss.bind.BindContext;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zul.*;

import java.util.*;

public class SearchActionVM
		extends BaseVM {

	static Logger log = Logger.getLogger(SearchActionVM.class.getName());

	@Wire("#paging")
	private Intbox paging;
	@Wire("#eventsListbox")
	private Listbox eventsListbox;
	@Wire("#toolbar")
	private Div toolbar;
	@Wire("#tree")
	private Tree tree;

	@WireVariable
	private SearchEventsService searchEventsService;

	private EventSearchCriteria eventSearchCriteria;
	private ListModelList<Event> eventsList;
	private Event selectedEvent;
	private Set<DefaultTreeNode<EventCategory>> selectedItems = new HashSet<DefaultTreeNode<EventCategory>>();

	@AfterCompose
	public void afterCompose(
			@ContextParam(ContextType.VIEW)
			Component view) {
		Selectors.wireComponents(view, this, false);
		Selectors.wireEventListeners(view, this);

		eventsListbox.getPagingChild().setMold("os");
		eventsListbox.getPagingChild().setAutohide(false);
		eventsListbox.getPagingChild().setDetailed(true);
		eventsList = new ListModelList<>();

		if (ZKSession.getAttribute("eventSearchCriteria") != null) {
			eventSearchCriteria = (EventSearchCriteria) ZKSession.getAttribute("eventSearchCriteria");
			SearchResults<Event> results = null;
			try {
				results = searchEventsService.searchEvents(eventSearchCriteria);
				Collection<Event> events = results.getData();
				eventsList.addAll(events);
				eventsListbox.setPageSize(results.getTotalRecords());
				eventsListbox.setActivePage(eventSearchCriteria.getPageNumber());
				if (results.getTotalRecords() != 0) {
					eventsListbox.setVisible(true);
					paging.setVisible(true);
					toolbar.setVisible(true);
				} else {
					eventsListbox.setVisible(false);
					paging.setVisible(false);
					toolbar.setVisible(false);
				}
				ZKSession.removeAttribute("eventSearchCriteria");
			} catch (ServiceException e) {
				log.error(e.getMessage());
				Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
						Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
			}
		} else {
			//Initial search criteria
			eventSearchCriteria = new EventSearchCriteria();
			eventSearchCriteria.setPageSize(paging.getValue());
			eventSearchCriteria.setPageNumber(0);
			eventSearchCriteria.setDisabledAccess(null);
			eventsListbox.setPageSize(paging.getValue());
		}

		Collection<EventCategory> parentalCategories = null;

		try {
			parentalCategories = searchEventsService.getEventCategoriesByParentId(1000);
			DefaultTreeModel model = new DefaultTreeModel(new DefaultTreeNode("ROOT", createTree(parentalCategories)));
			model.setMultiple(true);
			tree.setModel(model);
			tree.setItemRenderer(new EventCategoryRenderer());
		} catch (ServiceException e) {
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
	}

	public List createTree(Collection<EventCategory> children) {
		List childrenList = new ArrayList();

		if ((children != null) && (! children.isEmpty())) {

			for (EventCategory category : children) {

				if (category.getSubCategories() != null) {
					childrenList.add(new DefaultTreeNode(category, createTree(category.getSubCategories())));
				} else {
					childrenList.add(new DefaultTreeNode(category));
				}
			}
		}

		return childrenList;
	}

	@Listen("onSelect = #tree")
	public void onSelect(SelectEvent<Treeitem, String> event) {
		Treeitem ref = event.getReference();
		if (ref.isSelected()) {
			recursiveCheck((TreeNode<EventCategory>) ref.getValue());
		} else {
			recursiveUnCheck((TreeNode<EventCategory>) ref.getValue());
		}
	}

	public void recursiveCheck(TreeNode<EventCategory> treeNode) {
		for (TreeNode<EventCategory> node : treeNode.getChildren()) {
			selectedItems.add((DefaultTreeNode<EventCategory>) node);
			recursiveCheck(node);
		}
	}

	public void recursiveUnCheck(TreeNode<EventCategory> treeNode) {
		for (TreeNode<EventCategory> node : treeNode.getChildren()) {
			selectedItems.removeAll(treeNode.getChildren());
			recursiveUnCheck(node);
		}
	}

	@Command
	@NotifyChange("selectedEvent")
	public void onSearch() {
		selectedEvent = null;
		eventsList.clear();
		eventSearchCriteria.setPageNumber(0);
		eventSearchCriteria.setPageSize(paging.getValue());
		eventSearchCriteria.setEventStatus(3);
		eventsListbox.setPageSize(paging.getValue());
		eventSearchCriteria.setEventCategories(new HashSet<EventCategory>());

		if (selectedItems != null) {
			for (DefaultTreeNode defaultTreeNode : selectedItems) {
				EventCategory eventCategory = (EventCategory) defaultTreeNode.getData();
				eventSearchCriteria.getEventCategories().add(eventCategory);
			}
		}

		try {
			SearchResults<Event> results = searchEventsService.searchEvents(eventSearchCriteria);
			Collection<Event> events = results.getData();
			eventsList.addAll(events);
			if (results.getTotalRecords() != 0) {
				eventsListbox.setVisible(true);
				paging.setVisible(true);
				toolbar.setVisible(true);
			} else {
				eventsListbox.setVisible(false);
				paging.setVisible(false);
				toolbar.setVisible(false);
				Messagebox.show(MessageBuilder
						.buildErrorMessage(Labels.getLabel("action.notFound"), Labels.getLabel("action")),
						Labels.getLabel("common.messages.search"), Messagebox.OK, Messagebox.INFORMATION);
			}
			//			paging.setTotalSize(results.getTotalRecords());
			//			paging.setActivePage(eventSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	@NotifyChange({"selectedEvent", "eventSearchCriteria"})
	public void onResetSearch() {
		selectedEvent = null;
		eventSearchCriteria.setEventKeyword(null);
		eventSearchCriteria.setDateFrom(null);
		eventSearchCriteria.setDateTo(null);
		eventSearchCriteria.setEventCategories(null);
		eventSearchCriteria.setDisabledAccess(null);
		eventSearchCriteria.setCode(null);
		selectedItems = null;
		paging.setVisible(false);
		tree.clearSelection();
	}

	@Command
	@NotifyChange
	public void onSort(BindContext ctx) {
		org.zkoss.zk.ui.event.Event event = ctx.getTriggerEvent();
		Listheader listheader = (Listheader) event.getTarget();

		eventSearchCriteria.setOrderField(listheader.getId());
		eventSearchCriteria.setOrderDirection(listheader.getSortDirection());
		eventSearchCriteria.setPageNumber(0);
		selectedEvent = null;
		eventsList.clear();

		try {
			SearchResults<Event> results = searchEventsService.searchEvents(eventSearchCriteria);
			Collection<Event> events = results.getData();
			eventsList.addAll(events);
			//			paging.setTotalSize(results.getTotalRecords());
			//			paging.setActivePage(eventSearchCriteria.getPageNumber());
		} catch (ServiceException e) {
			log.error(e.getMessage());
			Messagebox.show(MessageBuilder.buildErrorMessage(e.getMessage(), Labels.getLabel("action")),
					Labels.getLabel("common.messages.read_title"), Messagebox.OK, Messagebox.ERROR);
		}
	}

	@Command
	public void onPrintPDF() {
		SearchResults<Event> results = null;
		eventSearchCriteria.setPageNumber(0);
		eventSearchCriteria.setPageSize(0);
		try {
			results = searchEventsService.searchEvents(eventSearchCriteria);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		Collection<Event> events = results.getData();

		Report report = ReportToolkit.requestEventsReport(events, ReportType.PDF);
		ZKSession.setAttribute("REPORT", report);
		ZKSession.sendPureRedirect(
				"/reportsServlet?zsessid=" + ZKSession.getCurrentWinID() + "&" + ZKSession.getPWSParams(), "_self");
	}

	@Command
	public void onView() {
		if (selectedEvent != null) {
			ZKSession.setAttribute("eventSearchCriteria", eventSearchCriteria);
			ZKSession.setAttribute("eventId", selectedEvent.getId());
			ZKSession.sendRedirect(PageURL.SEARCH_ACTION_VIEW);
		}
	}

	public EventSearchCriteria getEventSearchCriteria() {
		return eventSearchCriteria;
	}

	public void setEventSearchCriteria(EventSearchCriteria eventSearchCriteria) {
		this.eventSearchCriteria = eventSearchCriteria;
	}

	public ListModelList<Event> getEventsList() {
		return eventsList;
	}

	public void setEventsList(ListModelList<Event> eventsList) {
		this.eventsList = eventsList;
	}

	public Event getSelectedEvent() {
		return selectedEvent;
	}

	public void setSelectedEvent(Event selectedEvent) {
		this.selectedEvent = selectedEvent;
	}

	public Set<DefaultTreeNode<EventCategory>> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(Set<DefaultTreeNode<EventCategory>> selectedItems) {
		this.selectedItems = selectedItems;
	}
}
