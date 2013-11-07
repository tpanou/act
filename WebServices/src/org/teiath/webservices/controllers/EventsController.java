package org.teiath.webservices.controllers;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.teiath.data.domain.act.Event;
import org.teiath.data.paging.SearchResults;
import org.teiath.data.search.EventSearchCriteria;
import org.teiath.service.act.SearchEventsService;
import org.teiath.service.exceptions.ServiceException;
import org.teiath.webservices.model.ServiceEvent;
import org.teiath.webservices.model.ServiceEventList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@Controller
public class EventsController {

	@Autowired
	private SearchEventsService searchEventsService;

	private static final Logger logger_c = Logger.getLogger(EventsController.class);

	@RequestMapping(value = "/services/events", method = RequestMethod.GET)
	public ServiceEventList searchEvents(String category, String dFrom, String dTo, Boolean dAccess, String keyword) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat time = new SimpleDateFormat("HH:mm");

		ServiceEventList serviceEventList = new ServiceEventList();
		serviceEventList.setServiceEvents(new ArrayList<ServiceEvent>());
		ServiceEvent serviceEvent = null;

		EventSearchCriteria criteria = new EventSearchCriteria();
		try {
			criteria.setEventCategoryName(category);
			criteria.setDateFrom(dFrom != null ? sdf.parse(dFrom) : null);
			criteria.setDateTo(dTo != null ? sdf.parse(dTo) : null);
			criteria.setEventKeyword(keyword);
			criteria.setDisabledAccess(dAccess != null ? dAccess ? true : false : null);

			criteria.setPageNumber(0);
			criteria.setPageSize(Integer.MAX_VALUE);

			SearchResults<Event> results = searchEventsService.searchEvents(criteria);

			for (Event event : results.getData()) {
				serviceEvent = new ServiceEvent();
				serviceEvent.setTitle(event.getEventTitle());
				serviceEvent.setDescription(event.getEventDescription());
				serviceEvent.setEventCategoryName(event.getEventCategory().getName());
				serviceEvent.setDateFrom(event.getDateFrom());
				serviceEvent.setDateTo(event.getDateTo());
				serviceEvent.setPlace(event.getEventLocation());
				serviceEvent.setDisabledAccess(event.isDisabledAccess());
				serviceEvent.setParticipantNumber(event.getInterests());
				serviceEvent.setEventCreationDate(event.getEventCreationDate());

				serviceEventList.getServiceEvents().add(serviceEvent);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		logger_c.debug("Returing Routes: " + serviceEvent);
		return serviceEventList;
	}

	@RequestMapping(value = "/services/event", method = RequestMethod.GET)
	public ServiceEvent searchEventByCode(String code) {

		ServiceEvent serviceEvent = new ServiceEvent();
		try {

			Event event = searchEventsService.getEventByCode(code);
			serviceEvent.setTitle(event.getEventTitle());
			serviceEvent.setDescription(event.getEventDescription());
			serviceEvent.setEventCategoryName(event.getEventCategory().getName());
			serviceEvent.setDateFrom(event.getDateFrom());
			serviceEvent.setDateTo(event.getDateTo());
			serviceEvent.setPlace(event.getEventLocation());
			serviceEvent.setDisabledAccess(event.isDisabledAccess());
			serviceEvent.setParticipantNumber(event.getInterests());
			serviceEvent.setEventCreationDate(event.getEventCreationDate());
			serviceEvent.setCode(event.getCode());
		} catch (ServiceException e) {
			e.printStackTrace();
		}

		logger_c.debug("Returing Event: " + serviceEvent);

		return serviceEvent;
	}
}
